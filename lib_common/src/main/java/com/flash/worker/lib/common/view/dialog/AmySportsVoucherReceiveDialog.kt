package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnAmySportsVoucherReceiveListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_amy_sports_voucher_receive.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeTipDialog
 * Author: Victor
 * Date: 2020/12/18 12:12
 * Description: 艾米体育代金券领取弹窗
 * -----------------------------------------------------------------
 */
class AmySportsVoucherReceiveDialog(context: Context): AbsDialog(context),View.OnClickListener {

    var mOnAmySportsVoucherReceiveListener: OnAmySportsVoucherReceiveListener? = null

    override fun bindContentView() = R.layout.dlg_amy_sports_voucher_receive

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.9).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        setCanceledOnTouchOutside(false)
        mIvClose.setOnClickListener(this)
        mIvReceive.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvClose -> {
                dismiss()
            }
            mIvReceive -> {
                mOnAmySportsVoucherReceiveListener?.OnAmySportsVoucherReceive()
                dismiss()
            }
        }
    }
}