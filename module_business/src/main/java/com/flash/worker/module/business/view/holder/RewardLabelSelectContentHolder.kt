package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.rv_reward_label_select_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardLabelSelectContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: RewardLabelInfo?) {
        itemView.mClMatterRoot.setOnClickListener(this)
        itemView.mIvRemoveReward.setOnClickListener(this)

        itemView.mTvReward.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}