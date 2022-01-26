package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.coremodel.data.bean.TalentTypeInfo
import kotlinx.android.synthetic.main.rv_talent_type_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTypeContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTypeContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: TalentTypeInfo?, position: Int) {
        itemView.mIvTalentType.setOnClickListener(this)

        itemView.mIvTalentType.text = data?.name
        if (position == adapterPosition) {
            itemView.mViewChecked.visibility = View.VISIBLE
            itemView.mIvTalentType.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        } else {
            itemView.mViewChecked.visibility = View.GONE
            itemView.mIvTalentType.setTextColor(ResUtils.getColorRes(R.color.color_666666))
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}