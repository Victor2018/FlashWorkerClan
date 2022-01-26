package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnEmployerReleaseListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_employer_release.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HomeNewReleaseDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 首页雇主发布dialog
 * -----------------------------------------------------------------
 */
class EmployerReleaseDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "EmployerReleaseDialog"

    var mOnEmployerReleaseListener: OnEmployerReleaseListener? = null

    override fun bindContentView() = R.layout.dlg_employer_release

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.4).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvNewJobRelease.setOnClickListener(this)
        mTvNewTaskRelease.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvNewJobRelease -> {
                mOnEmployerReleaseListener?.OnNewJobRelease()
                dismiss()
            }
            mTvNewTaskRelease -> {
                mOnEmployerReleaseListener?.OnNewTaskRelease()
                dismiss()
            }
        }
    }

}