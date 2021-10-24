package me.jie65535.jcf.util

//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

object HttpUtil {
//    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * ### 下载图片
     */
    fun downloadImage(url: String, file: File): ByteArray {
        val request = Request.Builder().url(url).build()
        val imageByte = okHttpClient.newCall(request).execute().body!!.bytes()
        val fileParent = file.parentFile
        if (!fileParent.exists()) fileParent.mkdirs()
        file.writeBytes(imageByte)
        return imageByte
    }
}