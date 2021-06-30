package com.example.trackership.utils

import android.content.Context
import android.location.Location
import android.os.Build
import com.example.trackership.services.Path
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermission(context:Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        else{
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getFormattedStopWatchTime(ms:Long,includeMillis:Boolean=false):String{
        var millis=ms
        val hours=TimeUnit.MILLISECONDS.toHours(millis)
        millis-=TimeUnit.HOURS.toMillis(hours)
        val minutes=TimeUnit.MILLISECONDS.toMinutes(millis)
        millis-=TimeUnit.MINUTES.toMillis(minutes)
        val seconds=TimeUnit.MILLISECONDS.toSeconds(millis)
        if(!includeMillis){
            return "${if(hours<10) "0" else ""}$hours:"+
                    "${if(minutes<10) "0" else ""}$minutes:"+
                    "${if(seconds<10) "0" else ""}$seconds"
        }
        millis-=TimeUnit.SECONDS.toMillis(seconds)
        millis/=10

        return "${if(hours<10) "0" else ""}$hours:"+
                "${if(minutes<10) "0" else ""}$minutes:"+
                "${if(seconds<10) "0" else ""}$seconds:"+
                "${if(millis<10) "0" else ""}$millis"


    }

    fun calculateTotalDistance(path:Path):Float{
        var distance=0f
        for(i in 0..path.size-2){
            val pos1=path[i]
            val pos2=path[i+1]

            val result=FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance+=result[0]
        }
        return distance
    }
}