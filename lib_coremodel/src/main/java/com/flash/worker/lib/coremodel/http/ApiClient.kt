package com.flash.worker.lib.coremodel.http

import com.flash.worker.lib.coremodel.http.adapter.DoubleNullAdapter
import com.flash.worker.lib.coremodel.http.adapter.IntegerNullAdapter
import com.flash.worker.lib.coremodel.http.adapter.LongNullAdapter
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponseAdapterFactory
import com.flash.worker.lib.coremodel.http.interceptor.BasicParamsInterceptor
import com.flash.worker.lib.coremodel.http.interceptor.LogInterceptor
import com.flash.worker.lib.coremodel.http.verifier.HttpHostNameVerifier
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.WebConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ApiClient
 * Author: Victor
 * Date: 2020/12/9 14:48
 * Description: 使用协程进行网络请求实现的网络请求客户端
 * -----------------------------------------------------------------
 */
object ApiClient {
    const val TAG = "ApiClient"
    const val TIME_OUT:Long = 30

    /**
     * 用于存储ApiService
     */
    private val map = mutableMapOf<Class<*>, Any>()

    private val basicParamsInterceptor by lazy {
        BasicParamsInterceptor.Builder()
//                .addHeaderParam("X-TOKEN","423099")
//                .addQueryParam("deviceId", "12345")
                .build()
    }

    private val okHttpClient by lazy {
        var builder = OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(LogInterceptor())
            .addInterceptor(basicParamsInterceptor)

        if (!AppConfig.MODEL_DEBUG) {
            builder.hostnameVerifier(HttpHostNameVerifier())
        }

        builder.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(WebConfig.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
                .addCallAdapterFactory(NetworkResponseAdapterFactory())
                .build()
    }

    private fun buildGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Int::class.java, IntegerNullAdapter())
            .registerTypeAdapter(Double::class.java, DoubleNullAdapter())
            .registerTypeAdapter(Long::class.java, LongNullAdapter())
            .registerTypeAdapter(Int::class.javaPrimitiveType, IntegerNullAdapter())
            .registerTypeAdapter(Double::class.javaPrimitiveType, DoubleNullAdapter())
            .registerTypeAdapter(Long::class.javaPrimitiveType, LongNullAdapter())
            .create()
    }

    /**
     * 获取ApiService单例对象
     */
    private fun <T : Any> getService(apiClass: Class<T>): T {
        //重入锁单例避免多线程安全问题
        return if (map[apiClass] == null) {
            synchronized(ApiClient::class.java) {
                val t = retrofit.create(apiClass)
                if (map[apiClass] == null) {
                    map[apiClass] = t
                }
                t
            }
        } else {
            map[apiClass] as T
        }
    }

    /**
     * 获得想要的 retrofit service
     * @param clazz    想要的 retrofit service 接口，Retrofit会代理生成对应的实体类
     * @param <T>      api service
     * @return
     */
    fun <T : Any> getApiService(apiClass: Class<T>): T {
        return getService(apiClass)
    }

    fun addHeader(deviceJson: String?) {
        basicParamsInterceptor.headerParamsMap["X-USER-DEVICE"] = deviceJson ?: ""
    }
}