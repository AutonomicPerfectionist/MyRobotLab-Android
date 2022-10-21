package org.myrobotlab.kotlin.framework

import kotlin.reflect.KCallable

actual object ServiceMethodProvider {
    actual val Service.methods: List<KCallable<*>>
        get() = TODO("Not yet implemented")

    actual fun Service.callMethod(
        method: String,
        data: List<Any?>
    ): Any? {
        TODO("Not yet implemented")
    }
}