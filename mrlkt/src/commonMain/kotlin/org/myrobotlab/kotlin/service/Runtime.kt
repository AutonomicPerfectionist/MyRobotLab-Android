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
        register(Registration(runtimeID, "runtime", "org.myrobotlab.service.Runtime"))
    }


    fun register(registration: Registration) {
        mutableRegistry[registration.name] = registration
    }

    fun describe(uuid: String, query: String): DescribeResults {
        MrlClient.logger.info("Calling describe")
        return DescribeResults(runtimeID, "", mapOf(), null, null, registry.values.toList())
    }

    suspend inline fun <reified R: Service> start(name: String): R? {
        return start(name, R::class)
    }

    suspend fun <R : ServiceInterface> start(name: String, type: KClass<R>): R? = coroutineScope {
        val old = mutableRegistry.getOrPut(name) {
            val service = type.constructService(name)
            service.start(this)
            return@getOrPut Registration(runtimeID, name, "kt:${type.qualifiedName}", service = service)

        }
        if (old::class == type) {
            return@coroutineScope old.service as R
        } else {
            return@coroutineScope null
        }
    }
}