package org.myrobotlab.kotlin.framework

import org.myrobotlab.kotlin.codec.CodecUtils.Companion.fromJson
import org.myrobotlab.kotlin.codec.CodecUtils.Companion.toJson

actual class JsonSerde {
    actual inline fun <reified T> deserialize(json: String): T = json.fromJson()

    actual fun <T> serialize(o: T): String = o.toJson()
}