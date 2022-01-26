package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsPopWindow
 * Author: Victor
 * Date: 2020/11/27 17:34
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsPopWindow:PopupWindow {
    protected abstract fun bindContentView(): Int
    protected abstract fun getWeightPercentage(): Double
    protected abstract fun getHeightPercentage(): Double
    protected abstract fun initView(view: View?)

    var mContext: Context? = null
    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context?) : super(context) {
        mContext = context
        handleWindow()
        initialzie()
    }

    fun handleWindow() {
        val wm = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        val height = wm.defaultDisplay.height
        mWidth = (width * getWeightPercentage()).toInt()
        mHeight = (height * getHeightPercentage()).toInt()
        //设置popwindow弹出窗体的宽
        setWidth(if (mWidth > 0) mWidth else WindowManager.LayoutParams.WRAP_CONTENT)
        //设置popwindow弹出窗体的高
        setHeight(if (mHeight > 0) mHeight else WindowManager.LayoutParams.WRAP_CONTENT)
        //设置popwindow弹出窗体可点击
        isFocusable = true
        isOutsideTouchable = true
        //刷新状态
        update()
        //实例化一个ColorDrawable颜色为半透明
        val cd = ColorDrawable(0)
        //点击BACK键和其他地方使其消失，设置了这个才能触发OnDismissListener，设置其他控件变化等操作
        setBackgroundDrawable(cd)
        animationStyle = R.style.BasePopWindowStyle
    }

    fun initialzie() {
        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(bindContentView(), null)
        contentView = view
        initView(contentView)
    }


    open fun show(anchor: View) {
        show(anchor, 0, 0)
    }

    open fun show(anchor: View, gravity: LocationGravity?) {
        show(anchor, gravity, 0, 0)
    }

    open fun show(anchor: View, xOff: Int, yOff: Int) {
        show(anchor, LocationGravity.LEFT_BOTTOM, xOff, yOff)
    }

    open fun show(
        anchor: View,
        gravity: LocationGravity?,
        xOff: Int,
        yOff: Int
    ) {
        contentView.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        val width = contentView.measuredWidth
        val height = contentView.measuredHeight
        val locations = IntArray(2)
        anchor.getLocationOnScreen(locations)
        val left = locations[0]
        val top = locations[1]
        when (gravity) {
            LocationGravity.TOP_LEFT -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left - width + xOff,
                top - height + yOff
            )
            LocationGravity.TOP_CENTER -> {
                val offsetX = (anchor.width - width) / 2
                showAtLocation(
                    anchor, Gravity.NO_GRAVITY, left + offsetX + xOff,
                    top - height + yOff
                )
            }
            LocationGravity.TOP_RIGHT -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left + anchor.width + xOff,
                top - height + yOff
            )
            LocationGravity.BOTTOM_LEFT -> showAsDropDown(anchor, -width + xOff, yOff)
            LocationGravity.BOTTOM_CENTER -> {
                val offsetX1 = (anchor.width - height) / 2
                showAsDropDown(anchor, offsetX1 + xOff, yOff)
            }
            LocationGravity.BOTTOM_RIGHT -> showAsDropDown(anchor, anchor.width + xOff, yOff)
            LocationGravity.LEFT_TOP -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left - width + xOff,
                top - height + yOff
            )
            LocationGravity.LEFT_BOTTOM -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left - width + xOff,
                top + anchor.height + yOff
            )
            LocationGravity.LEFT_CENTER -> {
                val offsetY = (anchor.height - height) / 2
                showAtLocation(
                    anchor,
                    Gravity.NO_GRAVITY,
                    left - width + xOff,
                    top + offsetY + yOff
                )
            }
            LocationGravity.RIGHT_TOP -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left + anchor.width + xOff,
                top - height + yOff
            )
            LocationGravity.RIGHT_BOTTOM -> showAtLocation(
                anchor, Gravity.NO_GRAVITY, left + anchor.width + xOff,
                top + anchor.height + yOff
            )
            LocationGravity.RIGHT_CENTER -> {
                val offsetY1 = (anchor.height - height) / 2
                showAtLocation(
                    anchor, Gravity.NO_GRAVITY, left + anchor.width + xOff,
                    top + offsetY1 + yOff
                )
            }
            LocationGravity.FROM_BOTTOM -> showAtLocation(
                anchor,
                Gravity.BOTTOM,
                xOff,
                yOff
            )
        }
    }


    enum class LocationGravity {
        TOP_LEFT, TOP_RIGHT, TOP_CENTER, BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER, RIGHT_TOP, RIGHT_BOTTOM, RIGHT_CENTER, LEFT_TOP, LEFT_BOTTOM, LEFT_CENTER, FROM_BOTTOM
    }
}