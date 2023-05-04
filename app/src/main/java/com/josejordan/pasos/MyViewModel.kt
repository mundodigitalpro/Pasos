package com.josejordan.pasos

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date


class MyViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var sharedPreferences: SharedPreferences
    private val dailyStepDao = AppDatabase.getDatabase(application).dailyStepDao()
    private val stepRepository = StepRepository(dailyStepDao)

    init {
        initialize(application.applicationContext)
    }

    var stepCount = sharedPreferences.getInt(PREF_STEP_COUNT, 0)
        set(value) {
            field = value
            sharedPreferences.edit().putInt(PREF_STEP_COUNT, value).apply()
        }

    var totalStepCount = sharedPreferences.getInt(PREF_TOTAL_STEP_COUNT, 0)
        set(value) {
            field = value
            sharedPreferences.edit().putInt(PREF_TOTAL_STEP_COUNT, value).apply()
        }

    var initialStepCount = -1
    var previousStepCount = -1

    companion object {
        const val PREFERENCES_FILE = "com.josejordan.pasos.preferences"
        const val PREF_STEP_COUNT = "step_count"
        const val PREF_TOTAL_STEP_COUNT = "total_step_count"
    }

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    fun saveDailyStep() {
        viewModelScope.launch {
            val currentDate = getCurrentDate()
            val existingDailyStep = stepRepository.getDailyStep(currentDate)

            if (existingDailyStep == null) {
                val distance = stepsToKilometers(stepCount)
                stepRepository.insert(
                    DailyStep(
                        date = currentDate,
                        steps = stepCount,
                        distance = distance.toFloat()
                    )
                )
            } else {
                existingDailyStep.steps = stepCount
                existingDailyStep.distance = stepsToKilometers(stepCount).toFloat()
                stepRepository.update(existingDailyStep)
            }
        }
    }

    private fun getCurrentDate(): Date {
        return Date()
    }

    fun stepsToKilometers(steps: Int): Double {
        val stepsPerKilometer = 1250.0
        return steps / stepsPerKilometer
    }

    fun getAllDailySteps(): LiveData<List<DailyStep>> {
        return stepRepository.allDailySteps
    }
}




