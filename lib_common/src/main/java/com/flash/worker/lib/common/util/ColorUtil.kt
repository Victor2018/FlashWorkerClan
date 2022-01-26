package com.flash.worker.lib.common.util

import android.graphics.Color
import androidx.annotation.ColorInt
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import java.util.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ColorUtil
 * Author: Victor
 * Date: 2020/7/8 下午 06:04
 * Description: 
 * -----------------------------------------------------------------
 */
object ColorUtil {
    fun getDefaultRandomColor(): Int {
//        val colors = intArrayOf(
//            ResUtils.getColorRes(R.color.color_E6FCFC),
//            ResUtils.getColorRes(R.color.color_E6FBFD),
//            ResUtils.getColorRes(R.color.color_E5FAFE),
//            ResUtils.getColorRes(R.color.color_E5F9FE))

        val random = Random()
//        return colors[random.nextInt(colors.size)]
        var color = Color.argb(255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256))

        return color
    }

    /**
     * Set the alpha component of `color` to be `alpha`.
     */
    fun modifyAlpha(@ColorInt color: Int, alpha: Int): Int {
        return color and 0x00ffffff or (alpha shl 24)
    }

    /**
     * Set the alpha component of `color` to be `alpha`.
     */
    fun modifyAlpha(@ColorInt color: Int, alpha: Float): Int {
        return modifyAlpha(color, (255f * alpha).toInt())
    }

    fun getColorByHashCode (hashCode: Int): Int {
        val colors = ResUtils.getIntArrayRes(R.array.search_filter_colors)
        val color: Int = colors?.get(hashCode % colors?.size)!!
        return color
    }

    /**
     * 颜色透明度16进制计算
     * 255 * 不透明度 -> 转换成16进制数
     * @param alpha 不透明度
     */
    fun calcuColorAlpha (alpha: Int): String {
        var temp = 255 * alpha * 1.0f / 100f
        var alpha = Math.round(temp)
        var hexStr = Integer.toHexString(alpha)
        if (hexStr.length < 2) {
            hexStr = "0$hexStr"
        }
        return "$alpha% = ${hexStr.toUpperCase()}"
    }
}