package com.example.trackership.utils

object Constans {
    const val DATABASE_NAME="run_database"
    const val ACTION_START_OR_RESUME="ACTION_START_OR_RESUME"
    const val ACTION_PAUSE="ACTION_PAUSE"
    const val ACTION_STOP="ACTION_STOP"
    const val ACTION_SHOW_TRACKING_FRAGMENT="ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID="tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME="Tracking"
    const val NOTIFICATION_ID=1

    const val LOCATION_UPDATE_INTERVAL=5000L
    const val FASTEST_LOCATION_INTERVAL=2000L

    const val PATH_COLOR= android.graphics.Color.BLUE
    const val PATH_WIDTH=10f

    const val MAP_ZOOM=17f

    const val TIME_DELAY=50L

    const val FINISH_TRACKING_TAG="finish_tracking"

    //Shared Pref
    const val SHARED_PREFERENCES_NAME="PREFS"
    const val  KEY_FIRST_TIME_LAUNCH="KEY_FIRST_TIME_LAUNCH"
    const val  KEY_NAME="KEY_NAME"
    const val  KEY_WEIGHT="KEY_WEIGHT"
}