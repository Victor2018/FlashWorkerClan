package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnTalentReleaseListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_talent_release.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 首页人才发布dialog
 * -----------------------------------------------------------------
 */
class TalentReleaseDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "TalentReleaseDialog"

    var mOnTalentReleaseListener: OnTalentReleaseListener? = null

    override fun bindContentView() = R.layout.dlg_talent_release

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.4).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvNewTalentRelease.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvNewTalentRelease -> {
                mOnTalentReleaseListener?.OnTalentRelease()
                dismiss()
            }
        }
    }

}