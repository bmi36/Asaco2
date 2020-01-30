package com.example.asaco2.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.asaco2.R
import com.example.asaco2.StepViewModel
import com.example.asaco2.today
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.module_calory.*
import kotlinx.android.synthetic.main.module_distance.*
import kotlinx.android.synthetic.main.module_hosuu.*
import kotlinx.android.synthetic.main.walk_statu_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class GalleryFragment(private val stepCount: Int, private val calory: String, private val dis: Double) : Fragment(),

    CoroutineScope {

    private lateinit var viewModel: StepViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[StepViewModel::class.java]

        dayText.text = today
        hosuu_text.text = "$stepCount"
        calory_text.text = "${calory}kcal"
        distance_text.text = "${String.format("%.1f", dis)}kcal"

        listener(7)
        dayBtn.setOnClickListener { listener(7) }
        monthBtn.setOnClickListener { listener(12) }
    }

    private fun listener(size: Int) {
        val search: String = when (size) {
            7 -> day
            else -> month
        }

        launch(Default) {
            //        リストの生成（1週間or12か月）
            val list: List<Float>? = when (size) {
                7 -> viewModel.getStep(search.replaceInt().toLong())?.map { it.toFloat() }
                12 -> viewModel.getmonth(search.replaceInt().toLong()).map { it.toFloat() }
                else -> null
            }

            //            グラフの表示
            childFragmentManager.beginTransaction()
                .replace(frame.id, GraphFragment(list?.toTypedArray(),size,search)).commit()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()

}

private val currentTimeMillis = Date(System.currentTimeMillis())

@SuppressLint("SimpleDateFormat")
private val day: String = SimpleDateFormat("yyyy/MM/dd").run { format(currentTimeMillis) }

@SuppressLint("SimpleDateFormat")
private val month: String = SimpleDateFormat("yyyy/MM").run { format(currentTimeMillis) }

private fun String.replaceInt() = this.replace("/", "").toInt()