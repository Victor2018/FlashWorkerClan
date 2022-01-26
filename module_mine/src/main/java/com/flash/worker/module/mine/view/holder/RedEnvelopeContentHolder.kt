package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.AcivityInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_red_envelope_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RedEnvelopeContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class RedEnvelopeContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: AcivityInfo?) {
        itemView.mTvRemainCount.text = "当前可领取${data?.remainCount}个"
        itemView.mTvReceiveCount.text = "已领取${data?.receiveCount}个(共${data?.redPacketQty}个)"
        itemView.mTvRedEnvelopeAmt.text = data?.redPacketAmount.toString()
        itemView.mTvRedEnvelopeRange.text = data?.redPacketDesc

        if (data?.receiveCount == data?.redPacketQty) {
            itemView.mIvRedEnvelopeBg.setImageResource(R.mipmap.ic_red_envelope_normal)
            itemView.mIvReceive.setImageResource(R.mipmap.ic_receive_red_envelope_normal)
            itemView.mTvRedEnvelopeAmtPrefix.setTextColor(ResUtils.getColorRes(R.color.color_8B8B8B))
            itemView.mTvRedEnvelopeAmt.setTextColor(ResUtils.getColorRes(R.color.color_8B8B8B))
            itemView.mTvRedEnvelopeRange.setTextColor(ResUtils.getColorRes(R.color.color_8B8B8B))
        } else {
            itemView.mIvRedEnvelopeBg.setImageResource(R.mipmap.ic_red_envelope_focus)
            itemView.mIvReceive.setImageResource(R.mipmap.ic_receive_red_envelope_focus)
            itemView.mTvRedEnvelopeAmtPrefix.setTextColor(ResUtils.getColorRes(R.color.color_DC2B2D))
            itemView.mTvRedEnvelopeAmt.setTextColor(ResUtils.getColorRes(R.color.color_DC2B2D))
            itemView.mTvRedEnvelopeRange.setTextColor(ResUtils.getColorRes(R.color.color_DC2B2D))
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}