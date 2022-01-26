package com.flash.worker.module.business.view.dialog

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
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnResumeCopyListener
import kotlinx.android.synthetic.main.dlg_copy_resume.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CopyResumeDialog
 * Author: Victor
 * Date: 2021/10/20 10:35
 * Description: 
 * -----------------------------------------------------------------
 */
class CopyResumeDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var mOnResumeCopyListener: OnResumeCopyListener? = null

    override fun bindContentView() = R.layout.dlg_copy_resume

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
                val resumeName = mEtResumeName.text.toString()
                if (TextUtils.isEmpty(resumeName)) {
                    ToastUtils.show("请输入简历名称")
                    return
                }
                mOnResumeCopyListener?.OnResumeCopy(resumeName)
                dismiss()
            }
        }
    }
}