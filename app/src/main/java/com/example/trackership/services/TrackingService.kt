package com.example.trackership.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.trackership.R
import com.example.trackership.utils.Constans
import com.example.trackership.utils.Constans.ACTION_PAUSE
import com.example.trackership.utils.Constans.ACTION_START_OR_RESUME
import com.example.trackership.utils.Constans.ACTION_STOP
import com.example.trackership.utils.TrackingUtility
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Path=MutableList<LatLng>
typealias Paths=MutableList<Path>
@AndroidEntryPoint
class TrackingService :LifecycleService(){

    private var isFirstRun=true
    private var isCanceled=false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var notificationBuilder:NotificationCompat.Builder

    private lateinit var locationRequest: LocationRequest

    private lateinit var currentNotificationBuilder:NotificationCompat.Builder

    private val timeRunInSeconds=MutableLiveData<Long>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME->{
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }
                    else{
                        startTimer()
                    }

                }
                ACTION_PAUSE->{
                    pauseService()
                }
                ACTION_STOP->{
                    cancelService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        currentNotificationBuilder=notificationBuilder

        postInitialValues()

        isTracking.observe(this){
            updateTracking(it)
            updateNotificationTracking(it)
        }
    }

    companion object{
        val timeRunInMillis=MutableLiveData<Long>()
        val isTracking=MutableLiveData<Boolean>()
        val paths=MutableLiveData<Paths>()
    }

    private var isTimerEnabled=false
    private var loopTime=0L
    private var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimestamp=0L

    private fun startTimer(){
        addEmptyPath()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true

        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                loopTime=System.currentTimeMillis()-timeStarted

                timeRunInMillis.postValue(timeRun+loopTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp+1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! +1)
                    lastSecondTimestamp+=1000L
                }
                delay(Constans.TIME_DELAY)
            }
            timeRun+=loopTime
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        paths.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun addEmptyPath() {
        paths.value?.apply {
            add(mutableListOf())
            paths.postValue(this)
        }?: paths.postValue(mutableListOf(mutableListOf()))
    }

    private fun addPathPoint(location: Location?){
        location?.let {
            val pos=LatLng(location.latitude,location.longitude)
            paths.value?.apply {
                last().add(pos)
                paths.postValue(this)
            }
        }
    }

    private val locationCallback=object : LocationCallback(){
        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }

        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                 for(location in result.locations)
                     addPathPoint(location)
            }
        }
    }

    private fun updateTracking(isTracking:Boolean){
        if(isTracking){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                 locationRequest=LocationRequest.create().apply {
                    interval=Constans.LOCATION_UPDATE_INTERVAL
                    fastestInterval=Constans.FASTEST_LOCATION_INTERVAL
                    priority=PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }


        }
        else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }

    }

    private fun startForegroundService(){

        startTimer()

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


        startForeground(Constans.NOTIFICATION_ID,notificationBuilder.build())

        timeRunInSeconds.observe(this){
            if(!isCanceled){
                val notification=currentNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it*1000))
                notificationManager.notify(Constans.NOTIFICATION_ID,notification.build())
            }
        }
    }

    private fun updateNotificationTracking(isTracking: Boolean){
        val notText=if(isTracking)
            "Pause"
        else
            "Resume"
        val pendingIntent=if(isTracking){
            val pauseIntent=Intent(this,TrackingService::class.java).apply {
                action=ACTION_PAUSE
            }
            PendingIntent.getService(this,1,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        }
        else{
            val resumeIntent=Intent(this,TrackingService::class.java).apply {
                action= ACTION_START_OR_RESUME
            }
            PendingIntent.getService(this,2,resumeIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(currentNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }
        if(!isCanceled){
            currentNotificationBuilder=notificationBuilder
                .addAction(R.drawable.ic_pause,notText,pendingIntent)
            notificationManager.notify(Constans.NOTIFICATION_ID,currentNotificationBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager:NotificationManager){
        val channel=NotificationChannel(
            Constans.NOTIFICATION_CHANNEL_ID,
            Constans.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
    private fun cancelService(){
        isCanceled=true
        isFirstRun=true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }


}