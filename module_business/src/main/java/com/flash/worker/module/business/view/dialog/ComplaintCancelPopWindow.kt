package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnComplaintCancelListener
import com.flash.worker.module.business.interfaces.OnReportCancelListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ComplaintCancelPopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class ComplaintCancelPopWindow: AbsPopWindow, View.OnClickListener {

    val TAG = "ComplaintCancelPopWindow"
    var mOnComplaintCancelListener: OnComplaintCancelListener? = null
    var mTaskType: Int = 1//1，工单；2，任务

    constructor(context: Context?,taskType: Int) : super(context) {
        mContext = context
        mTaskType = taskType
        handleWindow()
        initialzie()
    }

    override fun bindContentView() = R.layout.pop_complaint_cancel

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        if (mTaskType == 2) {//任务没有违约解雇
            view?.findViewById<View>(R.id.line_cancel)?.hide()
            view?.findViewById<TextView>(R.id.mTvCancel)?.hide()
        }
        view?.findViewById<TextView>(R.id.mTvComplaint)?.setOnClickListener(this)
        view?.findViewById<TextView>(R.id.mTvCancel)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvComplaint -> {
                mOnComplaintCancelListener?.OnComplaint()
                dismiss()
            }
            R.id.mTvCancel -> {
                mOnComplaintCancelListener?.OnCancel()
                dismiss()
            }
        }
    }


}