package com.example.asaco2.ui.camera

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.asaco2.R
import com.example.asaco2.ui.home.CalendarEntity
import com.example.asaco2.ui.home.CalendarViewModel
import kotlinx.android.synthetic.main.image_success_fragment.*
import java.util.*

class ImageSuccessFragment(private val uri: Uri, private val cook: Cook) : Fragment() {

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.image_success_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
           ViewModelProvider(this)[CalendarViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        cameraImage.run {
            setImageURI(uri)
            adjustViewBounds = true
        }

        cooknametext.setText(cook.foodname, TextView.BufferType.EDITABLE)
        caltext.setText(cook.calorie.toString(), TextView.BufferType.EDITABLE)
        okbutton.setOnClickListener(lister)
    }

    val lister = View.OnClickListener {
        var enp = true
        if (cooknametext.text.isEmpty()) cooknametext.error = getString(R.string.errortext).also { enp = false }
        if(caltext.text.isEmpty()) caltext.error = getString(R.string.errortext).also { enp = false }

        if (enp) {
            val pref = activity?.run { getSharedPreferences("Cock", Context.MODE_PRIVATE) }
            val calorie = pref?.let { it.getInt("calory", 0) + cook.calorie } ?: 0
            val cookStr = cooknametext.text.toString()
            val calorieStr = caltext.text.toString().toInt()
            val timeStamp =
                SimpleDateFormat("yyyyMMddhhmmss").run { format(Date(System.currentTimeMillis())) }
                    .toLong()
            pref?.edit()?.putInt("calory", calorie)?.apply()
            CalendarEntity(timeStamp, cookStr, calorieStr).let { viewModel.insert(it) }
            Toast.makeText(activity, R.string.conp, Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
    }
}