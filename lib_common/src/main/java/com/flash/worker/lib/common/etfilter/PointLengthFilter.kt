package com.flash.worker.lib.common.etfilter

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PointLengthFilter
 * Author: Victor
 * Date: 2021/1/12 9:19
 * Description: 
 * -----------------------------------------------------------------
 */
class PointLengthFilter(digitsBeforeZero: Int, digitsAfterZero: Int): InputFilter {
    var regex: String  = String.format("[0-9]{0,%d}+(\\.[0-9]{0,%d})?", digitsBeforeZero, digitsAfterZero)
    var mPattern = Pattern.compile(regex)

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        //直接输入"."返回"0."
        //".x"删除"x"输出为"."，inputFilter无法处理成"0."，所以只处理直接输入"."的case
        if (".".equals(source) && "".equals(dest.toString())) {
            return "0."
        }
        var builder = StringBuilder(dest!!)
        if ("".equals(source)) {
            builder.replace(dstart, dend, "")
        } else {
            builder.insert(dstart, source)
        }
        var resultTemp: String  = builder.toString()
        //判断修改后的数字是否满足小数格式，不满足则返回 "",不允许修改
        var matcher = mPattern.matcher(resultTemp)
        if (!matcher.matches()) {
            return ""
        }
        return null

    }
}