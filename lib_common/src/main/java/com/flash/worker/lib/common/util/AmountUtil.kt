package com.flash.worker.lib.common.util

import android.text.TextUtils
import java.math.BigDecimal
import java.text.DecimalFormat


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AmountUtil
 * Author: Victor
 * Date: 2021/1/21 19:56
 * Description: 金额转换工具
 * -----------------------------------------------------------------
 */
object AmountUtil {

    val TAG = "AmountUtil"
    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return
     */
    fun getRoundUp(value: Double?, digit: Int): String? {
        var result = "0.0"
        try {
            var inputValue = value.toString()
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            result =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun getRoundUpDouble(value: Double?, digit: Int): Double {
        var result = 0.0
        try {
            var inputValue = value.toString()
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            result =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun getRoundUpString(value: Double?, digit: Int): String {
        var result = "0.0"
        try {
            var inputValue = value.toString()
            if (TextUtils.isEmpty(inputValue)) {
                inputValue = "0.0"
            }
            val bigDecimal = BigDecimal(inputValue)
            result =  bigDecimal.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    /**
     * 将每三个数字加上逗号处理,最后保留两位小数（通常使用金额方面的编辑）示例：9，702.44
     *
     * @param str
     * @return
     */
    fun addCommaDots(value: Double?): String? {
        var result = "0.00"
        try {
            val myformat = DecimalFormat()
            myformat.applyPattern(",##0.00")
            result = myformat.format(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getEvaluationCount(value: Int): String? {
        try {
            if (value < 1000) return value.toString()

            if (value % 1000.0 > 0) {
                val bigDecimal = BigDecimal((value / 1000.0).toString())
                val result: Double = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
                return "${result}k"
            }

            return "${value / 1000}K"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}