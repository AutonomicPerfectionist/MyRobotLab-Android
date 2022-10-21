package org.myrobotlab.kotlin.framework

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.myrobotlab.kotlin.framework.MrlClient.eventBus
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.callMethod
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods

@MrlClassMapping("org.myrobotlab.framework.interfaces.ServiceInterface")
interface ServiceInterface {
    val name: String

}

@MrlClassMapping("org.myrobotlab.framework.Service")
open class Service(override val name: String): ServiceInterface {
    suspend fun runInbox() = coroutineScope {
        launch {
            while (true) {
                eventBus.filter { message -> message.name == name}.collect { message->
                    if (message.method in this@Service.methods.map {method-> method.name }) {
                        val ret = this@Service.callMethod(message.method, message.data)
                    }
                }
            }
        }
    }
}