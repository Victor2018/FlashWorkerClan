package com.flash.worker.lib.common.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.R


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsDialog
 * Author: Victor
 * Date: 2020/11/27 17:14
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsDialog(context: Context): Dialog(context, R.style.BaseNoTitleDialog), IDialogRecycle {

    protected abstract fun bindContentView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindContentView())

        //设置属性信息宽高或者动画
        val window = window
        handleWindow(window!!)
        val wlp = window!!.attributes
        handleLayoutParams(wlp)
        window!!.attributes = wlp

    }

    /**
     * 用于处理窗口的属性
     *
     * @param window
     */
    abstract fun handleWindow(window: Window)

    abstract fun handleLayoutParams(wlp: WindowManager.LayoutParams?)

    override fun onDestroy() {
    }

}