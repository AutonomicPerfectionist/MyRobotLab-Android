package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.MrlClient.sendCommand
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods

import kotlin.reflect.KFunction1

infix fun <P> KFunction1<P, *>.subscribeTo(method: ServiceMethod) {
    TODO("Implement subscription")
}

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
    val name: String

    suspend fun runInbox(scope: CoroutineScope)

    operator fun get(methodName: String): ServiceMethod



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
    val mrlListeners = mutableMapOf<String, List<MRLListener>>()
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
}