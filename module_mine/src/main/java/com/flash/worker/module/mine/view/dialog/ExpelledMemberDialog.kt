package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnMemberExpelledListener
import kotlinx.android.synthetic.main.dlg_expelled_member.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ExpelledMemberDialog
 * Author: Victor
 * Date: 2021/5/10 20:13
 * Description: 
 * -----------------------------------------------------------------
 */
class ExpelledMemberDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var mOnMemberExpelledListener: OnMemberExpelledListener? = null

    override fun bindContentView() = R.layout.dlg_expelled_member

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.8).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        mClExpel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mClExpel -> {
                mOnMemberExpelledListener?.OnMemberExpelled()
                dismiss()
            }
        }
    }
}