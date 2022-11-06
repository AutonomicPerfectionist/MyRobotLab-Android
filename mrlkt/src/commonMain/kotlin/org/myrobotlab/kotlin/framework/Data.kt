package org.myrobotlab.kotlin.framework

import io.ktor.util.date.*
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.service.Runtime.runtimeID

@MrlClassMapping("org.myrobotlab.framework.Message")
data class Message(
    val name: String,
    val method: String,
    val data: List<Any?>,
    val sender: String = "",
    val sendingMethod: String = "",
    val historyList: List<String> = listOf(),
    val status: String = "",
    val encoding: String = "",
    val msgId: Long = getTimeMillis()

)

@MrlClassMapping("org.myrobotlab.framework.Registration")
data class Registration (
    val id: String,
    val name: String,
    val typeKey: String,
    val state: String = "",
    val interfaces: List<String> = listOf(),
    val service: ServiceInterface? = null
) {
    constructor(service: ServiceInterface): this(
        runtimeID, service.name, "kt:${service::class.qualifiedName}", service = service)

}

@MrlClassMapping("org.myrobotlab.framework.DescribeResults")
data class DescribeResults(
    val id: String,
    val uuid: String,
    val request: Map<String, Any?>,
    val platform: Any?,
    val status: String,
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