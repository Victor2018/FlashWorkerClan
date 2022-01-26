package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.pop_withdraw_notice.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawNoticePopWindow
 * Author: Victor
 * Date: 2021/11/3 14:30
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawNoticePopWindow: AbsPopWindow{

    var message: String? = null

    constructor(context: Context?,msg: String?) : super(context) {
        mContext = context
        message = msg
        handleWindow()
        initialzie()
    }

    override fun bindContentView() = R.layout.pop_withdraw_notice

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.mTvWithdrawNotice?.text = message
    }

}