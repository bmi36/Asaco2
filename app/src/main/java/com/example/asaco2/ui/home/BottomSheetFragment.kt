package com.example.asaco2.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import com.example.asaco2.R
import kotlinx.android.synthetic.main.fragment_included.*
import kotlinx.android.synthetic.main.fragment_included.view.*
import java.util.Calendar
import kotlin.math.roundToInt


class BottomSheetFragment(
    private val list: List<CalendarEntity>,
    private val dayString: String,
    private val step: Int,
    private val flag: Boolean,
    private val calory: Int
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_included, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var total = 0

        recyclerView.run {
            setHasFixedSize(true)
            adapter = activity?.applicationContext?.let { FoodAdapter(it, list.toTypedArray()) }
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }

        val prefs = activity?.getSharedPreferences("User", Context.MODE_PRIVATE)
        val height = prefs?.getString("height", "0f")?.toDouble() ?: 0.0
        val weight = prefs?.getString("weight", "0f")?.toDouble() ?: 0.0

        val hohaba = (height * 0.45)
        val walkcalorie =(step.let { 1.05 * (3 * hohaba * it) * weight } / 200000).toInt()


        for (element in list) {
            total += element.absorption as Int
        }

        nowText.text = dayString
        sumText.text = getString(R.string.sumText, total.toString())
        walkText.text = getString(R.string.walkCalText, step.toString())
        walkCalText.text = getString(R.string.kcal, calory.toString())

        val bmr = prefs?.getInt("bmr", 0) ?: 0
        val difference = if (flag)
            total - walkcalorie.toInt() else total - walkcalorie.toInt() - bmr

        totalCalText.text = getString(R.string.total, difference.toString())

    }
}