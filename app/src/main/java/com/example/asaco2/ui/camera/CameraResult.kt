package com.example.asaco2.ui.camera

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.example.asaco2.R
import kotlinx.android.synthetic.main.activity_camera_result.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.coroutines.CoroutineContext

class CameraResult : AppCompatActivity(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_camera_result)

        val fileDir = intent?.extras?.get("file") as Uri
        val file: File = fileDir.toFile()
        val uri = intent?.extras?.get("uri") as Uri

        frame.visibility = FrameLayout.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(frame.id, LoaderFragment())
            .commitNow()
        job(uri, file).start()
    }

    private fun job(uri: Uri, file: File) = launch(Dispatchers.Main) {
        val baseImage: String = toBase(BitmapFactory.decodeFile(file.absolutePath))

        withContext(Dispatchers.Default) {
            retrofitBuild().create(RetrofitInterface::class.java)
                .sendImage(baseImage).enqueue(object : Callback<Cook> {

                    override fun onFailure(call: Call<Cook>, t: Throwable) {
                        supportFragmentManager.beginTransaction()
                            .replace(frame.id, ImageFileFragment())
                            .commit()
                    }

                    override fun onResponse(call: Call<Cook>, response: Response<Cook>) {
                        response.body()?.let {
                            supportFragmentManager.beginTransaction()
                                .replace(frame.id, ImageSuccessFragment(uri, response.body() as Cook))
                                .commit()
                        }
                    }
                })
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}
