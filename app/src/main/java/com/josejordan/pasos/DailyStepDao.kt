package com.josejordan.pasos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface DailyStepDao {
    @Insert
    suspend fun insert(dailyStep: DailyStep)

    @Update
    suspend fun update(dailyStep: DailyStep)

    @Query("SELECT * FROM daily_steps WHERE date = :date")
    suspend fun getDailyStep(date: Date): DailyStep?

    @Query("SELECT * FROM daily_steps")
    fun getAllDailySteps(): LiveData<List<DailyStep>>
}

