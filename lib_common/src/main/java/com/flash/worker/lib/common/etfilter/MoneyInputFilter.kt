package com.flash.worker.lib.common.etfilter

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import com.flash.worker.lib.common.util.Loger
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MoneyInputFilter
 * Author: Victor
 * Date: 2021/2/2 10:16
 * Description: 输入金额时的文本过虑器，可设置最大输入金额、小数点后位数
 * -----------------------------------------------------------------
 */
class MoneyInputFilter: InputFilter {
    val TAG = "MoneyInputFilter"
    /**
     * 正则表达式：以0或正整数开头后跟0或1个(小数点后面跟0到2位数字)
     */
    private val FORMAT = "^(0|[1-9]\\d*)(\\.\\d{0,%s})?$"

    /**
     * 正则表达式：0-9.之外的字符
     */
    private val SOURCE_PATTERN: Pattern = Pattern.compile("[^0-9.,]")

    /**
     * 默认保留小数点后2位
     */
    private var mPattern: Pattern = Pattern.compile(String.format(FORMAT, "2"))

    /**
     * 允许输入的最大金额
     */
    private var maxValue = Int.MAX_VALUE.toDouble()

    /**
     * 设置保留小数点后的位数，默认保留2位
     *
     * @param length
     */
    fun setDecimalLength(length: Int) {
        mPattern = Pattern.compile(String.format(FORMAT, length))
    }

    /**
     * 设置允许输入的最大金额
     *
     * @param maxValue
     */
    fun setMaxValue(maxValue: Double) {
        this.maxValue = maxValue
    }

    /**
     * 当系统使用source的start到end的字串替换dest字符串中的dstart到dend位置的内容时，会调用本方法
     *
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0（删除时例外）
     * @param end    新输入的字符串终点下标，一般为source长度-1（删除时例外）
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为dest长度（删除时例外）
     * @param dend   原内容终点坐标，一般为dest长度（删除时例外）
     * @return 你希望输入的内容，比如当新输入的字符串为“恨”时，你希望把“恨”变为“爱”，则return "爱"
     */
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        Loger.e(TAG,"filter-source = $source")
        Loger.e(TAG,"filter-start = $start")
        Loger.e(TAG,"filter-end = $end")
        Loger.e(TAG,"filter-dest = $dest")
        Loger.e(TAG,"filter-dstart = $dstart")
        Loger.e(TAG,"filter-dend = $dend")
        // 删除时不用处理
        if (TextUtils.isEmpty(source)) {
            return null
        }

        // 不接受数字、小数点之外的字符
        if (SOURCE_PATTERN.matcher(source).find()) {
            return ""
        }
        val ssb = SpannableStringBuilder(dest)
        ssb.replace(dstart, dend, source, start, end)

        Loger.e(TAG,"filter-ssb = $ssb")

        //如果第一个输入的是"."则返回"0."
        if (TextUtils.equals(".",ssb)) {
            return "0."
        }

        val matcher: Matcher = mPattern.matcher(ssb)

        if (source?.contains(",")!!) {
            return source
        } else {
            return if (matcher.find()) {
                val str: String = matcher.group()
                Log.e(TAG,"匹配到的字符串=$str")

                //验证输入金额的大小
                val value = str.toDouble()
                if (value > maxValue) {
                    ""
                } else source
            } else {
                Log.e(TAG,"不匹配的字符串=" + ssb.toString())
                ""
            }
        }

    }

}