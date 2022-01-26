package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.TextView


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FitTextView
 * Author: Victor
 * Date: 2021/5/18 10:36
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class FitTextView: TextView {
    private var mTextPaint: Paint? = null
    private var mMaxTextSize = 0f// 获取当前所设置文字大小作为最大文字大小
    private val mMinTextSize = 3f

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        refitText(text.toString(), this.getHeight())   //textview视图的高度
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw) {
            refitText(this.getText().toString(), w)
        }
    }

    private fun initialize() {
        mTextPaint = TextPaint()
        mTextPaint?.set(this.paint)
        // 最大的大小默认为特定的文本大小，除非它太小了
        mMaxTextSize = this.textSize
    }

    /**
     * Resize the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     *
     */
    private fun refitText(text: String, textWidth: Int) {
        if (textWidth > 0) {
            val availableWidth = textWidth - this.paddingLeft - this.paddingRight
            var trySize = mMaxTextSize
            mTextPaint!!.textSize = trySize
            while (mTextPaint!!.measureText(text) > availableWidth) {
                trySize -= 1f
                if (trySize <= mMinTextSize) {
                    trySize = mMinTextSize
                    break
                }
                mTextPaint!!.textSize = trySize
            }

            // setTextSize参数值为sp值
            textSize = px2sp(context, trySize)
        }
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, pxValue: Float): Float {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return pxValue / fontScale
    }


}