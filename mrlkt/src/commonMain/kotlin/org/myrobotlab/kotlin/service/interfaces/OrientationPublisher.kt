package org.myrobotlab.kotlin.service.interfaces

import org.myrobotlab.kotlin.annotations.MrlClassMapping
import org.myrobotlab.kotlin.service.data.Orientation

/**
 * mrlkt equivalent of `org.myrobotlab.service.interfaces.OrientationPublisher`,
 * this interface marks a service as being able to publish orientation data.
 * Services that implement this interface are expected to publish [Orientation]
 * objects at some sampling rate, which is not guaranteed to be constant.
 */
@MrlClassMapping("org.myrobotlab.service.interfaces.OrientationPublisher")
interface OrientationPublisher {

    /**
     * Name of the service
     */
    val name: String

    /**
     * The method endpoint that is `invoke()`'ed
     * when new orientation data is available.
     * Interested parties should subscribe to this method,
     * and implementing services should make sure to call
     * `invoke("publishOrientation", ...)` to publish the
     * data when available.
     */
    fun publishOrientation(data: Orientation): Orientation

    /**
     * Attach a listener to this publisher so when
     * [publishOrientation] is invoked the given
     * listener is notified via [OrientationListener.onOrientation].
     * [listener] should implement [hashCode] to ensure
     * proper removal if [detach] is called.
     */
    fun attach(listener: OrientationListener)

    /**
     * Remove an already attached listener,
     * if [listener] was not attached already
     * then this method functions as a no-op.
     */
    fun detach(listener: OrientationListener)

    /**
     * Begin publishing orientation
     * data, published data is available
     * by either subscribing to [publishOrientation]
     * or by adding an [OrientationListener] via
     * [attach]. This method may or may not
     * allocate handlers on hardware resources
     * responsible for the orientation data
     * and therefore may block for a significant amount
     * of time.
     */
    fun start()

    /**
     * Stop publishing orientation
     * data, this may or may not
     * release any handlers on hardware resources
     * responsible for the orientation data.
     */
    fun stop()

    /**
     * Only implemented for backwards compatibility,
     * analogous to [start]
     */
    @Deprecated("use start")
    fun startOrientationTracking()

    /**
     * Only implemented for backwards compatibility,
     * analogous to [stop]
     */
    @Deprecated("use stop")
    fun stopOrientationTracking()
}