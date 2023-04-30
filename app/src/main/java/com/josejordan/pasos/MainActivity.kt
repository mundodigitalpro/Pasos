package com.josejordan.pasos

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
class MainActivity : AppCompatActivity(), SensorEventListener {

    private val viewModel: MyViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var stepCountTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var totalStepCountTextView: TextView
    private lateinit var totalDistanceTextView: TextView
    private val activityRecognitionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.init(applicationContext)

        stepCountTextView = findViewById(R.id.step_count_text_view)
        distanceTextView = findViewById(R.id.distance_text_view)
        totalStepCountTextView = findViewById(R.id.total_step_count_text_view)
        totalDistanceTextView = findViewById(R.id.total_distance_text_view)

        checkAndRequestPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        viewModel.stepCount.observe(this, Observer { stepCount ->
            updateStepCountDisplay(stepCount, viewModel.totalStepCount.value ?: 0)
        })

        viewModel.totalStepCount.observe(this, Observer { totalStepCount ->
            updateStepCountDisplay(viewModel.stepCount.value ?: 0, totalStepCount)
        })
    }

    private fun updateStepCountDisplay(stepCount: Int, totalStepCount: Int) {
        stepCountTextView.text = getString(R.string.step_count, stepCount)

        val distance = stepsToKilometers(stepCount)
        distanceTextView.text = getString(R.string.distance, distance)

        totalStepCountTextView.text = getString(R.string.total_step_count, totalStepCount)

        val totalDistance = stepsToKilometers(totalStepCount)
        totalDistanceTextView.text = getString(R.string.total_distance, totalDistance)
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    activityRecognitionRequestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            activityRecognitionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Permiso de reconocimiento de actividad otorgado",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Permiso de reconocimiento de actividad denegado",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (viewModel.initialStepCount < 0) {
                viewModel.initialStepCount = event.values[0].toInt()
            }

            val currentStepCount = event.values[0].toInt() - viewModel.initialStepCount

            if (viewModel.previousStepCount != -1) {
                val difference = currentStepCount - viewModel.previousStepCount
                viewModel.updateTotalStepCount(viewModel.totalStepCount.value!! + difference)
            }

            viewModel.previousStepCount = currentStepCount
            viewModel.updateStepCount(currentStepCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun stepsToKilometers(steps: Int): Double {
        val stepsPerKilometer = 1250.0
        return steps / stepsPerKilometer
    }
}
