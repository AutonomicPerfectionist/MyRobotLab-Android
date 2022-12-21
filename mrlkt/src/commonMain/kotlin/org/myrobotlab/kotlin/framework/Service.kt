package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.MrlClient.logger
import org.myrobotlab.kotlin.framework.MrlClient.sendCommand
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods
import org.myrobotlab.kotlin.framework.interfaces.NameProvider
import org.myrobotlab.kotlin.service.Runtime
import kotlin.jvm.Transient
import kotlin.reflect.KCallable

import kotlin.reflect.KFunction1
import kotlin.reflect.KType

/**
 * DSL method to provide a similar API to [ServiceMethod.subscribeTo()]
 */
infix fun <P, R> KFunction1<P, R>.subscribeTo(method: ServiceMethod) {

    TODO("Implement subscription")
}

/**
 * Represents a handle to a service method, whether
 * the service is local or remote.
 *
 * @param service The service instance this method belongs to
 * @param methodName The name of this method
 */
data class ServiceMethod(
    val service: ServiceInterface,
    val methodName: String,
    val valueParameters: List<KType>,
    val returnType: KType,
    val callable: KCallable<*>
) {
    operator fun invoke(vararg params: Any?) {
        val f = ::invoke
        println(f)

        TODO("Implement ServiceMethod.invoke()")

    }

    infix fun subscribeTo(publisher: ServiceMethod) {

    }
}

@MrlClassMapping("org.myrobotlab.framework.interfaces.ServiceInterface")
interface ServiceInterface: NameProvider {
    /**
     * The name of the service, as set by the user
     * when starting a service.
     */
    override val name: String

    /**
     * The ID of the runtime this service
     * belongs to.
     */
    val id: String

    /**
     * The full name of this service.
     * A full name combines this service's
     * name and its Runtime ID, and is equal to
     * `"$name@$id"`.
     *
     * @see name
     * @see id
     */
    val fullName: String

    /**
     * The type key of this service. A type
     * key identifies the language and class of
     * an object across process boundaries.
     * For kotlin, this type key is equal to
     * `"kt:${this::class.qualifiedName}"`.
     */
    val typeKey: String

    /**
     * The type key of this service. A type
     * key identifies the language and class of
     * an object across process boundaries.
     * For kotlin, this type key is equal to
     * `"kt:${this::class.qualifiedName}"`.
     */
    val type: String

    /**
     * Equivalent to type,
     * duplicated due to legacy reasons.
     */
    val serviceClass: String

    /**
     * A coroutine scope that this service may use to launch additional
     * coroutines.
     */
    var serviceScope: CoroutineScope

    /**
     * Launch a new coroutine within the provided scope
     * to handle message receiving.
     *
     * @param scope The scope to launch the inbox
     * coroutine in.
     */
    suspend fun runInbox(scope: CoroutineScope)

    /**
     * DSL method to get a handle to a service method
     * based on the name of the method. This allows
     * Kotlin services to provide the same DSL
     * API as proxy services.
     *
     * @param methodName The name of the method to get a handle to
     */
    operator fun get(methodName: String): ServiceMethod

    /**
     * Add a listener to this service, the given listener
     * will be notified when the topic method is invoked.
     */
    fun addListener(listener: MRLListener)

    /**
     * Add a listener by inferring the callback topic name.
     */
    fun addListener(topicMethod: String, callbackName: String)

    /**
     * Removes a listener that was previously registered.
     *
     * @param outMethod The publishing method
     * @param serviceName The name of the listening service
     * @param inMethod The callback method
     */
    fun removeListener(outMethod: String, serviceName: String, inMethod: String)

    /**
     * Invoke a method of this service using a message. The message must
     * have the same destination name as the name of this service.
     * All subscribed listeners will be notified with the results
     * of the invocation.
     *
     * @param message: The message carrying the method and parameters
     * @return The result of the method call
     */
    suspend fun <R> invoke(message: Message): R?


    /**
     * Invoke a method of this service using a method name and any arguments.
     * All subscribed listeners will be notified with the results
     * of the invocation.
     *
     * @param method: The method to invoke
     * @param data: Any parameters to pass to the method
     * @return The result of the method call
     */
    suspend fun <R> invoke(method: String, vararg data: Any?): R?

