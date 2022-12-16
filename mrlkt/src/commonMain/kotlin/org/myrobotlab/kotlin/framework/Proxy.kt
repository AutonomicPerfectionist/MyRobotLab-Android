package org.myrobotlab.kotlin.framework

import kotlin.reflect.KClass

/**
 * Represents a remote service method that cannot be represented by
 * a [Service] class, for example in the case where a remote instance
 * has a service that is not implemented in this instance.
 */
data class ProxyMethod<R: Any>(val name: String, val methodName: String, val paramList: List<KClass<*>>, val returnType: KClass<R>) {
    inline operator fun <reified T: R> invoke(vararg params: Any?): T? = MrlClient.callService(name, methodName, params)
}

/**
 * Represents a remote service that cannot be represented by
 * a normal [Service] class.
 */
data class Proxy(val name: String, val typeKey: String, val methods: List<ProxyMethod<*>>) {

    operator fun <R : Any> get(methodName: String): ProxyMethod<R> = methods.find { it.methodName == methodName } as ProxyMethod<R>
}