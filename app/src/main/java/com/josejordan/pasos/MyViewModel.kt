package com.josejordan.pasos

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private lateinit var sharedPreferences: SharedPreferences

    var stepCount: Int = 0
        get() = sharedPreferences.getInt(PREF_STEP_COUNT, 0)
        set(value) {
            field = value
            sharedPreferences.edit().putInt(PREF_STEP_COUNT, value).apply()
        }

    var totalStepCount: Int = 0
        get() = sharedPreferences.getInt(PREF_TOTAL_STEP_COUNT, 0)
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

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    }
}
