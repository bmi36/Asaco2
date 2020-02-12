package com.example.asaco2.ui.tools

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.asaco2.R
import com.example.asaco2.hideKeyboard
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_tools.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.lang.NullPointerException

class ToolsFragment(private val content: Context, private val navView: NavigationView) :
    Fragment() {

    interface FinishBtn {
        fun onClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tools, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = content.getSharedPreferences("User", Context.MODE_PRIVATE)
        val checkId: Int = prefs.getInt("sex", R.id.maleradioBtn)

        userName.setText(prefs.getString("name", "梅田ひろし"))
        weight.setText(prefs.getString("weight", 60f.toString()))
        high.setText(prefs.getString("height", 170f.toString()))
        agetext.setText(prefs.getString("age", "34"))
        Btngroup.check(checkId)

        UserButton.setOnClickListener {

            if (checkEdit()) {
                val editor = prefs.edit()
                val str = userName.text.toString()
                val wint = weight.text.toString().toDouble()
                val hint = high.text.toString().toDouble()
                val temp = (hint * hint) / 10000
                val bmi = (wint / temp).toInt()
                val age = agetext.text.toString()
                val sex = activity?.findViewById<RadioButton>(Btngroup.checkedRadioButtonId)
                val bmr = when (sex?.text.toString()) {
                    getString(R.string.mail) -> (66 + 13.7 * wint + 5.0 * hint - 6.8 * age.toInt()).toInt()
                    getString(R.string.famail) -> (665.1 + 9.6 * wint + 1.7 * hint - 7.0 * age.toInt()).toInt()
                    else -> 0
                }

                editor.putString("name", str)
                editor.putString("weight", wint.toString())
                editor.putString("height", hint.toString())
                editor.putInt("bmi", bmi)
                editor.putString("age", age)
                sex?.let { editor.putInt("sex", it.id) }
                editor.putInt("bmr", bmr)
                activity?.let {
                    navView.UserName.text = str
                    navView.bmiText.text = getString(R.string.bmi, bmi.toString())
                }

                (content as FinishBtn).onClick().run {
                    editor.apply()
                    Toast.makeText(activity, R.string.conp, Toast.LENGTH_SHORT).show()
                }
            }
            activity?.let { hideKeyboard(it) }
        }
    }

    private fun checkEdit(): Boolean {
        val errorAction = getString(R.string.Error)
        var isOk = true

        if (userName.text.isEmpty()) userName.error = errorAction.also { isOk = false }
        if (weight.text.isEmpty()) weight.error = errorAction.also { isOk = false }
        if (high.text.isEmpty()) high.error = errorAction.also { isOk = false }
        if (agetext.text.isEmpty()) agetext.error = errorAction.also { isOk = false }

        return isOk
    }
}