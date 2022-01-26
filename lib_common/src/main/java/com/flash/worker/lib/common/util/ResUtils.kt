package com.flash.worker.lib.common.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResUtils.java
 * Author: Victor
 * Date: 2018/9/4 11:35
 * Description: 
 * -----------------------------------------------------------------
 */
object ResUtils {

    fun getStringRes(id: Int): String {
        try {
            return getResources().getString(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return ""
        }

    }

    fun getStringRes(id: Int, vararg args: Any): String {
        try {
            return getResources().getString(id, args)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 获取 String[] 值. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getStringArrayRes(id: Int): Array<String>? {
        try {
            return getResources().getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        }

    }
    /**
     * 获取 int[] 值. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getIntArrayRes(id: Int): IntArray? {
        try {
            return getResources().getIntArray(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取dimension px值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    fun getDimenPixRes(id: Int): Int {
        try {
            return getResources().getDimensionPixelOffset(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 获取dimension float形式的 px值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    fun getDimenFloatPixRes(id: Int): Float {
        try {
            return getResources().getDimension(id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1f
        }

    }

    /**
     * 获取 color 值. 如果id对应的资源文件不存在, 则返回 -1.
     *
     * @param id
     * @return
     */
    @ColorInt
    fun getColorRes(id: Int): Int {
        try {
            return ContextCompat.getColor(App.get(), id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 获取 Drawable 对象. 如果id对应的资源文件不存在, 则返回 null.
     *
     * @param id
     * @return
     */
    fun getDrawableRes(id: Int): Drawable? {
        try {
            return ContextCompat.getDrawable(App.get(), id)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取资源
     *
     * @return
     */
    fun getResources(): Resources {
        return App.get().resources
    }

    fun getDrawableByName(name: String): Int {
        return getResources().getIdentifier(name, "drawable", App.get().getPackageName())
    }

    /**
     * 根据图片名字获取Id
     */
    fun getDrawableId(name: String): Int {
        try {
            val field = R.drawable::class.java!!.getField(name)
            return field.getInt(field.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }
}