package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.MrlClient.sendCommand
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods

import kotlin.reflect.KFunction1

/**
 * DSL method to provide a similar API to [ServiceMethod.subscribeTo()]
 */
infix fun <P> KFunction1<P, *>.subscribeTo(method: ServiceMethod) {
    TODO("Implement subscription")
}

/**
 * Represents a handle to a service method, whether
 * the service is local or remote.
 *
 * @param service The service instance this method belongs to
 * @param methodName The name of this method
 */
data class ServiceMethod(val service: ServiceInterface, val methodName: String) {
    operator fun invoke(vararg params: Any?){
        val f = ::invoke
        println(f)

        TODO("Implement ServiceMethod.invoke()")

    }

    infix fun subscribeTo(publisher: ServiceMethod) {

    }
}

@MrlClassMapping("org.myrobotlab.framework.interfaces.ServiceInterface")
interface ServiceInterface {
    /**
     * The name of the service, as set by the user
     * when starting a service.
     */
    val name: String

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
abstract class Service(override val name: String) : ServiceInterface {
    val mrlListeners = mutableMapOf<String, MutableList<MRLListener>>()
    private val serviceMethods = methods.associateBy({it.name}, {ServiceMethod(this, it.name)})

    override operator fun get(methodName: String): ServiceMethod =
        serviceMethods[methodName] ?: throw NoSuchElementException()

    override suspend fun runInbox(scope: CoroutineScope) {
        scope.launch {
            println("Launched")
            eventBus.filter { it.name == name }.takeWhile { it.method != "shutdown" }.collect { message->

                if (message.method in this@Service.methods.map { method -> method.name }) {
                    val ret = this@Service.callMethod(message.method, message.data)
                    mrlListeners[message.method]?.forEach { listener ->
                        sendCommand(listener.callbackName, listener.callbackMethod, listOf(ret))

                    }
                }
            }
        }
    }

    override fun addListener(listener: MRLListener) {
        MrlClient.logger.info("Adding listener: $listener")
        mrlListeners.getOrPut(listener.topicMethod) { mutableListOf() }.add(listener)
    }
}