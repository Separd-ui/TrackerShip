package com.example.trackership.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.trackership.MainActivity
import com.example.trackership.R
import com.example.trackership.utils.Constans
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(
    @ApplicationContext context: Context
    ) =  LocationServices.getFusedLocationProviderClient(context)


    @Provides
    @ServiceScoped
    fun providePendingIntent(
        @ApplicationContext context: Context
    ) = PendingIntent.getActivity(
        context,
        0,
        Intent(context,MainActivity::class.java).also {
            it.action=Constans.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )



    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) =
        NotificationCompat.Builder(context,Constans.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("TrackingShip")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)
}