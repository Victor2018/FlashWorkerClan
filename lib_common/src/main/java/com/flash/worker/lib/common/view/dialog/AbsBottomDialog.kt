package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.DensityUtil


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AbsBottomDialog
 * Author: Victor
 * Date: 2020/11/27 17:26
 * Description: 
 * -----------------------------------------------------------------
 */
abstract class AbsBottomDialog(context: Context):AbsDialog(context) {
    override fun handleWindow(window: Window) {
        //底部弹出
        window.setGravity(Gravity.BOTTOM)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp!!.width = DensityUtil.getDisplayWidth()
        wlp.windowAnimations = R.style.BottomDialogAnimShow
    }
}