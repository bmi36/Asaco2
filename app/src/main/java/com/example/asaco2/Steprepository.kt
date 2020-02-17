package com.example.asaco2

import androidx.annotation.WorkerThread
import java.lang.Exception

class RoomRepository (private val dao: RoomDao){

    @WorkerThread
    suspend fun insert(entity: RoomEntity) = dao.insert(entity)
    suspend fun update(entity: RoomEntity) = dao.update(entity)
    suspend fun getsum(date: Long): Array<Int> = dao.getsumSteps(date)
    suspend fun getMonth(year: Long): Array<Int> = dao.getMonth(year)
    suspend fun getDayEntity(id: Long): RoomEntity = dao.getDayEntity(id)
}