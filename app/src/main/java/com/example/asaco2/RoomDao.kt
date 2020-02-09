package com.example.asaco2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomDao{

    @Query("select step from entity where data")
    fun getStep(): Array<Int>
    @Query("select sum(step) from entity where data between :data - 6 and :data group by data order by data ASC")
    suspend fun getsumSteps(data: Long): Array<Int>

    @Insert
    suspend fun insert(entity: RoomEntity)

    @Update
    suspend fun update(entity: RoomEntity)

    @Query("select sum(step) from entity where data between :year || 01 || '%' and :year || 12 || '%' order by :year || data ASC")
    suspend fun getMonth(year: Long): Array<Int>

    @Query("select * from entity where data like :data || '%'")
    suspend fun getDayEntity(data: Long): RoomEntity

}