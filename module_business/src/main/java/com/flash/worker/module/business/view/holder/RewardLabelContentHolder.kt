package com.flash.worker.module.business.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import kotlinx.android.synthetic.main.rv_reward_label_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardLabelContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardLabelContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: RewardLabelInfo?,isChecked: Boolean) {
        itemView.mChkRewardLabel.setOnClickListener(this)
        
        itemView.mChkRewardLabel.isChecked = isChecked
        itemView.mChkRewardLabel.text = data?.name
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}