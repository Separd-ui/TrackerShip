package com.example.trackership.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.trackership.db.SortType
import com.example.trackership.db.SortType.*
import com.example.trackership.models.Run
import com.example.trackership.utils.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val runRepository: RunRepository
):ViewModel() {

    private var sortType=TIMESTAMP

    fun insertRun(run: Run) = viewModelScope.launch(Dispatchers.IO){
        runRepository.insertRun(run)
    }
    fun deleteRun(run: Run)=viewModelScope.launch(Dispatchers.IO){
        runRepository.deleteRun(run)
    }

    private val runsSortedByDate=runRepository.getRunsSortedByDate().asLiveData()
    private val runsSortedByDistance=runRepository.getRunsSortedByDistance().asLiveData()
    private val runsSortedByDuration=runRepository.getRunsSortedByDuration().asLiveData()
    private val runsSortedByCalories=runRepository.getRunsSortedByCalories().asLiveData()
    private val runsSortedByAvgSpeed=runRepository.getRunsSortedBySpeed().asLiveData()

    val runsToObserve=MediatorLiveData<List<Run>>()

    init {
        runsToObserve.addSource(runsSortedByDate){
            if(sortType==TIMESTAMP)
                runsToObserve.postValue(it)
        }
        runsToObserve.addSource(runsSortedByAvgSpeed){
            if(sortType==AVG_SPEED)
                runsToObserve.postValue(it)
        }
        runsToObserve.addSource(runsSortedByDistance){
            if(sortType==DISTANCE)
                runsToObserve.postValue(it)
        }
        runsToObserve.addSource(runsSortedByCalories){
            if(sortType==CALORIES)
                runsToObserve.postValue(it)
        }
        runsToObserve.addSource(runsSortedByDuration){
            if(sortType==DURATION)
                runsToObserve.postValue(it)
        }
    }

    fun sortRunsBy(sortType: SortType){
        when(sortType){
            DURATION->runsSortedByDate.value?.let { runsToObserve.postValue(it) }
            TIMESTAMP->runsSortedByDate.value?.let { runsToObserve.postValue(it) }
            CALORIES->runsSortedByCalories.value?.let { runsToObserve.postValue(it) }
            DISTANCE->runsSortedByDistance.value?.let{ runsToObserve.postValue(it) }
            AVG_SPEED->runsSortedByAvgSpeed.value?.let{ runsToObserve.postValue(it)}
        }.also {
            this.sortType=sortType
        }
    }

}