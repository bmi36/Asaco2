package com.example.asaco2.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.asaco2.R


class FoodAdapter(
    val context: Context,
    private val list: Array<CalendarEntity>

) : RecyclerView.Adapter<FoodListViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        val view = inflater.inflate(R.layout.food_list_row, parent, false)
        val holder = FoodListViewHolder(view)
        view.tag = holder
        return holder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) =
        list[position].let {
            holder.foodText.text = it.food
            holder.calText.text =  context.getString(R.string.kcal,it.absorption.toString())
        }
}

class FoodListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val foodText: TextView = view.findViewById(R.id.foodText)
    val calText: TextView = view.findViewById(R.id.foodCalText)

}