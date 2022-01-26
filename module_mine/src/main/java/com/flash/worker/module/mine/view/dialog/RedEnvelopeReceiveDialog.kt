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
import com.flash.worker.lib.coremodel.data.bean.AcivityInfo
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnRedEnvelopeReceiveListener
import com.flash.worker.module.mine.view.interfaces.OnRewardReceiveListener
import kotlinx.android.synthetic.main.dlg_red_envelope_receive.*
import kotlinx.android.synthetic.main.rv_red_envelope_cell.view.*

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
class RedEnvelopeReceiveDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var mOnRedEnvelopeReceiveListener: OnRedEnvelopeReceiveListener? = null

    var mAcivityInfo: AcivityInfo? = null

    override fun bindContentView() = R.layout.dlg_red_envelope_receive

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.9).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        mIvClose.setOnClickListener(this)
        mIvReceive.setOnClickListener(this)

        mTvRedEnvelopeAmt.text = AmountUtil.addCommaDots(mAcivityInfo?.redPacketAmount)
        mTvRedEnvelopeRange.text = mAcivityInfo?.redPacketDesc
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
            R.id.mIvReceive -> {
                mOnRedEnvelopeReceiveListener?.OnRedEnvelopeReceive(mAcivityInfo)
                dismiss()
            }
        }
    }
}