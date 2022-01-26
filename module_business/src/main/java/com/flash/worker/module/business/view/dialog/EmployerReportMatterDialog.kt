package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnMatterSelectListener
import kotlinx.android.synthetic.main.dlg_employer_report_matter.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReportMatterDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReportMatterDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "EmployerReportMatterDialog"

    companion object {
        val FAKE_INFO = 0
        val NOT_ARRIVED  = 1
        val OTHER  = 2
    }

    var mOnMatterSelectListener: OnMatterSelectListener? = null

    override fun bindContentView() = R.layout.dlg_employer_report_matter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvFakeInfo.setOnClickListener(this)
        mTvNotArrived.setOnClickListener(this)
        mTvOther.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvFakeInfo -> {
                mOnMatterSelectListener?.OnMatterSelect(FAKE_INFO)
            }
            R.id.mTvNotArrived -> {
                mOnMatterSelectListener?.OnMatterSelect(NOT_ARRIVED)
            }
            R.id.mTvOther -> {
                mOnMatterSelectListener?.OnMatterSelect(OTHER)
            }
        }
        dismiss()
    }
}