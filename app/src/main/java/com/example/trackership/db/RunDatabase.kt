package com.example.trackership.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trackership.models.Run
import com.example.trackership.utils.Constans

@Database(entities = [Run::class],version = 1,exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunDatabase :RoomDatabase(){

    abstract fun provideDao():RunDao


    companion object{

        private var INSTANCE:RunDatabase?=null

        fun getDatabase(context: Context) = INSTANCE?: synchronized(this){
            val instance=Room.databaseBuilder(
                context,
                RunDatabase::class.java,
                Constans.DATABASE_NAME
            ).build()

            INSTANCE=instance
            instance
        }
    }
}