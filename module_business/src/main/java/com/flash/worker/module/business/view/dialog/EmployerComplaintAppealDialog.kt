package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnAppealSelectListener
import kotlinx.android.synthetic.main.dlg_employer_complaint_appeal.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerComplaintAppealDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerComplaintAppealDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "EmployerComplaintAppealDialog"

    companion object {
        val REQUIRE_MARGIN_PAYMENT = 0
        val REQUIRE_REFUND_PREPAID_SALARY  = 1
    }

    var mOnAppealSelectListener: OnAppealSelectListener? = null

    var requireSettlement: Boolean = false

    override fun bindContentView() = R.layout.dlg_employer_complaint_appeal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvRequireMarginPayment.setOnClickListener(this)
        mTvRefundPrepaidSalary.setOnClickListener(this)

        if (requireSettlement) {
            mTvRefundPrepaidSalary.visibility = View.VISIBLE
        } else {
            mTvRefundPrepaidSalary.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvRequireMarginPayment -> {
                mOnAppealSelectListener?.OnAppealSelect(REQUIRE_MARGIN_PAYMENT)
            }
            R.id.mTvRefundPrepaidSalary -> {
                mOnAppealSelectListener?.OnAppealSelect(REQUIRE_REFUND_PREPAID_SALARY)
            }
        }
        dismiss()
    }
}