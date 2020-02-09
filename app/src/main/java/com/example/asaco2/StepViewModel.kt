package com.example.asaco2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class StepViewModel : ViewModel() {


    val stepEntity: MutableLiveData<RoomEntity> = MutableLiveData()
    private var mstepEntity: RoomEntity? = null

    fun getStep(step: Int){
        val date = SimpleDateFormat("yyyyMMdd", Locale.JAPAN).format(Calendar.getInstance().time)
        mstepEntity = RoomEntity(date.toLong(),step)
        stepEntity.postValue(mstepEntity)
    }

    //    private val stepArray: MutableLiveData<ArrayList<StepEntity>> = MutableLiveData()
//    var mstepArray: ArrayList<StepEntity>? = null


//    fun uploadList(step: Int){
//        val date = SimpleDateFormat("yyyyMMdd", Locale.UK).format(Calendar.getInstance().time)
//        mstepArray?.let {arraylist->
//            arraylist.forEach { if (it.id == date.toLong()) it.step = step }
//        stepArray = arraylist
//    }
}