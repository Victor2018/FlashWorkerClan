package com.flash.worker.module.message.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmotionKeyboard
 * Author: Victor
 * Date: 2021/4/27 14:45
 * Description: 
 * -----------------------------------------------------------------
 */
class EmotionKeyboard {
    private val SHARE_PREFERENCE_NAME = "EmotionKeyboard"
    private val SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height"
    var mActivity: Activity? = null
    private var mInputManager //软键盘管理类
            : InputMethodManager? = null
    private var sp: SharedPreferences? = null
    private var mEmotionLayout //表情布局
            : View? = null
    private var mEditText //
            : EditText? = null
    private var mContentView //内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪
            : View? = null

    private constructor() {

    }

    fun with(activity: Activity): EmotionKeyboard {
        var emotionInputDetector = EmotionKeyboard()
        emotionInputDetector.mActivity = activity
        emotionInputDetector.mInputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        emotionInputDetector.sp = activity.getSharedPreferences(
            SHARE_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        return emotionInputDetector
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     *
     * @param contentView
     * @return
     */
    fun bindToContent(contentView: View?): EmotionKeyboard? {
        mContentView = contentView
        return this
    }

    /**
     * 绑定编辑框
     *
     * @param editText
     * @return
     */
    fun bindToEditText(editText: EditText?): EmotionKeyboard? {
        mEditText = editText
        mEditText!!.requestFocus()
        mEditText!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP && mEmotionLayout!!.isShown) {
                lockContentHeight() //显示软件盘时，锁定内容高度，防止跳闪。
                hideEmotionLayout(true) //隐藏表情布局，显示软件盘
                //软件盘显示后，释放内容高度
                mEditText!!.postDelayed({ unlockContentHeightDelayed() }, 200L)
            }
            false
        }
        return this
    }

    /**
     * 绑定表情按钮(可以有多个表情按钮)
     *
     * @param emotionButton
     * @return
     */
    fun bindToEmotionButton(vararg emotionButton: View): EmotionKeyboard? {
        for (view in emotionButton) {
            view.setOnClickListener(getOnEmotionButtonOnClickListener())
        }
        return this
    }

    private fun getOnEmotionButtonOnClickListener(): View.OnClickListener {
        return View.OnClickListener { v ->
            if (mOnEmotionButtonOnClickListener != null) {
                if (mOnEmotionButtonOnClickListener!!.onEmotionButtonOnClickListener(v)) {
                    return@OnClickListener
                }
            }
            if (mEmotionLayout!!.isShown) {
                lockContentHeight() //显示软件盘时，锁定内容高度，防止跳闪。
                hideEmotionLayout(true) //隐藏表情布局，显示软件盘
                unlockContentHeightDelayed() //软件盘显示后，释放内容高度
            } else {
                if (isSoftInputShown()) { //同上
                    lockContentHeight()
                    showEmotionLayout()
                    unlockContentHeightDelayed()
                } else {
                    showEmotionLayout() //两者都没显示，直接显示表情布局
                }
            }
        }
    }

    /*================== 表情按钮点击事件回调 begin ==================*/
    interface OnEmotionButtonOnClickListener {
        /**
         * 主要是为了适用仿微信的情况，微信有一个表情按钮和一个功能按钮，这2个按钮都是按钮了底部区域的显隐
         *
         * @param view
         * @return true：拦截切换输入法，false：让输入法正常切换
         */
        fun onEmotionButtonOnClickListener(view: View?): Boolean
    }

    var mOnEmotionButtonOnClickListener: OnEmotionButtonOnClickListener? = null

    fun setOnEmotionButtonOnClickListener(onEmotionButtonOnClickListener: OnEmotionButtonOnClickListener?) {
        mOnEmotionButtonOnClickListener = onEmotionButtonOnClickListener
    }
    /*================== 表情按钮点击事件回调 end ==================*/

    /*================== 表情按钮点击事件回调 end ==================*/
    /**
     * 设置表情内容布局
     *
     * @param emotionView
     * @return
     */
    fun setEmotionView(emotionView: View?): EmotionKeyboard? {
        mEmotionLayout = emotionView
        return this
    }

    fun build(): EmotionKeyboard? {
//设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity!!.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        //隐藏软件盘
        hideSoftInput()
        return this
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return
     */
    fun interceptBackPress(): Boolean {
        if (mEmotionLayout!!.isShown) {
            hideEmotionLayout(false)
            return true
        }
        return false
    }

    private fun showEmotionLayout() {
        var softInputHeight = getSupportSoftInputHeight()
        if (softInputHeight == 0) {
            softInputHeight = sp!!.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400)
        }
        hideSoftInput()
        mEmotionLayout!!.layoutParams.height = softInputHeight
        mEmotionLayout!!.visibility = View.VISIBLE
    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private fun hideEmotionLayout(showSoftInput: Boolean) {
        if (mEmotionLayout!!.isShown) {
            mEmotionLayout!!.visibility = View.GONE
            if (showSoftInput) {
                showSoftInput()
            }
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private fun lockContentHeight() {
        val params =
            mContentView!!.layoutParams as LinearLayout.LayoutParams
        params.height = mContentView!!.height
        params.weight = 0.0f
    }

    /**
     * 释放被锁定的内容高度
     */
    private fun unlockContentHeightDelayed() {
        mEditText!!.postDelayed({
            (mContentView!!.layoutParams as LinearLayout.LayoutParams).weight =
                1.0f
        }, 200L)
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private fun showSoftInput() {
        mEditText!!.requestFocus()
        mEditText!!.post { mInputManager!!.showSoftInput(mEditText, 0) }
    }

    /**
     * 隐藏软件盘
     */
    private fun hideSoftInput() {
        mInputManager!!.hideSoftInputFromWindow(mEditText!!.windowToken, 0)
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private fun isSoftInputShown(): Boolean {
        return getSupportSoftInputHeight() != 0
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private fun getSupportSoftInputHeight(): Int {
        val r = Rect()
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity!!.window.decorView.getWindowVisibleDisplayFrame(r)
        //获取屏幕的高度
        val screenHeight = mActivity!!.window.decorView.rootView.height
        //计算软件盘的高度
        var softInputHeight = screenHeight - r.bottom
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight()
        }
        if (softInputHeight < 0) {
            Log.w(
                "LQR",
                "EmotionKeyboard--Warning: value of softInputHeight is below zero!"
            )
        }
        //存一份到本地
        if (softInputHeight > 0) {
            sp!!.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply()
        }
        return softInputHeight
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun getSoftButtonsBarHeight(): Int {
        val metrics = DisplayMetrics()
        //这个方法获取可能不是真实屏幕的高度
        mActivity!!.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        //获取当前屏幕的真实高度
        mActivity!!.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) {
            realHeight - usableHeight
        } else {
            0
        }
    }

    /**
     * 获取软键盘高度
     *
     * @return
     */
    fun getKeyBoardHeight(): Int {
        return sp!!.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400)
    }
}