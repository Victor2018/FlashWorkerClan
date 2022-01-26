package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.EmploymentRewardInfo
import com.flash.worker.lib.coremodel.data.bean.UserCouponInfo
import kotlinx.android.synthetic.main.rv_coupon_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CouponContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class CouponContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: UserCouponInfo?) {

        itemView.mTvName.text = data?.name
        itemView.mTvAmount.text = data?.amount?.toInt()?.toString()
        itemView.mTvExpireTime.text = "${data?.expireTime}\t\t到期"

        itemView.mTvUse.setOnClickListener(this)

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}