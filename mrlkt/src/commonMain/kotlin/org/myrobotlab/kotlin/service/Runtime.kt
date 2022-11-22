package org.myrobotlab.kotlin.service

import kotlinx.coroutines.coroutineScope
import org.myrobotlab.kotlin.framework.*
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.constructService
import org.myrobotlab.kotlin.framework.ServiceMethodProvider.methods
import org.myrobotlab.kotlin.utils.ImmutableMapWrapper
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

object Runtime: Service("runtime") {
    lateinit var runtimeID: String
    private set

    private var hasInit = false

    private val mutableRegistry = mutableMapOf<String, Registration>()

    /**
     * The current registry of services known to MrlKt.
     * In order to add new elements, one must call [register]
     */
    val registry: Map<String, Registration> = ImmutableMapWrapper(mutableRegistry)

    fun initRuntime(id: String) {
        require(!hasInit) {"Runtime may only be initialized once"}
        hasInit = true
        runtimeID = id
        mutableRegistry["runtime"] = Registration(runtimeID, "runtime", "org.myrobotlab.service.Runtime")
    }


    suspend fun register(registration: Registration) {
        MrlClient.logger.info("Registering: ${registration.name}")
        mutableRegistry[registration.name] = registration
        invoke<Registration>("registered", registration)
    }

    suspend fun registered(registration: Registration): Registration {
        MrlClient.logger.info("Registered service: ${registration.name}@${registration.id}")
        return registration
    }

    fun describe(uuid: String, query: String): DescribeResults {
        MrlClient.logger.info("Calling describe")
        return DescribeResults(runtimeID, "", mapOf(), null, null, registry.values.toList())
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
            service.start(this)
            register(Registration(service))
            return@run service

        }
        if (old::class == type) {
            return@coroutineScope old as R
        } else {
            return@coroutineScope null
        }
    }

    override fun addListener(listener: MRLListener) {
        super.addListener(listener)
        if (listener.topicMethod == "registered" && listener.callbackName == "runtime") {
            MrlClient.connected = true
        }
    }
}