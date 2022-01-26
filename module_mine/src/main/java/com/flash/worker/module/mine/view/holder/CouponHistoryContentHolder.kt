package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmploymentRewardInfo
import com.flash.worker.lib.coremodel.data.bean.UserCouponInfo
import kotlinx.android.synthetic.main.rv_coupon_history_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CouponHistoryContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class CouponHistoryContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: UserCouponInfo?) {
        itemView.mTvName.text = data?.name
        itemView.mTvAmount.text = data?.amount?.toInt()?.toString()
        itemView.mTvExpireTime.text = "${data?.expireTime}\t\t到期"

        var status = data?.status ?:0
        if (status == 1) {
            itemView.mTvStatus.text = "待使用"
        } else if (status == 2) {
            itemView.mTvStatus.text = "已使用"
        } else if (status == 3) {
            itemView.mTvStatus.text = "已过期"
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}