package com.example.trackership.utils

import com.example.trackership.db.RunDao
import com.example.trackership.models.Run
import javax.inject.Inject

class RunRepository @Inject constructor(
    val runDao: RunDao
) {

    suspend fun insertRun(run:Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getRunsSortedByDate() = runDao.getRunsSortedByDate()

    fun getRunsSortedByDistance() = runDao.getRunsSortedByDistance()

    fun getRunsSortedByCalories() = runDao.getRunsSortedByCalories()

    fun getRunsSortedByDuration() = runDao.getRunsSortedByDuration()

    fun getRunsSortedBySpeed() = runDao.getRunsSortedBySpeed()

    fun getTotalAvgSpeed() = runDao.getAverageSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalDuration() = runDao.getTotalDuration()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

}