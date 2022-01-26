package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnRedemptionCodeListener
import kotlinx.android.synthetic.main.dlg_redemption_code.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RedemptionCodeDialog
 * Author: Victor
 * Date: 2021/5/10 20:13
 * Description: 
 * -----------------------------------------------------------------
 */
class RedemptionCodeDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var mOnRedemptionCodeListener: OnRedemptionCodeListener? = null

    override fun bindContentView() = R.layout.dlg_redemption_code

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.8).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                dismiss()
            }
            R.id.mTvConfirm -> {
                val code = mEtCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.show("请输入兑换码")
                    return
                }
                mOnRedemptionCodeListener?.OnRedemptionCode(code)
                dismiss()
            }
        }
    }
}