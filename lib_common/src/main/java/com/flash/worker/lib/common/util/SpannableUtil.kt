package com.flash.worker.lib.common.util

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.flash.worker.lib.common.view.widget.URLSpanNoUnderline


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SpannableUtil
 * Author: Victor
 * Date: 2020/12/9 19:31
 * Description: 
 * -----------------------------------------------------------------
 */
object SpannableUtil {

    /**
     * 设置不同颜色
     */
    fun setSpannableColor(textView: TextView,color: Int,text: String,spanText: String) {
        val mColorBase = color
        val mSearchCount = getWordCount(spanText)
        val spannableString = SpannableString(text)
        var index = 0
        while (index != -1) {
            index = text.indexOf(spanText, index)
            if (index == -1) break
            spannableString.setSpan(
                ForegroundColorSpan(mColorBase),
                index,
                (index + mSearchCount).also { index = it },
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannableString
    }

    fun setSpannableColor(textView: TextView,color: Int,text: String,spanTexts: List<String>) {
        val mColorBase = color
        val spannableString = SpannableString(text)

        for (i in spanTexts.indices) {
            var spanText = spanTexts.get(i)
            val redCount = getWordCount(spanText)

            var index = 0
            while (index != -1) {
                index = text.indexOf(spanText, index)
                if (index == -1) break
                spannableString.setSpan(
                    ForegroundColorSpan(mColorBase),
                    index,
                    (index + redCount).also { index = it },
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }

        textView.text = spannableString
    }

    /**
     * 添加下划线
     */
    fun setSpannableUnderline(textView: TextView,text: String,spanText: String) {
        if (textView == null) return
        if (TextUtils.isEmpty(text)) return
        if (TextUtils.isEmpty(spanText)) return

        val mUnderlineCount = getWordCount(spanText)
        val spannableString = SpannableString(text)

        var underlineIndex = 0
        while (underlineIndex != -1) {
            underlineIndex = text.indexOf(spanText, underlineIndex)
            if (underlineIndex == -1) break
            spannableString.setSpan(
                UnderlineSpan(),
                underlineIndex,
                (underlineIndex + mUnderlineCount).also { underlineIndex = it },
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = spannableString
    }

    fun stripUnderlines(textView: TextView) {
        if (null != textView && textView.text is Spannable) {
            val s: Spannable = textView.text as Spannable
            val spans: Array<URLSpan> = s.getSpans(0, s.length, URLSpan::class.java)
            for (span in spans) {
                val start: Int = s.getSpanStart(span)
                val end: Int = s.getSpanEnd(span)
                s.removeSpan(span)
                var mSpan = URLSpanNoUnderline(span.url)
                s.setSpan(mSpan, start, end, 0)
            }
            textView.text = s
        }
    }

    fun getWordCount(s: String): Int {
        var s = s
        s = s.replace("[\\u4e00-\\u9fa5]".toRegex(), "*")
        return s.length
    }
}