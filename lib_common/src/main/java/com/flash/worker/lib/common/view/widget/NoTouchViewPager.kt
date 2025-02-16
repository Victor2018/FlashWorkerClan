package com.flash.worker.lib.common.view.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NoTouchViewPager.kt
 * Author: Victor
 * Date: 2018/8/30 15:05
 * Description: 不能滑动的ViewPager
 * -----------------------------------------------------------------
 */
class NoTouchViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = false

    override fun onInterceptTouchEvent(event: MotionEvent) = false
}