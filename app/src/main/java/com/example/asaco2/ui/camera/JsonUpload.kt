package com.example.asaco2.ui.camera

import android.graphics.Bitmap
import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

//const val URL = "http://192.168.3.7:8080"
const val  URL = "http://192.168.42.120:8080"
interface RetrofitInterface {
    @POST("/post")
    fun sendImage(@Body image: String?): Call<Cook>
}

//写真をBASE64にエンコードするやつ
fun toBase(bitmap: Bitmap): String {
    val bao = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao)
    val ba = bao.toByteArray()
    return Base64.encodeToString(ba, Base64.DEFAULT)
}

//レトロフィットつくるやつ
fun retrofitBuild(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(URL).client(
            OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}