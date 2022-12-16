package org.myrobotlab.android.service

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.myrobotlab.android.ContextContainer
import org.myrobotlab.kotlin.annotations.MrlService
import org.myrobotlab.kotlin.framework.Service
import org.myrobotlab.kotlin.service.data.*
import org.myrobotlab.kotlin.service.interfaces.OrientationListener
import org.myrobotlab.kotlin.service.interfaces.OrientationPublisher
import org.myrobotlab.kotlin.service.interfaces.Sampler

/**
 * A service that provides access to the Android Rotation
 * Vector sensor, which in essence acts as a software-normalized
 * gyroscope instead of raw gyro data.
 *
 * [Orientation] data published by this service describes
 * the orientation of the physical Android device this service is
 * running on. On a normal candy-bar style phone,
 * this defines the Z axis as being parallel to the device's
 * long edge and going down the center. The X axis is therefore
 * parallel to the short edge through the center, and the Y axis is
 * through the device from back to front through the center again.
 *
 * These axes may be different on other Android devices.
 *
 * See [Orientation] for information on how the rotational
 * axes are defined in relation to the x/y/z axes.
 *
 * The orientation angles range from -180 to +180
 * with zero pitch and roll being defined as the standard
 * perfectly vertical Portrait orientation. Yaw's
 * zero is harder to define, it is recommended to find it by experimentation.
 *
 * @author AutonomicPerfectionist
 */
@MrlService
class Gyro(name: String): Service(name), OrientationPublisher, SensorEventListener, Sampler {
    private val sensorManager: SensorManager by inject()
    private var gyro: Sensor? = null

    override var sampleRate: Hertz = 2.hz


    override fun publishOrientation(data: Orientation): Orientation {
        return data
    }

    override fun attach(listener: OrientationListener) {
        TODO("Not yet implemented")
    }

    override fun detach(listener: OrientationListener) {
        TODO("Not yet implemented")
    }

    override fun start() {
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, gyro, sampleRate.period.toMicroseconds().value)

    }

    override fun stop() {
        sensorManager.unregisterListener(this)
    }

    @Deprecated("use start", ReplaceWith("start()"))
    override fun startOrientationTracking() {
        TODO("Not yet implemented")
    }

    @Deprecated("use stop", ReplaceWith("stop()"))
    override fun stopOrientationTracking() {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == gyro) {
            // Big long calculation to convert
            // quaternion to yaw/pitch/roll
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val worldAxisX = SensorManager.AXIS_X
            val worldAxisZ = SensorManager.AXIS_Z
            val adjustedRotationMatrix = FloatArray(9)
            SensorManager.remapCoordinateSystem(
                rotationMatrix,
                worldAxisX,
                worldAxisZ,
                adjustedRotationMatrix
            )
            val orientationArray = FloatArray(3)
            SensorManager.getOrientation(adjustedRotationMatrix, orientationArray)
            val yaw: Float = orientationArray[0] * FROM_RADS_TO_DEGS
            val pitch: Float = orientationArray[1] * FROM_RADS_TO_DEGS
            val roll: Float = orientationArray[2] * FROM_RADS_TO_DEGS
            val orientation = Orientation(roll.toDouble(), pitch.toDouble(), yaw.toDouble())
            serviceScope.launch {
                invoke<Orientation>("publishOrientation", orientation)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    companion object {
        const val FROM_RADS_TO_DEGS = -57
    }
}