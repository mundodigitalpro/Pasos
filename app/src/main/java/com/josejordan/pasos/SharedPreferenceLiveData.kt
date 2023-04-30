package com.josejordan.pasos

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class SharedPreferenceLiveData<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defaultValue)
            }
        }

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defaultValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getValueFromPreferences(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue as String)
            is Int -> sharedPreferences.getInt(key, defaultValue as Int)
            is Float -> sharedPreferences.getFloat(key, defaultValue as Float)
            is Long -> sharedPreferences.getLong(key, defaultValue as Long)
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue as Boolean)
            else -> throw IllegalArgumentException("Unsupported type")
        } as T
    }
}
