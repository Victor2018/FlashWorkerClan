package com.flash.worker.module.mine.view.holder

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.data.PayWayInfo
import kotlinx.android.synthetic.main.rv_pay_way_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayWayContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class PayWayContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: PayWayInfo?, checkPosition: Int) {
        itemView.mIvPayWay.setImageResource(data?.payWayIconResId!!)
        itemView.mTvPayWay.text = data?.payWayName

        if (checkPosition == adapterPosition) {
            itemView.mIvPayWayChecked.visibility = View.VISIBLE
            itemView.setBackgroundResource(R.drawable.border_ffd424_radius_7)
        } else {
            itemView.mIvPayWayChecked.visibility = View.GONE
            itemView.setBackgroundResource(R.drawable.border_cccccc_radius_7)
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}