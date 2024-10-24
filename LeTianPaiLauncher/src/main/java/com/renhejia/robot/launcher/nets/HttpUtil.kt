package com.renhejia.robot.launcher.nets


import android.util.Log
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpUtil {
    /**
     * 发送get请求
     *
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    fun get(url: String, params: Map<String?, String?>?): String? {
        return request("get", url, params)
    }

    /**
     * 发送post请求
     *
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    fun post(url: String, params: Map<String?, String?>?): String? {
        return request("post", url, params)
    }

    /**
     * 发送http请求
     *
     * @param method 请求方法
     * @param url    地址
     * @param params 参数
     * @return 请求结果
     */
    fun request(method: String, url: String, params: Map<String?, String?>?): String? {
        require(method.isNotEmpty()) { "Request method cannot be empty" }
        require(url.isNotEmpty()) { "URL cannot be empty" }

        val httpBuilder = url.toHttpUrlOrNull()?.newBuilder() ?: throw IllegalArgumentException("Invalid URL")

        params?.forEach { (key, value) ->
            if (key != null) {
                httpBuilder.addQueryParameter(key, value)
            }
        }

        val request: Request = Request.Builder()
            .url(httpBuilder.build())
            .method(method, null) // For GET requests, the body should be null
            .build()

        return executeRequest(request)
    }

    /**
     * 发送post请求（json格式）
     *
     * @param url  url
     * @param json json字符串
     * @return 请求结果
     */
    fun postJson(url: String, json: String): String? {
        require(url.isNotEmpty()) { "URL cannot be empty" }

        val request: Request = Request.Builder()
            .url(url)
            .post(json.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): String? {
        val client = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                return response.body?.string()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("NetworkError", "Error making request", e)
            return null
        }
    }
}
