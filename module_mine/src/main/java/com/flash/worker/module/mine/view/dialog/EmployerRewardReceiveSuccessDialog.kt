 package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.dlg_employer_reward_receive_success.*

 /*
  * -----------------------------------------------------------------
  * Copyright (C) 2020-2080, by Victor, All rights reserved.
  * -----------------------------------------------------------------
  * File: RedEnvelopeReceiveDialog
  * Author: Victor
  * Date: 2021/5/10 20:13
  * Description:
  * -----------------------------------------------------------------
  */
class EmployerRewardReceiveSuccessDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var rewardAmount: Double = 0.0

    override fun bindContentView() = R.layout.dlg_employer_reward_receive_success

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 1.0).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        mIvClose.setOnClickListener(this)

        mTvRewardAmt.text = AmountUtil.addCommaDots(rewardAmount)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
        }
    }
}