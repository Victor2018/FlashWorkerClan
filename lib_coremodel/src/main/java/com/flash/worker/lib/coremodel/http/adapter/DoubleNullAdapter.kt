package com.flash.worker.lib.coremodel.http.adapter

import android.text.TextUtils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DoubleNullAdapter
 * Author: Victor
 * Date: 2021/7/7 14:47
 * Description: 
 * -----------------------------------------------------------------
 */
class DoubleNullAdapter: TypeAdapter<Double>() {

    override fun write(writer: JsonWriter?, value: Double?) {
        if (value == null) {
            writer?.nullValue()
            return
        }
        writer?.value(value)
    }

    override fun read(reader: JsonReader?): Double? {
        if (reader?.peek() === JsonToken.NULL) {
            reader?.nextNull()
            return 0.0
        }
        val stringValue = reader?.nextString() ?: ""
        if (TextUtils.isEmpty(stringValue)) return null
        return try {
            stringValue.toDouble()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            null
        }
    }
}