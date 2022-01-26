package com.flash.worker.lib.coremodel.http.interceptor

import android.util.Log
import com.flash.worker.lib.coremodel.util.AppConfig
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LogInterceptor
 * Author: Victor
 * Date: 2020/12/3 9:46
 * Description: 日志拦截器
 * -----------------------------------------------------------------
 */
class LogInterceptor: Interceptor {
    val TAG = "LogInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (AppConfig.MODEL_DEBUG) {
            Log.e(TAG, "request url = ${request.url}")
            Log.e(TAG, "request headers = ${Gson().toJson(request.headers)}")
        }

        val requestBody = request.body
        val buffer = Buffer()
        requestBody?.writeTo(buffer)
        var charset = Charset.forName("UTF-8")
//                val contentType = requestBody?.contentType()
//                var charset = contentType?.charset(UTF8)
        val parm: String = buffer.readString(charset)

        if (AppConfig.MODEL_DEBUG) {
            Log.e(TAG, "request parm = $parm")
            Log.e(TAG, "request requestMethod = ${request.method}")
        }

        var response = chain.proceed(request)

        if (AppConfig.MODEL_DEBUG) {
            Log.e(TAG,"repsonse url = ${request.url}")
            Log.e(TAG,"repsonse code = ${response.code}")
            //response.peekBody不会关闭流
            Log.e(TAG,"responseData = ${response.peekBody(1024)?.string()}")
        }

        return response
    }
}