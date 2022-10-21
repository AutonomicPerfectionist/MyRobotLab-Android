package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable
import kotlin.reflect.KClass

actual object ServiceMethodProvider {
    actual val Service.methods: List<KCallable<*>>
        get() = this::class.members as List<KCallable<*>>

    actual fun Service.callMethod(
        method: String,
        data: List<Any?>
    ): Any? {
        val serviceMethods = methods.filter { it.name == method && it.parameters.size == data.size}

        val dataTypes = data.map { it?.let { it::class } }

        var compatibleServiceMethod: KCallable<*>? = null

        for (serviceMethod in serviceMethods) {
            val params = serviceMethod.parameters
            var isCompatible = true
            for (i in params.indices) {
                val dataType = dataTypes[i]?.javaObjectType ?: continue
                val paramType = (params[i].type.classifier as KClass<*>).javaObjectType
                if (!paramType.isAssignableFrom(dataType)) {
                    isCompatible = false
                    break
                }
            }
            if (isCompatible) {
                compatibleServiceMethod = serviceMethod
                break
            }

        }

        if (compatibleServiceMethod == null) {
            throw RuntimeException("Cannot find compatible method")
        }
        return compatibleServiceMethod.call(*data.toTypedArray())


    }
}