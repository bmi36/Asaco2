package com.example.asaco2

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.asaco2.ui.camera.CameraResult
import com.example.asaco2.ui.gallery.GalleryFragment
import com.example.asaco2.ui.home.CalendarFragment
import com.example.asaco2.ui.tools.ToolsFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


const val CAMERA_REQUEST_CODE = 1
const val HUNTER = "梅田ひろし"


class MainActivity : AppCompatActivity(), ToolsFragment.FinishBtn {

    companion object {
        private const val REQUEST_CODE = 1000
    }

    lateinit var setFragment: Fragment
    var mSensorManager: SensorManager? = null
    var mStepCounterSensor: Sensor? = null
    lateinit var cookPrefs: SharedPreferences
    private var stepcount = 0
    lateinit var permissions: Array<String>
    lateinit var roomViewModel: RoomViewModel
    var flg = false
    private var hohaba: Double = 0.0
    var weight = 0.0
    var bind = false
    lateinit var stepService: StepService
    val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.nav_calendar,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools
            ), drawer_layout
        )
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            if (service is StepService.StepBindar) {
                bind = true
                stepService = service.getBindar()
                initdatebase()
            }
        }
    }

    fun initdatebase(): Int {
        val date =
            SimpleDateFormat("yyyyMMdd", Locale.JAPAN).format(Calendar.getInstance().time)
        val stepPrefs = getSharedPreferences("STEP", Context.MODE_PRIVATE)
        val flgPrefs = getSharedPreferences("FLAG", Context.MODE_PRIVATE)
        stepPrefs.all.map { Pair(it.key.toLong(), it.value as Int) }.forEach {
            val entity = RoomEntity(it.first, it.second)
            val flg = flgPrefs.getBoolean(date, false)
            if (flg) {
                roomViewModel.update(entity)
            } else {
                flgPrefs.edit().putBoolean(date, true).apply()
                roomViewModel.insert(entity)
            }
        }
        return roomViewModel.getStep(date.toLong())?.let {
            if (it.isEmpty()) 0 else it.last()
        } ?: 0
    }


    //    ナビゲーションの初期化
    private val navView: NavigationView by lazy {
        nav_view.apply {
            this.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_slideshow -> {
                        if (flg) takePicture()
                        action(null)
                        true
                    }
                    R.id.nav_calendar -> {
                        toolbar.title = getString(R.string.calendar)
                        action(CalendarFragment(calgary()))
                    }
                    R.id.nav_gallery -> {
                        toolbar.title = getString(R.string.hosuu)
                        action(
                            GalleryFragment(
                                initdatebase(),
                                (calgary()).toString(),
                                (hohaba * stepcount / 100000)
                            )
                        )
                    }
                    R.id.nav_tools -> {
                        toolbar.title = getString(R.string.setting)
                        action(ToolsFragment(this@MainActivity, navView))
                    }
                    else -> action(null)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)

        permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        checkPermission(permissions, REQUEST_CODE)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mStepCounterSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        title = getString(R.string.calendar)
        setSupportActionBar(toolbar)
        cookPrefs = getSharedPreferences("Cock", Context.MODE_PRIVATE)

        fab.setOnClickListener {
            //カメラボタンが押されたときになんかするやつ
            if (flg) takePicture()
            drawer_layout.closeDrawer(GravityCompat.START)

        }

        //どろわーの設定
        ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).also {
            drawer_layout.addDrawerListener(it)
            it.syncState()
        }

        roomViewModel =
            ViewModelProvider.AndroidViewModelFactory(application).create(RoomViewModel::class.java)

        val date =
            SimpleDateFormat("yyyyMMdd", Locale.JAPAN).format(Calendar.getInstance().time)

        stepcount = getSharedPreferences("STEP", Context.MODE_PRIVATE).getInt(date, 0)

        setFragment = CalendarFragment(calgary())
        setHeader(navView)
        navView.setCheckedItem(R.id.nav_calendar)
        action(CalendarFragment(calgary()))
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private lateinit var file: File

    private lateinit var uri: Uri

    //写真を取るときのやつ
    private fun takePicture() {
        val folder = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val name = SimpleDateFormat("ddHHmmss", Locale.JAPAN).format(Date()).let {
            String.format("CameraIntent_%s.jpg", it)
        }
        file = File(folder, name)
        uri = FileProvider.getUriForFile(
            applicationContext, "$packageName.fileprovider",
            file
        )

        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .putExtra(MediaStore.EXTRA_OUTPUT, uri).run {
                startActivityForResult(this, CAMERA_REQUEST_CODE)
            }
    }

    //ぱーにっしょん確認するやつ
    private fun checkPermission(permissions: Array<String>, request_code: Int) {
        ActivityCompat.requestPermissions(this, permissions, request_code)
    }

    //    パーミッションのリクエストとかのそうゆうやつ
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> for (index in permissions.indices) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) flg = true
                else {
                    flg = false
                    break
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val intentService = Intent(this, StepService::class.java)
        stopService(intentService)
        unbindService(connection)
    }


    //写真を撮った後のやつ
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                registerDatabase(file)
                val intent = Intent(this, CameraResult::class.java)
                    .putExtra("file", file.toUri())
                    .putExtra("uri", uri)
                startActivity(intent)
            }
        }
    }

    //フラグメントの切り替えのやつ
    private fun action(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction().also {
                setFragment = fragment
                it.replace(R.id.nav_host_fragment, fragment).commit()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //スライドメニューのへっだーのやつ
    private fun setHeader(navView: NavigationView) {
        getSharedPreferences("User", Context.MODE_PRIVATE).let { data ->

            LayoutInflater.from(this).inflate(R.layout.nav_header_main, navView, false)
                .run {

                    this.UserName.text = data.getString("name", HUNTER)
                    navView.addHeaderView(this)
                }
        }
    }


    //画像をDBに保存するやつ
    private fun registerDatabase(file: File) {
        val contentValues = ContentValues().also {
            it.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            it.put("_data", file.absolutePath)
        }
        this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }

    //バックボタンを押したときのやつ
    override fun onBackPressed() {
        when (navView.checkedItem?.itemId) {
            R.id.nav_calendar -> super.onBackPressed()
            else -> onClick()
        }
    }

    private fun calgary() = (stepcount.let { 1.05 * (3 * hohaba * it) * weight } / 200000).toInt()

    override fun onClick() {
        toolbar.title = getString(R.string.calendar)
        navView.setCheckedItem(R.id.nav_calendar)
        action(CalendarFragment(calgary()))
    }

    override fun onStart() {

        getSharedPreferences("User", Context.MODE_PRIVATE).let {
            val isShokai = it.getBoolean("shokai", false)
            if (!isShokai) action(ToolsFragment(this, navView)).let {
                title = getString(R.string.shokai)
                Toast.makeText(this, R.string.toast_shokika, Toast.LENGTH_LONG).show()
            }
            hohaba = ((it.getString("height", "170")?.toDouble() ?: 0.0) * 0.45)
            weight = it.getString("weight", "60")?.toDouble() ?: 0.0

            val intentService = Intent(this, StepService::class.java)
            startService(intentService)
            bindService(intentService, connection, Context.BIND_AUTO_CREATE)
            navView.getHeaderView(0).run {
                bmiText.text = getString(R.string.bmi, it.getInt("bmi", 0).toString())
                Cal.text = getString(R.string.calText, cookPrefs.getInt("calory", 0).toString())
                barn.text = getString(R.string.barnText, calgary().toString())
            }
        }
        super.onStart()
    }
}

//キーボードが消えるやつ
fun hideKeyboard(activity: Activity) {
    val view = activity.currentFocus
    if (view != null) {
        val manager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}