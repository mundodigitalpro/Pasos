package com.josejordan.pasos
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private lateinit var sharedPreferences: SharedPreferences

    val stepCount: LiveData<Int> by lazy {
        SharedPreferenceLiveData(sharedPreferences, PREF_STEP_COUNT, 0)
    }

    val totalStepCount: LiveData<Int> by lazy {
        SharedPreferenceLiveData(sharedPreferences, PREF_TOTAL_STEP_COUNT, 0)
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

    fun updateStepCount(value: Int) {
        sharedPreferences.edit().putInt(PREF_STEP_COUNT, value).apply()
    }

    fun updateTotalStepCount(value: Int) {
        sharedPreferences.edit().putInt(PREF_TOTAL_STEP_COUNT, value).apply()
    }
}
