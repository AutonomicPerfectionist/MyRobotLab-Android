package org.myrobotlab.kotlin.service.data

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.image.SerializableImage")
data class SerializableImage(val bytes: List<Byte>, val height: Int) {

    // Just so downstream users can add extension methods to it
    companion object
}
