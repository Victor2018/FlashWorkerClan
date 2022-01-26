package com.flash.worker.lib.common.view.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import com.flash.worker.lib.common.util.Loger
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.regex.Pattern


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NumberAnimTextView
 * Author: Victor
 * Date: 2021/2/3 20:05
 * Description: 
 * -----------------------------------------------------------------
 */
@SuppressLint("AppCompatCustomView")
class NumberAnimTextView: TextView {

    /**
     * 起始值 默认 0
     */
    private var mNumStart = "0"

    /**
     * 结束值
     */
    private var mNumEnd: String? = null

    /**
     * 动画总时间 默认 2000 毫秒
     */
    var mDuration: Long = 2000

    /**
     * 前缀
     */
    private val mPrefixString = ""

    /**
     * 后缀
     */
    private val mPostfixString = ""

    /**
     * 是否开启动画
     */
    private var mIsEnableAnim = true

    /**
     * 是否是整数
     */
    private var isInt = false
    private var animator: ValueAnimator? = null

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    fun setNumberString(number: String) {
        setNumberString("0", number)
    }

    fun setNumberString(numberStart: String, numberEnd: String) {
        mNumStart = numberStart
        mNumEnd = numberEnd

        if (checkNumString(numberStart, numberEnd)) {
            // 数字合法　开始数字动画
            Loger.e(javaClass.simpleName,"checkNumString-true")
            start()
        } else {
            Loger.e(javaClass.simpleName,"checkNumString-false")
            // 数字不合法　直接调用　setText　设置最终值
            text = mPrefixString + numberEnd + mPostfixString
        }
    }

    fun setEnableAnim(enableAnim: Boolean) {
        mIsEnableAnim = enableAnim
    }

    /**
     * 校验数字的合法性
     *
     * @param numberStart 　开始的数字
     * @param numberEnd   　结束的数字
     * @return 合法性
     */
    private fun checkNumString(
        numberStart: String,
        numberEnd: String
    ): Boolean {
        val regexInteger = "-?\\d*"
        var mNumberIntPattern = Pattern.compile(regexInteger)

        isInt = mNumberIntPattern.matcher(numberEnd).matches()
                && mNumberIntPattern.matcher(numberStart).matches()

        if (isInt) {
            return true
        }
//        val regexDecimal = "-?[1-9]\\d*.\\d*|-?0.\\d*[1-9]\\d*"
        //金额带逗号匹配支持
        val regexDecimal = "^([0-9]+|[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{1,2})?\$"
        var mNumberDecimalPattern = Pattern.compile(regexDecimal)
        if ("0" == numberStart) {
            if (mNumberDecimalPattern.matcher(numberEnd).matches()) {
                return true
            }
        }
        return if (mNumberDecimalPattern.matcher(numberEnd).matches()
            && mNumberDecimalPattern.matcher(numberStart).matches()) {
            true
        } else false
    }

    private fun start() {
        if (!mIsEnableAnim) {
            // 禁止动画
            text = mPrefixString + format(BigDecimal(mNumEnd?.replace(",",""))) + mPostfixString
            return
        }
        animator = ValueAnimator.ofObject(
            BigDecimalEvaluator(),
            BigDecimal(mNumStart),
            BigDecimal(mNumEnd?.replace(",",""))
        )
        animator?.setDuration(mDuration)
        animator?.setInterpolator(AccelerateDecelerateInterpolator()) //先加速在减速
        animator?.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            val value =
                valueAnimator.animatedValue as BigDecimal
            text = mPrefixString + format(value) + mPostfixString
        })
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                text = mPrefixString + mNumEnd + mPostfixString
            }
        })
        animator?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animator != null) {
            animator?.cancel()
        }
    }

    /**
     * 格式化 BigDecimal ,小数部分时保留两位小数并四舍五入
     *
     * @param bd 　BigDecimal
     * @return 格式化后的 String
     */
    private fun format(bd: BigDecimal): String {
        val pattern = StringBuilder()
        if (isInt) {
            pattern.append("#,###")
        } else {
            var length = 0
            val s1 = mNumStart.split("\\.".toRegex()).toTypedArray()
            val s2 = mNumEnd!!.split("\\.".toRegex()).toTypedArray()
            val s = if (s1.size > s2.size) s1 else s2
            if (s.size > 1) {
                // 小数部分
                val decimals = s[1]
                if (decimals != null) {
                    length = decimals.length
                }
            }
            pattern.append("#,##0")
            if (length > 0) {
                pattern.append(".")
                for (i in 0 until length) {
                    pattern.append("0")
                }
            }
        }
        val df = DecimalFormat(pattern.toString())
        return df.format(bd)
    }

    inner class BigDecimalEvaluator : TypeEvaluator<Any> {
        override fun evaluate(
            fraction: Float,
            startValue: Any,
            endValue: Any
        ): Any {
            val start = startValue as BigDecimal
            val end = endValue as BigDecimal
            val result = end.subtract(start)
            var bigDecimal = BigDecimal(fraction.toString())
            return result.multiply(bigDecimal).add(start)
        }
    }
}