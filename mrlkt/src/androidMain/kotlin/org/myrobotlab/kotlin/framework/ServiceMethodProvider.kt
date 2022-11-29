package org.myrobotlab.kotlin.framework

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaMethod

actual object ServiceMethodProvider {
    actual val ServiceInterface.mrlInterfaceNames: List<String>
        get() {
            return this::class.allSuperclasses.mapNotNull {
                if(it.java.isInterface && it.hasAnnotation<MrlClassMapping>())
                    it.findAnnotation<MrlClassMapping>()?.javaClass
                else null
            }
        }

    actual val ServiceInterface.methods: List<ServiceMethod>
        get() {
            val classMembers = (this::class.members as List<KCallable<*>>).map {
                ServiceMethod(this, it.name,
                    it.valueParameters.map { param -> param.type }, it.returnType, it)
            }
            val classProperties = (this::class.memberProperties as List<KProperty<*>>)
            val getters = classProperties.map {
                it.getter
            }.map {
                ServiceMethod(this, it.javaMethod?.name ?: it.name,
                    it.valueParameters.map { param -> param.type }, it.returnType, it)
            }
            val setters = classProperties.mapNotNull {
                if (it is KMutableProperty) it.setter else null
            }.map {
                ServiceMethod(this, it.javaMethod?.name ?: it.name,
                    it.valueParameters.map { param -> param.type }, it.returnType, it)
            }

            return classMembers + getters + setters
        }

    actual suspend fun <R> ServiceInterface.callMethod(
        method: String,
        data: List<Any?>
    ): R? {
        val serviceMethods = methods.filter { it.methodName == method && it.valueParameters.size == data.size}
        val dataTypes = data.map { it?.let { it::class } }

        var compatibleServiceMethod: KCallable<*>? = null

        for (serviceMethod in serviceMethods) {
            val params = serviceMethod.valueParameters
            var isCompatible = true
            for (i in params.indices) {
                val dataType = dataTypes[i]?.javaObjectType
                    ?: if (params[i].isMarkedNullable)
                        continue
                    else {
                        isCompatible = false
                        break
                    }
                val paramType = (params[i].classifier as KClass<*>).javaObjectType
                if (!paramType.isAssignableFrom(dataType)) {
                    isCompatible = false
                    break
                }
            }
            if (isCompatible) {
                compatibleServiceMethod = serviceMethod.callable
                break
            }

        }

        if (compatibleServiceMethod == null) {
            throw RuntimeException("Cannot find compatible method ($name@$method($data))")
        }
        // TODO Test whether receiver is needed, test whether method is suspending or not
        if (compatibleServiceMethod.isSuspend) {
            return compatibleServiceMethod.callSuspend(this, *data.toTypedArray()) as R?

        }
        return compatibleServiceMethod.call(this, *data.toTypedArray()) as R?


    }

    actual fun <T : ServiceInterface> KClass<T>.constructService(
        name: String
    ): T {
        return this.primaryConstructor?.call(name) ?: throw RuntimeException("No primary constructor with one name parameter found")
    }
}