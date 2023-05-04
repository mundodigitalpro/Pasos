package com.josejordan.pasos

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {

    //private val viewModel: MyViewModel by viewModels()
    private lateinit var viewModel: MyViewModel
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

        //viewModel.init(applicationContext)

        val viewModelFactory = MyViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyViewModel::class.java)

        observeDailySteps()

        stepCountTextView = findViewById(R.id.step_count_text_view)
        distanceTextView = findViewById(R.id.distance_text_view)
        totalStepCountTextView = findViewById(R.id.total_step_count_text_view)
        totalDistanceTextView = findViewById(R.id.total_distance_text_view)

        checkAndRequestPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //scheduleSaveDailyStepWorker()
    }
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), activityRecognitionRequestCode)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            activityRecognitionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(findViewById(android.R.id.content), "Permiso de reconocimiento de actividad otorgado", Snackbar.LENGTH_SHORT).show()
                } else {

                    Snackbar.make(findViewById(android.R.id.content), "Permiso de reconocimiento de actividad denegado", Snackbar.LENGTH_SHORT).show()
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
                viewModel.totalStepCount += difference
            }

            viewModel.previousStepCount = currentStepCount
            viewModel.stepCount = currentStepCount
            updateStepCountDisplay()
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private fun updateStepCountDisplay() {
        stepCountTextView.text = getString(R.string.step_count, viewModel.stepCount)

        val distance = viewModel.stepsToKilometers(viewModel.stepCount)
        distanceTextView.text = getString(R.string.distance, distance)

        totalStepCountTextView.text = getString(R.string.total_step_count, viewModel.totalStepCount)

        val totalDistance = viewModel.stepsToKilometers(viewModel.totalStepCount)
        totalDistanceTextView.text = getString(R.string.total_distance, totalDistance)
    }
    private fun scheduleSaveDailyStepWorker() {
        val saveDailyStepWorkRequest = PeriodicWorkRequestBuilder<SaveDailyStepWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueue(saveDailyStepWorkRequest)
    }

    private fun observeDailySteps() {
        viewModel.getAllDailySteps().observe(this, Observer { dailySteps ->
            Log.d("MainActivity", "Daily steps data:")
            dailySteps.forEach { dailyStep ->
                Log.d("MainActivity", "Date: ${dailyStep.date}, Steps: ${dailyStep.steps}, Distance: ${dailyStep.distance}")
            }
        })
    }
}
