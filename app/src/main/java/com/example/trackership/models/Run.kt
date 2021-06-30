package com.example.trackership.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class Run(
    var img:Bitmap?=null,
    var timestamp:Long=0L,
    var avgSpeed:Float=0f,
    var distanceInMetres:Int=0,
    var duration:Long=0L,
    var caloriesBurned:Int=0,
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
)