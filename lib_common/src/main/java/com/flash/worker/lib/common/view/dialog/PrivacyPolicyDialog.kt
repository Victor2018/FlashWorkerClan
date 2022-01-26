package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.SpannableUtil
import com.flash.worker.lib.common.util.ToastUtils
import kotlinx.android.synthetic.main.dlg_privacy_policy.*
import kotlin.system.exitProcess


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PrivacyPolicyDialog
 * Author: Victor
 * Date: 2020/12/18 12:12
 * Description: 
 * -----------------------------------------------------------------
 */
class PrivacyPolicyDialog(context: Context): AbsDialog(context),View.OnClickListener,
        DialogInterface.OnKeyListener {

    var mOnDialogOkCancelClickListener: OnDialogOkCancelClickListener? = null

    var mExitTime: Long = 0

    override fun bindContentView() = R.layout.dlg_privacy_policy

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
        setCanceledOnTouchOutside(false)
        mTvContent.movementMethod = LinkMovementMethod.getInstance()
        mTvOk.setOnClickListener(this)
        mTvCancel.setOnClickListener(this)
        setOnKeyListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvOk -> {
                mOnDialogOkCancelClickListener?.OnDialogOkClick()
                dismiss()
            }
            mTvCancel -> {
                mOnDialogOkCancelClickListener?.OnDialogCancelClick()
                dismiss()
            }
        }
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - mExitTime < 2000) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(0)
                } else {
                    mExitTime = System.currentTimeMillis()
                    ToastUtils.show("再按一次退出")
                }
                return true
            }
        }

        return false
    }

}