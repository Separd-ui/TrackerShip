package com.example.trackership.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.trackership.db.RunDatabase
import com.example.trackership.utils.Constans
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        Constans.DATABASE_NAME
        ).build()


    @Provides
    @Singleton
    fun provideRunDao(
        database: RunDatabase
    ) = database.provideDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ) = context.getSharedPreferences(Constans.SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(Constans.KEY_NAME,"")?:""

    @Provides
    @Singleton
    fun provideWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(Constans.KEY_WEIGHT,50f)

    @Provides
    @Singleton
    fun provideFirstTimeLaunchKey(sharedPreferences: SharedPreferences) = sharedPreferences
        .getBoolean(Constans.KEY_FIRST_TIME_LAUNCH,true)
}