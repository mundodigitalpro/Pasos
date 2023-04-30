package com.josejordan.pasos

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    var stepCount: Int = 0
    var distance: Double = 0.0
    var totalStepCount: Int = 0
    var totalDistance: Double = 0.0
    var initialStepCount = -1
    var previousStepCount = -1
}
