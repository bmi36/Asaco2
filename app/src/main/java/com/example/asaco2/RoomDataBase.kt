package com.example.asaco2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomEntity::class],version = 1,exportSchema = false)
abstract class RoomDataBase : RoomDatabase(){
    abstract fun dao(): RoomDao

    companion object{
        @Volatile
        private  var instance: RoomDataBase? = null

        fun getInstance(context: Context): RoomDataBase = instance ?: synchronized(this){
            Room.databaseBuilder(context,RoomDataBase::class.java,"step.db").build()
        }
    }
}