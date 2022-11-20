package org.myrobotlab.kotlin.framework

import io.ktor.util.date.*
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.mrlInterfaceNames
import org.myrobotlab.kotlin.service.Runtime.runtimeID
import kotlin.jvm.Transient

@MrlClassMapping("org.myrobotlab.framework.Message")
data class Message(
    val name: String,
    val method: String,
    val data: MutableList<Any?>,
    val sender: String = "",
    val sendingMethod: String = "",
    val historyList: List<String> = listOf(),
    val status: String? = null,
    val encoding: String = "",
    val msgId: Long = getTimeMillis()

)

@MrlClassMapping("org.myrobotlab.framework.Registration")
data class Registration (
    val id: String,
    val name: String,
    val typeKey: String,
    val state: String = "{}",
    val interfaces: List<String> = listOf("org.myrobotlab.framework.interfaces.ServiceInterface"),
    @Transient
    val service: ServiceInterface? = null
) {
    constructor(service: ServiceInterface): this(
        runtimeID, service.name, service.typeKey, service = service, interfaces = service.mrlInterfaceNames)

}

@MrlClassMapping("org.myrobotlab.framework.DescribeResults")
data class DescribeResults(
    val id: String,
    val uuid: String,
    val request: Map<String, Any?>,
    val platform: Any?,
    val status: String?,
    val registrations: List<Registration>
)

@MrlClassMapping("org.myrobotlab.framework.MRLListener")
data class MRLListener (
    val topicMethod: String,
    val callbackName: String,
    val callbackMethod: String
)

@MrlClassMapping("org.myrobotlab.framework.DescribeQuery")
data class DescribeQuery (
    val id: String,
    val uuid: String,
    val platform: Any?
)