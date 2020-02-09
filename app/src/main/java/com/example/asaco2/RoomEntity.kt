package com.example.asaco2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "entity")
data class RoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "data")
    var id: Long,
    @ColumnInfo(name = "step")
    var step: Int
): Serializable