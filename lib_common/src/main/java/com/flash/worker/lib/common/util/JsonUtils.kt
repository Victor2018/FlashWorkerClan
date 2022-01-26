package com.flash.worker.lib.common.util

import android.text.TextUtils
import com.flash.worker.lib.coremodel.http.adapter.DoubleNullAdapter
import com.flash.worker.lib.coremodel.http.adapter.IntegerNullAdapter
import com.flash.worker.lib.coremodel.http.adapter.LongNullAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JsonUtils
 * Author: Victor
 * Date: 2020/7/24 上午 11:35
 * Description: 
 * -----------------------------------------------------------------
 */
object JsonUtils {
    private val mGson = buildGson()
    /**
     * 日志标签
     */
    private const val TAG = "JsonUtils"

    fun toJSONString(obj: Any?): String {
        if (obj == null) return ""
        try {
            return mGson.toJson(obj)
//            return JSON.toJSONString(obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun <T> parseObject(text: String, clazz: Class<T>?): T? {
        if (TextUtils.isEmpty(text)) return null
        if (clazz == null) return null
        try {
//            val result = JSON.parseObject(text, clazz) //mGson.fromJson(text, clazz)
            val result = mGson.fromJson(text, clazz)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> parseObject(text: String, clazz: Type?): T? {
        if (TextUtils.isEmpty(text)) return null
        if (clazz == null) return null
        try {
            return mGson.fromJson(text, clazz)
//            return JSON.parseObject(text, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> parseArray(text: String?, clazz: Class<T>?): List<T>? {
        if (TextUtils.isEmpty(text)) return null
        if (clazz == null) return null
        try {
//            return JSON.parseArray(text, clazz)

            val type = object : TypeToken<ArrayList<JsonObject>>() {}.type
            val jsonObjects: ArrayList<JsonObject> = mGson.fromJson(text, type)
            val arrayList: ArrayList<T> = ArrayList()
            for (jsonObject in jsonObjects) {
                arrayList.add(mGson.fromJson(jsonObject, clazz))
            }
            return arrayList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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

}