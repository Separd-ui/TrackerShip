package com.example.trackership.db

import androidx.room.*
import com.example.trackership.models.Run
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run:Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    fun  getRunsSortedByDate():Flow<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY duration DESC")
    fun  getRunsSortedByDuration():Flow<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun  getRunsSortedByCalories():Flow<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY avgSpeed DESC")
    fun  getRunsSortedBySpeed():Flow<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMetres DESC")
    fun  getRunsSortedByDistance():Flow<List<Run>>

    @Query("SELECT SUM(duration) FROM run_table")
    fun getTotalDuration():Flow<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned():Flow<Int>

    @Query("SELECT SUM(distanceInMetres) FROM run_table")
    fun getTotalDistance():Flow<Int>

    @Query("SELECT AVG(avgSpeed) FROM run_table")
    fun getAverageSpeed():Flow<Float>
}