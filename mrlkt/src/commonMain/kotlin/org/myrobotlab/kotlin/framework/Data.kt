package org.myrobotlab.kotlin.framework

import io.ktor.util.date.*
import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.codec.JsonDelegate
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.mrlInterfaceNames
import org.myrobotlab.kotlin.service.Runtime.runtimeID
import kotlin.jvm.Transient

/**
 * The fundamental unit of MRL's
 * pub/sub system. A [Message] represents
 * either a synchronous or an asynchronous
 * request to execute a [method] of some service
 * given by [name] with the given [data] as parameters.
 * A message also contains information
 * on where it's been routed through via the [historyList]
 * and which [sender] service and [sendingMethod] sent the message.
 * The [msgId] defines when this message was created, which
 * is useful for small latency corrections.
 */
@MrlClassMapping("org.myrobotlab.framework.Message")
data class Message(
    val name: String,
    val method: String,
    val data: List<Any?>,
    val sender: String = "",
    val sendingMethod: String = "",
    val historyList: List<String> = listOf(),
    val status: String? = null,
    val encoding: String = "",
    val msgId: Long = getTimeMillis()

)

/**
 * Defines a registration for a service,
 * which is used by [org.myrobotlab.kotlin.service.Runtime]
 * and its Java equivalent to manage the list of currently
 * running services. The [id] points to the Runtime ID
 * that the service belongs to, while [name] and [typeKey]
 * define the service's name and its type information
 * respectively. The [state] defines the minimum JSON
 * representation required to ensure this object passes
 * deserialization on the Java side but in the future
 * will hold the serialized form of [service].
 * The [service] holds a reference to the [ServiceInterface] object
 * that this registration is for, or null if for a remote service.
 */
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
    /**
     * Delegate to provide [state] in the future, for now
     * using it causes issues with serialization.
     */
    @Transient
    private val delegate = JsonDelegate(::service)


    /**
     * Create a registration from the given [service],
     * deriving all required values from the service.
     */
    constructor(service: ServiceInterface): this(
        runtimeID,
        service.name,
        service.typeKey,
        service = service,
        interfaces = service.mrlInterfaceNames,
        state = "{\"name\": \"${service.name}\", \"id\": \"${service.id}\", \"serviceClass\": ${service.serviceClass}}"
    )

}

/**
 * The results from calling
 * [org.myrobotlab.kotlin.service.Runtime.describe]
 * and its Java equivalent, which provides information
 * on the described MRL instance. The [id] defines
 * the Runtime ID of the described instance, e.g.
 * `"android"`, [uuid] is the connection UUID (unused), [request]
 * contains a copy of the request made to the described instance's
 * describe() method, [status] is a JSON-encoded String
 * of a Java Status object identifying the status of the `describe()`
 * call, and [registrations] is the list of currently
 * registered services that the described runtime knows about.
 */
@MrlClassMapping("org.myrobotlab.framework.DescribeResults")
data class DescribeResults(
    val id: String,
    val uuid: String,
    val request: DescribeQuery,
    val platform: Any?,
    val status: String?,
    val registrations: List<Registration>
)

/**
 * A query sent to a foreign Runtime's `describe()` method.
 * The [id] is the ID of the instance making the query, the
 * [uuid] is the connection UUID (unused), and
 * the [platform] is an object containing platform data
 * about the instance making the query.
 */
@MrlClassMapping("org.myrobotlab.framework.DescribeQuery")
data class DescribeQuery (
    val id: String = runtimeID,
    val uuid: String = "",
    val platform: Any? = null
)

/**
 * Encodes information for
 * a subscription from a [topicMethod]
 * to a [callbackName] service's [callbackMethod]
 * method. The service that is being subscribed to
 * is implied by calling [Service.addListener]
 * on the target service with an object of this type.
 */
@MrlClassMapping("org.myrobotlab.framework.MRLListener")
data class MRLListener (
    val topicMethod: String,
    val callbackName: String,
    val callbackMethod: String
)