package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.PhoneUtil
import com.flash.worker.lib.common.util.ToastUtils
import kotlinx.android.synthetic.main.dlg_call.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CallDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 打电话dialog
 * -----------------------------------------------------------------
 */
class CallDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "CallDialog"

    var tel: String? = null

    override fun bindContentView() = R.layout.dlg_call

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
//        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvTel.text = tel

        mTvTel.setOnClickListener(this)
        mTvCancel.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvTel -> {
                PhoneUtil.callPhone(context,tel)
                dismiss()
            }
            R.id.mTvCancel -> {
                dismiss()
            }
        }
    }

}