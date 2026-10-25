package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.*

object QiblaHelper {
    // Kaaba coordinates (Mecca)
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LNG = 39.8262

    /**
     * Calculates the bearing (0 - 360 degrees) from a given lat/lng to the Kaaba
     */
    fun calculateQiblaDirection(userLat: Double, userLng: Double): Float {
        val lat1 = Math.toRadians(userLat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val deltaLng = Math.toRadians(KAABA_LNG - userLng)

        val y = sin(deltaLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing.toFloat()
    }

    /**
     * Flow of device azimuth heading (0 - 360 degrees, where 0 is magnetic North)
     */
    fun getCompassHeading(context: Context): Flow<Float> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sensorManager == null) {
            trySend(0f)
            close()
            return@callbackFlow
        }

        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        var lastAzimuth = 0f

        val listener = object : SensorEventListener {
            private val rMat = FloatArray(9)
            private val orientation = FloatArray(3)
            private var lastAcc: FloatArray? = null
            private var lastMag: FloatArray? = null

            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rMat, event.values)
                    SensorManager.getOrientation(rMat, orientation)
                    val azimuthRad = orientation[0]
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    azimuthDeg = (azimuthDeg + 360f) % 360f
                    // Smooth
                    lastAzimuth = smoothHeading(lastAzimuth, azimuthDeg)
                    trySend(lastAzimuth)
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    lastAcc = event.values.clone()
                    computeHeadingFromAccMag()
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    lastMag = event.values.clone()
                    computeHeadingFromAccMag()
                }
            }

            private fun computeHeadingFromAccMag() {
                val acc = lastAcc ?: return
                val mag = lastMag ?: return
                if (SensorManager.getRotationMatrix(rMat, null, acc, mag)) {
                    SensorManager.getOrientation(rMat, orientation)
                    var deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    deg = (deg + 360f) % 360f
                    lastAzimuth = smoothHeading(lastAzimuth, deg)
                    trySend(lastAzimuth)
                }
            }

            private fun smoothHeading(oldH: Float, newH: Float): Float {
                var diff = newH - oldH
                while (diff < -180f) diff += 360f
                while (diff > 180f) diff -= 360f
                return (oldH + 0.2f * diff + 360f) % 360f
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometer?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