    /**
     * Starts the service's inbox coroutine as well
     * as performs any other initialization needed.
     *
     * @param scope: The scope that the inbox coroutine
     * should run in.
     */
    suspend fun startService(scope: CoroutineScope)

}

/**
 * Base service class for Kotlin services.
 *
 * This class implements all needed
 * functionality for the framework to manage it.
 * Kotlin equivalent of `org.myrobotlab.framework.Service`
 *
 * @param name The short name of the service, to be supplied by the
 * user when starting the service.
 */
@MrlClassMapping("org.myrobotlab.framework.Service")
abstract class Service(override val name: String) : KoinComponent, ServiceInterface {
    /**
     * Map of method name to listeners to that method.
     */
    private val mrlListeners = mutableMapOf<String, MutableList<MRLListener>>()

    /**
     * Map between method names and their [ServiceMethod] reference.
     *
     * FIXME This currently overwrites method overloads, switch to list of overloads
     */
    @Transient
    private val serviceMethods = methods.associateBy({ it.methodName }, { it })

    override val fullName: String
        get() = "$name@${Runtime.runtimeID}"

    override val id: String
        get() = Runtime.runtimeID

    override val typeKey: String = "kt:${this::class.qualifiedName}"

    override val type: String by ::typeKey

    override val serviceClass: String by ::type

    @Transient
    override var serviceScope: CoroutineScope = MainScope()

    override operator fun get(methodName: String): ServiceMethod =
        serviceMethods[methodName] ?: throw NoSuchElementException()

    override suspend fun runInbox(scope: CoroutineScope) {
        scope.launch {
            println("Launched")
            eventBus.filter { it.name == name || it.name == fullName }
                .takeWhile { it.method != "shutdown" }
                .collect { message ->

                    if (message.method in this@Service.methods.map { method -> method.methodName }) {
                        this@Service.invoke<Any?>(message)
                    } else {
                        logger.info("No method named ${message.method} found")
                    }
                }
        }
    }

    override fun addListener(listener: MRLListener) {
        MrlClient.logger.info("Adding listener: $listener")
        mrlListeners.getOrPut(listener.topicMethod) { mutableListOf() }.add(listener)
    }

    override fun addListener(topicMethod: String, callbackName: String) {
        val callbackTopic = if (topicMethod.startsWith("publish")) {
            "on${
                topicMethod.substring("publish".length)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }"
        } else if (topicMethod.startsWith("get")) {
            "on${
                topicMethod.substring("get".length)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }"
        } else {

            // no replacement - just pefix and capitalize
            // FIXME - subscribe to onMethod --- gets ---> onOnMethod :P
            "on${topicMethod.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}"
        }

        addListener(MRLListener(topicMethod, callbackName, callbackTopic))
    }

    override fun removeListener(outMethod: String, serviceName: String, inMethod: String) {
        mrlListeners[outMethod]?.indexOfFirst { it.callbackName == serviceName }
            ?.let { mrlListeners[outMethod]?.removeAt(it) }
    }

    override suspend fun <R> invoke(message: Message): R? {
        require(message.name == name || message.name == "$name@${Runtime.runtimeID}") { "Attempting to invoke method on incorrect service" }
        return invoke(message.method, *message.data.toTypedArray())
    }

    override suspend fun <R> invoke(method: String, vararg data: Any?): R? {
        val ret = this@Service.callMethod<R>(method, data.toList())
        mrlListeners[method]?.forEach { listener ->
            MrlClient.logger.info("Invoking for listener ${listener.callbackName}.${listener.callbackMethod}")
            val nameParts = listener.callbackName.split('@')
            if (nameParts.size <= 1 || nameParts[1] == Runtime.runtimeID)
                eventBus.emit(Message(listener.callbackName, listener.callbackMethod, listOf(ret)))
            else
                sendCommand(listener.callbackName, listener.callbackMethod, listOf(ret))

        }
        return ret
    }

    override suspend fun startService(scope: CoroutineScope) {
        runInbox(scope)
    }
}