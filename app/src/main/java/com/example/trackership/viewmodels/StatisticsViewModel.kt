package com.example.trackership.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.trackership.utils.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    runRepository: RunRepository
): ViewModel(){

    val totalTimeRun=runRepository.getTotalDuration().asLiveData()
    val totalDistance=runRepository.getTotalDistance().asLiveData()
    val totalCalories=runRepository.getTotalCaloriesBurned().asLiveData()
    val totalAvgSpeed=runRepository.getTotalAvgSpeed().asLiveData()

    val runsSortedByDate = runRepository.getRunsSortedByDate().asLiveData()

}