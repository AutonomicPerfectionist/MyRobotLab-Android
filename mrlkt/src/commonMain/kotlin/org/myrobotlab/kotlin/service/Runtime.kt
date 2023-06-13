package org.myrobotlab.kotlin.service

import kotlinx.coroutines.coroutineScope
import org.myrobotlab.kotlin.framework.*
import org.myrobotlab.kotlin.framework.MrlClient.remoteId
import org.myrobotlab.kotlin.framework.MrlClient.serde
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.constructService
import org.myrobotlab.kotlin.utils.ImmutableMapWrapper
import kotlin.reflect.KClass

/**
 * Core runtime service that handles service
 * registration and connection handshake procedures.
 * This service is not marked with [org.myrobotlab.kotlin.annotations.MrlService]
 * to ensure it is not exposed as a user-startable
 * service.
 */
object Runtime: Service("runtime") {
    /**
     * The ID of this mrlkt instance, i.e. `"mrlkt"`.
     * [initRuntime] should be called to change this
     * ID, and registry entries will be updated when
     * such a call is made.
     */
    var runtimeID: String = "mrlkt"
    private set(new) {

        mutableRegistry.entries.forEach { entry ->
            if (entry.value.id == field)
                entry.setValue(entry.value.copy(id=new))
        }
        field = new
    }

    /**
     * The service registry that is only mutable
     * within this [Runtime] service. It is exposed
     * through [registry] via a read-only wrapper.
     */
    private val mutableRegistry = mutableMapOf<String, Registration>()

    /**
     * The current registry of services known to MrlKt.
     * In order to add new elements, one must call [register]
     */
    val registry: Map<String, Registration> = ImmutableMapWrapper(mutableRegistry)

    /**
     * Initialize the runtime service with
     * an mrlkt runtime [id], i.e. `"android"`.
     * This method *must* be called before initiating
     * a connection with [MrlClient.connectCoroutine] or
     * its blocking variant [MrlClient.connect].
     *
     * TODO use lambdas with receivers to make [MrlClient.connectCoroutine]
     *  only available within an [initRuntime] call to prevent
     *  order of calls errors
     */
    fun initRuntime(id: String) {
        require(!MrlClient.connected) {"Runtime may only be initialized while disconnected"}
        runtimeID = id
        mutableRegistry["runtime"] = Registration(runtimeID, "runtime", "org.myrobotlab.service.Runtime")
    }

    /**
     * Register a newly-discovered/created service
     * described by [registration]
     * to [registry] and inform interested parties
     * by invoking [registered].
     */
    suspend fun register(registration: Registration) {
        MrlClient.logger.info("Registering: ${registration.name}")
        mutableRegistry[registration.name] = registration
        invoke<Registration>("registered", registration)
    }

    /**
     * Publishing point for when a new service is registered.
     * Services wishing to be notified when this occurs
     * should subscribe to this method, *not*
     * [register].
     */
    fun registered(registration: Registration): Registration {
        MrlClient.logger.info("Registered service: ${registration.name}@${registration.id}")
        return registration
    }

    /**
     * Get a description of this mrlkt instance. This
     * method's Java equivalent is used as part of the handshake
     * procedure.
     */
    fun describe(uuid: String, query: DescribeQuery?): DescribeResults {
        MrlClient.logger.info("Calling describe")
        if(query != null)
            MrlClient.remoteId = query.id
        return DescribeResults(runtimeID, "", DescribeQuery(), null, null, registry.values.toList())
    }

    /**
     * Start a new service with the provided name. Which
     * service is started is determined by [R]
     *
     * @param R The Service type to create and start
     * @param name The name of the new service
     */
    suspend inline fun <reified R: Service> start(name: String): R? {
        return start(name, R::class)
    }

    /**
     * Start a new service of the provided type with the provided name.
     *
     * @param name The name of the new service.
     * @param type Which service class to instantiate.
     */
    suspend fun <R : ServiceInterface> start(name: String, type: KClass<R>): R? = coroutineScope {
        MrlClient.logger.info("Starting service $name")
        val old = Runtime.registry[name]?.service ?: this.run {
            MrlClient.logger.info("Creating service")
            val service = type.constructService(name)
            service.startService(this)
            register(Registration(service))
            return@run service

        }
        if (old::class == type) {
            return@coroutineScope old as R
        } else {
            return@coroutineScope null
        }
    }

    /**
     * Add a listener, the subscriber will be notifed when
     * the topic method is invoked. Additionally,
     * [MrlClient.connected] will be set to true
     * when the listener topic method is `"registered"`
     * and the callback name is `"runtime"`, as this defines
     * the end of the handshake sequence.
     */
    override fun addListener(listener: MRLListener) {

        if (listener.topicMethod == "registered" && listener.callbackName == "runtime@$remoteId") {
            // Fix for incorrect callback name
            super.addListener(listener.copy(callbackName = "runtime@$remoteId"))
            MrlClient.connected = true
        } else {
            super.addListener(listener)
        }
    }
}