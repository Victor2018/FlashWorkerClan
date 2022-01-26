package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.TextView
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RequiredTextView
 * Author: Victor
 * Date: 2020/12/17 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class RequiredTextView :TextView {
    private var prefix: String? = "*"
    private var prefixColor: Int = Color.RED

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        initAttr(context,attrs)
    }

    private fun initAttr(
        context: Context,
       attrs: AttributeSet?
    ) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RequiredTextView)
        prefix = ta.getString(R.styleable.RequiredTextView_prefix)
        prefixColor = ta.getInteger(R.styleable.RequiredTextView_prefix_color, Color.RED)
        var text: String? = ta.getString(R.styleable.RequiredTextView_android_text)
        if (TextUtils.isEmpty(prefix)) {
            prefix = "*"
        }
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        ta.recycle()
        setTextValue(text!!)
    }

    fun setTextValue(text: String?) {
        try {
            val span: Spannable = SpannableString(prefix + text)
            span.setSpan(
                ForegroundColorSpan(prefixColor),
                0,
                prefix?.length!!,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setText(span)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}