package com.example.asaco2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RoomViewModel(application: Application) : ViewModel() {
    private val repository: RoomRepository = RoomDataBase.getInstance(application).dao().let {
        RoomRepository(it)
    }

    private var stepList = MediatorLiveData<Array<Int>>()
    var mstepList: MediatorLiveData<Array<Int>>

    init {
       stepList.postValue(repository.getStep())
        mstepList = this.stepList
    }

    fun getStep(id: Long) = runBlocking { getsum(id) }
    fun getMonth(year: Long) = runBlocking { getmonth(year) }
    fun getDayEntity(id: Long) = runBlocking { getdayentity(id) }

    fun insert(entity: RoomEntity) = viewModelScope.launch { repository.insert(entity) }
    fun update(entity: RoomEntity) = viewModelScope.launch{ repository.update(entity) }
    suspend fun getsum(id: Long): Array<Int>? = withContext(Dispatchers.Default) { repository.getsum(id) }
    suspend fun getmonth(year: Long): Array<Int> = withContext(Dispatchers.Default){ repository.getMonth(year) }
    suspend fun getdayentity(id: Long): RoomEntity = withContext(Dispatchers.Default){ repository.getDayEntity(id) }
    fun getStep() {
        stepList.postValue(repository.getStep())
    }
}