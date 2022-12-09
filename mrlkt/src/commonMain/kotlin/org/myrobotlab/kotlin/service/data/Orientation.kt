package org.myrobotlab.kotlin.service.data

import org.myrobotlab.kotlin.annotations.MrlClassMapping

/**
 * Represents a physical orientation of some sensor
 * or object in degrees. [roll] measures rotation about
 * the object's Y axis, [pitch] measures rotation about
 * the X axis, and [yaw] measures the rotation about the
 * Z axis. The coordinate system used to define these axes
 * is dependent on the implementing service; for example:
 * one service may define Z as up while another may define Z
 * as out from a given surface.
 */
@MrlClassMapping("org.myrobotlab.service.data.Orientation")
data class Orientation(val roll: Double, val pitch: Double, val yaw: Double)