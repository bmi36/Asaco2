package com.example.asaco2.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.sql.SQLException

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val calendarRepository: CalendarRepository
    private var repoDao: CalendarDao = CalendarDatabase.getInstance(application).calendarDao()

    init {
        calendarRepository = CalendarRepository(repoDao)
    }

    fun insert(entity: CalendarEntity) = viewModelScope.launch { calendarRepository.insert(entity) }

    fun update(entity: CalendarEntity) = viewModelScope.launch { calendarRepository.update(entity) }

    fun getCalendar(id: Long): List<CalendarEntity>? = calendarRepository.getCalendar(id)
}