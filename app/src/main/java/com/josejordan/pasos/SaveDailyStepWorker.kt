package com.josejordan.pasos

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class SaveDailyStepWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams), ViewModelStoreOwner {
    private val viewModelStore = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }

    override suspend fun doWork(): Result = coroutineScope {
        // Get an instance of MyViewModel using ViewModelProvider and ViewModelStoreOwner
        val myViewModel = ViewModelProvider(this@SaveDailyStepWorker)[MyViewModel::class.java]

        // Initialize MyViewModel with the applicationContext
        myViewModel.initialize(applicationContext)

        // Save daily step data and reset step count
        myViewModel.saveDailyStep()
        myViewModel.stepCount = 0

        Result.success()
    }
}
