package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.service.data.Orientation

@MrlClassMapping("org.myrobotlab.service.interfaces.OrientationPublisher")
interface OrientationPublisher {
    val name: String

    fun publishOrientation(data: Orientation): Orientation
    fun attach(listener: OrientationListener)
    fun detach(listener: OrientationListener)

    /**
     * start publishing orientation
     */
    fun start()

    /**
     * stop publishing orientation
     */
    fun stop()

    @Deprecated("") /* use start */
    fun startOrientationTracking()

    @Deprecated("") /* use stop */
    fun stopOrientationTracking()
}