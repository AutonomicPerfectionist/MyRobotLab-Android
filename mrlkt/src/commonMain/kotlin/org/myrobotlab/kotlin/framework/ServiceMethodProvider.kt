package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable

expect object ServiceMethodProvider {
    val Service.methods:List<KCallable<*>>
    fun Service.callMethod(method: String, data:List<Any?>): Any?
}