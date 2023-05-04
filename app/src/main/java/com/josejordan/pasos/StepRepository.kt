package com.josejordan.pasos

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class StepRepository(private val dailyStepDao: DailyStepDao) {

    val allDailySteps: LiveData<List<DailyStep>> = dailyStepDao.getAllDailySteps()

    suspend fun getDailyStep(date: Date): DailyStep? {
        return withContext(Dispatchers.IO) {
            dailyStepDao.getDailyStep(date)
        }
    }

    suspend fun insert(dailyStep: DailyStep) {
        withContext(Dispatchers.IO) {
            dailyStepDao.insert(dailyStep)
        }
    }

    suspend fun update(dailyStep: DailyStep) {
        withContext(Dispatchers.IO) {
            dailyStepDao.update(dailyStep)
        }
    }
}
