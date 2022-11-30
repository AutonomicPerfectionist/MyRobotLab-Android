package org.myrobotlab.kotlin.service.data

import org.myrobotlab.kotlin.annotations.MrlClassMapping

@MrlClassMapping("org.myrobotlab.service.data.Orientation")
data class Orientation(val roll: Double, val pitch: Double, val yaw: Double)