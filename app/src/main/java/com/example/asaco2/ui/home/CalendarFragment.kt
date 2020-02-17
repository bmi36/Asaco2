package com.example.asaco2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.asaco2.R
import com.example.asaco2.RoomViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.coroutines.CoroutineContext

class CalendarFragment(val calory: Int) : Fragment(), CoroutineScope {

    private lateinit var calendaredModel: CalendarViewModel
    private lateinit var stepModel: RoomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    private lateinit var bottomsheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom_sheet_layout.layoutParams.height = 900
        bottomsheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val strMonth = if (month > 9) "${month + 1}" else "0${month + 1}"
            val strDay = if (dayOfMonth > 9) "$dayOfMonth" else "0${dayOfMonth}"
            val date = SimpleDateFormat("yyyyMMdd", Locale.JAPAN).format(Calendar.getInstance().time)
            val dateString = "${year}年${month}月${dayOfMonth}日"
            val id = (year.toString()+strMonth+strDay).toLong()
            val flg = date.toLong() == id

             activity?.run {
                calendaredModel =ViewModelProvider(this)[CalendarViewModel::class.java]
                 stepModel = ViewModelProvider(this)[RoomViewModel::class.java]
            } ?: throw Exception("Invalid Activity")

            launch(Dispatchers.Default) {
                val element = calendaredModel.getCalendar(id)
                val step = stepModel.getDayEntity(id)
                if (element != null) {
                    if (element.isNotEmpty()) {
                        activity?.run {
                            supportFragmentManager.beginTransaction()
                                .replace(include_frame.id, BottomSheetFragment(element, dateString,step.step,flg,calory))
                                .commit()
                        }

                        bottomsheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }
                } else if (bottomsheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomsheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}