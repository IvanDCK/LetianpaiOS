package com.renhejia.robot.launcher.nets

import android.content.Context
import com.google.gson.Gson
import com.renhejia.robot.guidelib.utils.SystemUtil.ltpSn
import com.renhejia.robot.launcher.encryption.EncryptionUtils.Companion.robotMac
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * @author liujunbin
 */
class GeeUINetworkUtil private constructor(context: Context) {
    private var mContext: Context? = null
    private var gson: Gson? = null
    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
    }

    // 创建一个 HashMap 对象
    var map: HashMap<String, Int> = HashMap()

    init {
        init(context)
    }

    companion object {
        private var instance: GeeUINetworkUtil? = null
        private const val AUTHORIZATION = "Authorization"

        fun getInstance(context: Context): GeeUINetworkUtil {
            synchronized(GeeUINetworkUtil::class.java) {
                if (instance == null) {
                    instance = GeeUINetworkUtil(context.applicationContext)
                }
                return instance!!
            }
        }

        fun get(uri: String, callback: Callback) {
            val okHttpClient = OkHttpClient()
            val request: Request = Request.Builder() // Use Request.Builder
                .get()
                .url("https://yourservice.com$uri")
                .build()
            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
           
        }

        fun get1(context: Context?, uri: String, callback: Callback) {
            val okHttpClient = OkHttpClient()
            val httpBuilder = "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder() 
            val sn = ltpSn
            httpBuilder.addQueryParameter("sn", sn)

            val request: Request = Request.Builder() 
                .url(httpBuilder.build())
                .build()

            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }

        fun get(context: Context?, uri: String, hashMap: HashMap<*, *>?, callback: Callback) {
            val okHttpClient = OkHttpClient()
            val httpBuilder = "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder() 
            val sn = ltpSn

            //
            //
            // for(HashMap.Entry<String,String> entry : hashMap.entrySet()){
            //
            // }
            //

            httpBuilder.addQueryParameter("sn", sn)

            val request: Request = Request.Builder() // Use Request.Builder
                .url(httpBuilder.build())
                .build()

            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
            
        }

        // 添加一些键值对
        // map.put("key1", 1);
        // map.put("key2", 2);
        // map.put("key3", 3);
        //
        //// 循环遍历 HashMap
        // for (Map.Entry<String, Integer> entry : map.entrySet()) {
        // // 获取键和值
        // String key = entry.getKey();
        // int value = entry.getValue();
        //
        // // 输出键值对
        // System.out.println("Key: " + key + ", Value: " + value);
        // }
        fun get(context: Context?, uri: String, callback: Callback) {
            val sn = ltpSn
            val authorization = ""
            get1(context, sn, authorization, uri, callback)
        }

        fun get1(context: Context?, sn: String?, key: String?, uri: String, callback: Callback) {
            val okHttpClient = OkHttpClient()
            val httpBuilder = "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder()

            httpBuilder.addQueryParameter("sn", sn)
            httpBuilder.addQueryParameter("sn", sn) 

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .build()

            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }

        fun post(uri: String, hashMap: HashMap<*, *>, callback: Callback) {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .callTimeout(10, TimeUnit.MINUTES)
                .build()

            val url = "https://yourservice.com$uri"
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = Gson().toJson(hashMap).toRequestBody(mediaType)
            val request: Request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(callback)
        }

        fun get11(
            context: Context?,
            auth: String,
            sn: String?,
            ts: String?,
            uri: String,
            callback: Callback
        ) {
            val okHttpClient = OkHttpClient()
            val httpBuilder = "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder()

            httpBuilder.addQueryParameter("sn", sn)
            httpBuilder.addQueryParameter("ts", ts)

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .header(AUTHORIZATION, auth) // Use header() instead of addHeader()
                .build()

            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }

        fun get11(context: Context?, auth: String, ts: String?, uri: String, callback: Callback) {
            val okHttpClient = OkHttpClient()
            val httpBuilder = "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder()
            val mac = robotMac

            httpBuilder.addQueryParameter("mac", mac)
            httpBuilder.addQueryParameter("ts", ts)

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .header(AUTHORIZATION, auth) // Use header() instead of addHeader()
                .build()

            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }
    }
}
