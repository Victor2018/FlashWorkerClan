package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnCreditFreezeListener
import kotlinx.android.synthetic.main.pop_credit_freeze.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditFreezePopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class CreditFreezePopWindow: AbsPopWindow, View.OnClickListener {

    var mOnCreditFreezeListener: OnCreditFreezeListener? = null
    var frozenAmt: Double = 0.0

    constructor(context: Context?,frozenAmount: Double) : super(context) {
        mContext = context
        frozenAmt = frozenAmount
        handleWindow()
        initialzie()
    }

    override fun bindContentView() = R.layout.pop_credit_freeze

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.mTvCreditFreeze?.setOnClickListener(this)

        view?.mTvCreditFreeze?.text = "信用冻结:${AmountUtil.addCommaDots(frozenAmt)}元"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCreditFreeze -> {
                mOnCreditFreezeListener?.OnCreditFreeze()
                dismiss()
            }
        }
    }


}