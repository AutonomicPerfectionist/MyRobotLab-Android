package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.MrlClient.sendCommand
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods

@MrlClassMapping("org.myrobotlab.framework.interfaces.ServiceInterface")
interface ServiceInterface {
    val name: String

}

@MrlClassMapping("org.myrobotlab.framework.Service")
abstract class Service(override val name: String) : ServiceInterface {
    val mrlListeners = mutableMapOf<String, List<MRLListener>>()

    suspend fun runInbox() = coroutineScope {
        launch {
            eventBus.filter { message -> message.name == name }.collect { message ->
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