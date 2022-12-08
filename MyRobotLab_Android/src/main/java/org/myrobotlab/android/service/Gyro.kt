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
import org.myrobotlab.kotlin.service.data.Orientation
import org.myrobotlab.kotlin.service.interfaces.OrientationListener
import org.myrobotlab.kotlin.service.interfaces.OrientationPublisher

@MrlService
class Gyro(name: String): Service(name), OrientationPublisher, SensorEventListener {
    private val contextContainer: ContextContainer by inject()
    private val sensorManager: SensorManager by inject()
    private var gyro: Sensor? = null

    private val sensorPollRate = 500 * 1000 //500 ms



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
        sensorManager.registerListener(this, gyro, sensorPollRate)

    }

    override fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun startOrientationTracking() {
        TODO("Not yet implemented")
    }

    override fun stopOrientationTracking() {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == gyro) {
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
            val pitch: Float = orientationArray[1] * FROM_RADS_TO_DEGS
            val roll: Float = orientationArray[2] * FROM_RADS_TO_DEGS
            val yaw: Float = orientationArray[0] * FROM_RADS_TO_DEGS
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