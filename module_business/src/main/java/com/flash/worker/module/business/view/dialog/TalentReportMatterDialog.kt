package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnMatterSelectListener
import kotlinx.android.synthetic.main.dlg_talent_report_matter.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReportMatterDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReportMatterDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "TalentReportMatterDialog"

    companion object {
        val FAKE_INFO = 0
        val NOT_PROVIDE_JOB  = 1
        val OTHER  = 2
    }

    var mOnMatterSelectListener: OnMatterSelectListener? = null

    override fun bindContentView() = R.layout.dlg_talent_report_matter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvFakeInfo.setOnClickListener(this)
        mTvNotProvideJob.setOnClickListener(this)
        mTvOther.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvFakeInfo -> {
                mOnMatterSelectListener?.OnMatterSelect(FAKE_INFO)
            }
            R.id.mTvNotProvideJob -> {
                mOnMatterSelectListener?.OnMatterSelect(NOT_PROVIDE_JOB)
            }
            R.id.mTvOther -> {
                mOnMatterSelectListener?.OnMatterSelect(OTHER)
            }
        }
        dismiss()
    }
}