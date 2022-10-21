package org.myrobotlab.kotlin.framework

import io.ktor.util.date.*

@MrlClassMapping("org.myrobotlab.framework.Message")
data class Message(
    val msgId: Long = getTimeMillis(),
    val name: String,
    val sender: String = "",
    val sendingMethod: String = "",
    val historyList: List<String> = listOf(),
    val status: String = "",
    val encoding: String = "",
    val method: String,
    val data: List<Any?>
)

@MrlClassMapping("org.myrobotlab.framework.Registration")
data class Registration (
    val id: String,
    val name: String,
    val typeKey: String,
    val state: String,
    val interfaces: List<String>
)

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