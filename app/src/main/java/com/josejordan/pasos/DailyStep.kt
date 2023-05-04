package com.josejordan.pasos

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "daily_steps")
@TypeConverters(DateConverter::class)
data class DailyStep(
    @PrimaryKey
    val date: Date,
    var steps: Int,
    var distance: Float
)
