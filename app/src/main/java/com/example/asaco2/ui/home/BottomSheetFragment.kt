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

        for (element in list) { total += element.absorption as Int }

        nowText.text = dayString
        sumText.text = getString(R.string.sumText,total.toString())
        walkText.text = getString(R.string.walkCalText,step.toString())
        walkCalText.text = getString(R.string.kcal,calory.toString())

        val bmr = activity?.getSharedPreferences("User",Context.MODE_PRIVATE)?.getInt("bmr",0) ?: 0

        val difference = if (flag){
            total - step - bmr
        }else{ total - step}

        totalCalText.text = getString(R.string.total,difference.toString())

    }
}