package com.flash.worker.lib.common.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PullBackLayout
 * Author: Victor
 * Date: 2021/1/22 12:01
 * Description: 
 * -----------------------------------------------------------------
 */
class PullBackLayout: FrameLayout {
    private var dragger: ViewDragHelper? = null

    private var minimumFlingVelocity = 0

    var callback: Callback? = null

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs,defStyle) {
        dragger = ViewDragHelper.create(this, 1f / 8f, ViewDragCallback())
        minimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragger?.shouldInterceptTouchEvent(ev)!!
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        dragger?.processTouchEvent(event!!)
        return true
    }

    override fun computeScroll() {
        var continueSettling = dragger?.continueSettling(true) ?: false
        if (continueSettling) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    interface Callback {
        fun onPullStart()
        fun onPull(progress: Float)
        fun onPullCancel()
        fun onPullComplete()
    }

    inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return 0
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return Math.max(0, top)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return height
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            callback?.onPullStart()
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            callback?.onPull(top.toFloat() / height.toFloat())
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val slop: Int = if (yvel > minimumFlingVelocity) height / 6 else height / 3
            if (releasedChild.top > slop) {
                callback?.onPullComplete()
            } else {
                callback?.onPullCancel()
                dragger?.settleCapturedViewAt(0, 0)
                invalidate()
            }
        }
    }
}