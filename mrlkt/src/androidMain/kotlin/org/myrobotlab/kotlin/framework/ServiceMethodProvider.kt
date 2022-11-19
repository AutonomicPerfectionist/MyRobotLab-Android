package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters

actual object ServiceMethodProvider {
    actual val Service.methods: List<KCallable<*>>
        get() = this::class.members as List<KCallable<*>>

    actual fun <R> Service.callMethod(
        method: String,
        data: List<Any?>
    ): R? {
        val serviceMethods = methods.filter { it.name == method && it.valueParameters.size == data.size}
        val dataTypes = data.map { it?.let { it::class } }

        var compatibleServiceMethod: KCallable<*>? = null

        for (serviceMethod in serviceMethods) {
            val params = serviceMethod.valueParameters
            var isCompatible = true
            for (i in params.indices) {
                val dataType = dataTypes[i]?.javaObjectType
                    ?: if (params[i].type.isMarkedNullable)
                        continue
                    else {
                        isCompatible = false
                        break
                    }
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
        // TODO Test whether receiver is needed, test whether method is suspending or not
        return compatibleServiceMethod.call(this, *data.toTypedArray()) as R?


    }
}