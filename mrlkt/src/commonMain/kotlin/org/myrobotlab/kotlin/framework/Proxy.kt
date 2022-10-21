package org.myrobotlab.kotlin.framework

import kotlin.reflect.KClass

data class ProxyMethod<R: Any>(val name: String, val methodName: String, val paramList: List<KClass<*>>, val returnType: KClass<R>) {
    operator fun invoke(vararg params: Any?): R? = MrlClient.callService(name, methodName, params)
}

data class Proxy(val name: String, val typeKey: String, val methods: List<ProxyMethod<*>>) {

    operator fun <R : Any> get(methodName: String): ProxyMethod<R> = methods.find { it.methodName == methodName } as ProxyMethod<R>
}