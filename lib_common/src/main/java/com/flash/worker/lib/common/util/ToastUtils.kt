package com.flash.worker.lib.common.util

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntegerRes
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.coremodel.util.AppConfig

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ToastUtils.java
 * Author: Victor
 * Date: 2018/9/4 11:34
 * Description: 吐司工具类
 * -----------------------------------------------------------------
 */
object ToastUtils {

    /**
     * 调试模式下可显示
     *
     * @param msg
     */
    fun showDebug(msg: String) {
        if (AppConfig.MODEL_DEBUG) {
            Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 调试模式下可显示
     *
     * @param resId
     */
    fun showDebug(@IntegerRes resId: Int) {
        if (AppConfig.MODEL_DEBUG) {
            val text = ResUtils.getStringRes(resId)
            Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 短暂显示
     *
     * @param msg
     */
    fun showShort(msg: CharSequence) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun showShort(resId: Int) {
        val text = ResUtils.getStringRes(resId)
        Toast.makeText(App.get(), text, Toast.LENGTH_SHORT).show()
    }

    /**
     * 长时间显示
     *
     * @param msg
     */
    fun showLong(msg: CharSequence) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_LONG).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun showLong(resId: Int) {
        val text = ResUtils.getStringRes(resId)
        Toast.makeText(App.get(), text, Toast.LENGTH_LONG).show()
    }

    fun show(msg: CharSequence?) {
        if (msg == null) return
        if (TextUtils.isEmpty(msg.toString())) return

        val inflater = App.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        mTvMessage.text = msg
        val mToast = Toast(App.get())
        mToast.setGravity(Gravity.CENTER, 0, 0)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }

    fun show(resId: Int) {
        val msg = ResUtils.getStringRes(resId)
        if (TextUtils.isEmpty(msg)) return
        val inflater = App.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        mTvMessage.text = msg
        val mToast = Toast(App.get())
        mToast.setGravity(Gravity.CENTER, 0, 0)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }

    fun show(msg: CharSequence?,drawableTopResId: Int) {
        if (msg == null) return
        if (TextUtils.isEmpty(msg.toString())) return

        val inflater = App.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //自定义布局
        val view: View = inflater.inflate(R.layout.toast_img_layout, null)
        val mTvMessage = view.findViewById<View>(R.id.tv_message) as TextView

        TextViewBoundsUtil.setTvDrawableTop(view.context,mTvMessage,drawableTopResId)

        mTvMessage.text = msg
        val mToast = Toast(App.get())
        mToast.setGravity(Gravity.CENTER, 0, 0)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.view = view
        mToast.show()
    }
}