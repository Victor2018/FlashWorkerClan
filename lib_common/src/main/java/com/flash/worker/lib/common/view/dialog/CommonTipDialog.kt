package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_common_tip.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommonTipDialog
 * Author: Victor
 * Date: 2020/12/18 12:12
 * Description: 
 * -----------------------------------------------------------------
 */
class CommonTipDialog(context: Context): AbsDialog(context),View.OnClickListener {

    var mTitle: String? = null
    var mContent: String? = null
    var mCancelText: String? = null
    var mOkText: String? = null

    var okBtnVisible: Int = View.VISIBLE
    var cancelBtnVisible: Int = View.VISIBLE

    var contentGravity = Gravity.CENTER

    var mOnDialogOkCancelClickListener: OnDialogOkCancelClickListener? = null

    override fun bindContentView() = R.layout.dlg_common_tip

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
        mTvTitle.text = mTitle
        mTvContent.text = mContent
        mTvCancel.text = mCancelText
        mTvOk.text = mOkText

        mTvOk.visibility = okBtnVisible
        mTvCancel.visibility = cancelBtnVisible
        line_cancel.visibility = cancelBtnVisible

        mTvContent.gravity = contentGravity

        mTvOk.setOnClickListener(this)
        mTvCancel.setOnClickListener(this)
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

}