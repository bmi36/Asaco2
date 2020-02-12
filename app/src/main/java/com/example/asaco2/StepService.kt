package com.example.asaco2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*


class StepService : Service(), SensorEventListener, LifecycleOwner {

    private lateinit var mSensorManager: SensorManager
    private lateinit var mstepConterSensor: Sensor
    var step: Int = -2
    private val dispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        super.onCreate()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mstepConterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        mSensorManager.registerListener(this, mstepConterSensor, SensorManager.SENSOR_DELAY_NORMAL)
//        serviceViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//            .create(RoomViewModel::class.java)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return StepBindar()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val date = SimpleDateFormat("yyyyMMdd", Locale.JAPAN).format(Calendar.getInstance().time)
            step = getSharedPreferences("STEP",Context.MODE_PRIVATE).getInt(date,0)
            step++
            getSharedPreferences("STEP",Context.MODE_PRIVATE).edit().putInt(date,step).apply()

            Log.d("step", step.toString())
        }
    }

    inner class StepBindar : Binder() {
        fun getBindar(): StepService = this@StepService
    }
    override fun getLifecycle(): Lifecycle = dispatcher.lifecycle
}