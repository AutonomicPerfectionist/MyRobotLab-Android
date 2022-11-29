package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.service.data.Orientation

interface OrientationListener {
    val name: String

    fun onOrientation(data: Orientation): Orientation
}