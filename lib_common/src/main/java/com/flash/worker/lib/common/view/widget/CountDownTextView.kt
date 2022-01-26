package com.flash.worker.lib.common.view.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnCountDownFinishListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CountDownTextView
 * Author: Victor
 * Date: 2021/6/7 14:36
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class CountDownTextView: TextView {
    // 倒计时
    private val duration: Long = 2000

    // 扫过的角度
    private var mSweepAngle = 360f
    private var animator: ValueAnimator? = null
    private var mRect: RectF = RectF()
    private var mBackgroundPaint: Paint? = null
    private var mNormalBackgroundPaint: Paint? = null
    var mOnCountDownFinishListener: OnCountDownFinishListener? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        mNormalBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mNormalBackgroundPaint?.color = ResUtils.getColorRes(R.color.color_FAEFD5)
        mNormalBackgroundPaint?.strokeWidth = DensityUtil.dip2px(context,
                ResUtils.getDimenFloatPixRes(R.dimen.dp_2)).toFloat()
        mNormalBackgroundPaint?.style = Paint.Style.STROKE

        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint?.color = ResUtils.getColorRes(R.color.color_FFCE24)
        mBackgroundPaint?.strokeWidth = DensityUtil.dip2px(context,
                ResUtils.getDimenFloatPixRes(R.dimen.dp_2)).toFloat()
        mBackgroundPaint?.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        var padding = DensityUtil.dip2px(context,
                ResUtils.getDimenFloatPixRes(R.dimen.dp_1)).toFloat()
        mRect.top = padding
        mRect.left = padding
        mRect.right = canvas.getWidth() - padding
        mRect.bottom = canvas.getHeight() - padding

        // 画倒计时线默认内圆
        canvas.drawArc(mRect,
                -90f,
                360f,
                false,
                mNormalBackgroundPaint!!)

        // 画倒计时线内圆
        canvas.drawArc(mRect, //弧线所使用的矩形区域大小
                -90f,  //开始角度
                360-mSweepAngle, //扫过的角度
                false, //是否使用中心
                mBackgroundPaint!!)

        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }

    /**
     * 开始倒计时
     */
    fun start() {
        // 在动画中
        if (mSweepAngle != 360f) return
        mSweepAngle = 360f
        animator = ValueAnimator.ofFloat(mSweepAngle).setDuration(duration.toLong())
        animator?.setInterpolator(LinearInterpolator())
        animator?.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mSweepAngle = value
            invalidate()

            if (value == 360f) {
                mOnCountDownFinishListener?.OnCountDownFinish()
            }
        })
        animator?.start()
    }

    fun reset() {
        mSweepAngle = 360f
        animator = null
    }

    fun pause() {
        animator?.pause()
    }

    fun resume() {
        if (isPaused()) {
            animator?.start()
        }
    }

    fun stop() {
        animator?.cancel()
        animator = null
    }

    fun isPaused (): Boolean {
        return animator?.isPaused ?: false
    }

}