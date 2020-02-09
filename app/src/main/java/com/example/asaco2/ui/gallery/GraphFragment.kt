package com.example.asaco2.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.asaco2.EnamDate
import com.example.asaco2.R
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.graph_fragment.*
import kotlinx.coroutines.*
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class GraphFragment(
    private val list: Array<Float>?, private val size: EnamDate?
) : Fragment(), CoroutineScope {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.graph_fragment, container, false)

    private val barChar: ArrayList<BarEntry> = ArrayList()
    private val nameList: ArrayList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            action()
            barChar.asReversed().let {
                if (it.size != 0) {
                    val barDataSet = BarDataSet(it, "Cels")
                    val barData = BarData(nameList.asReversed(), barDataSet)
                    chart.run {
                        data = barData
                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
                    }
                }
            }
        }
        chart.animateY(1000)
    }

    //    表示するやつを作るやつ
    private fun action() {
        list?.let {
            if (it.isNotEmpty()) {
                for (i in 0..it.lastIndex) {
                    try {
                        addlist(i, it[i])
                    } catch (e: NullPointerException) {
                        break
                    }
                }
            }
        }
    }

    fun addlist(index: Int, element: Float) {

        val calendar = Calendar.getInstance()
        var strDate: String
        val unit = when (size) {
            EnamDate.DAY -> getString(R.string.Day).also {
                strDate = SimpleDateFormat(
                    "d",
                    Locale.JAPAN
                ).run { format(calendar.apply { add(Calendar.DATE, -index) }.time) }
            }

            EnamDate.MONTH -> getString(R.string.Month).also {
                strDate = SimpleDateFormat(
                    "M",
                    Locale.JAPAN
                ).run { format(calendar.apply { add(Calendar.MONTH, -index) }.time) }
            }
            else -> getString(R.string.Day).also {
                strDate = SimpleDateFormat(
                    "d",
                    Locale.JAPAN
                ).run { format(calendar.apply { add(Calendar.DATE, -index) }.time) }
            }
        }

        barChar.add(BarEntry(element, index))
        nameList.add(index, "$strDate$unit")
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}