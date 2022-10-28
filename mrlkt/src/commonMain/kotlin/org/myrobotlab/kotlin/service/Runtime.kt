package org.myrobotlab.kotlin.service

import org.myrobotlab.kotlin.framework.Registration
import org.myrobotlab.kotlin.framework.Service
import org.myrobotlab.kotlin.utils.ImmutableMapWrapper

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
        register(Registration(this))
    }


    fun register(registration: Registration) {
        mutableRegistry[registration.name] = registration
    }
}