package com.flash.worker.lib.common.view.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DotLineView
 * Author: Victor
 * Date: 2021/9/28 16:57
 * Description: 
 * -----------------------------------------------------------------
 */
class DotLineView: View {
    val ORIENTATION_HORIZONTAL = 0
    val ORIENTATION_VERTICAL = 1
    private var mPaint: Paint? = null
    private var orientation = 0

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context,attrs)
    }

    fun initAttrs (context: Context, attrs: AttributeSet?) {
        val dashGap: Int
        val dashLength: Int
        val dashThickness: Int
        val color: Int

        val a: TypedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.DotLineView, 0, 0)

        try {
            dashGap = a.getDimensionPixelSize(R.styleable.DotLineView_dashGap, 5)
            dashLength = a.getDimensionPixelSize(R.styleable.DotLineView_dashLength, 5)
            dashThickness = a.getDimensionPixelSize(R.styleable.DotLineView_dashThickness, 3)
            color = a.getColor(R.styleable.DotLineView_divider_line_color, -0x1000000)
            orientation =
                a.getInt(R.styleable.DotLineView_divider_orientation, ORIENTATION_HORIZONTAL)
        } finally {
            a.recycle()
        }

        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = color
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = dashThickness.toFloat()
        mPaint!!.pathEffect = DashPathEffect(floatArrayOf(dashGap.toFloat(), dashLength.toFloat()), 0f)
    }

    fun setBgColor(color: Int) {
        mPaint?.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        if (orientation === ORIENTATION_HORIZONTAL) {
            val center = height * 0.5f
            canvas?.drawLine(0f, center, width.toFloat(), center, mPaint!!)
        } else {
            val center = width * 0.5f
            canvas?.drawLine(center, 0f, center, height.toFloat(), mPaint!!)
        }
    }
}