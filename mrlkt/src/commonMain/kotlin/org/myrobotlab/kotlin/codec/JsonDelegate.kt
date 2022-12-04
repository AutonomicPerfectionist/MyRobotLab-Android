package org.myrobotlab.kotlin.codec

import org.myrobotlab.kotlin.framework.MrlClient
import kotlin.reflect.KProperty

class JsonDelegate<P>(val prop: () -> P?) {
    private var state: String? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        val p = prop()
        return state ?: if(p != null) MrlClient.serde.serialize(p) else "{}"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        state = value
    }
}