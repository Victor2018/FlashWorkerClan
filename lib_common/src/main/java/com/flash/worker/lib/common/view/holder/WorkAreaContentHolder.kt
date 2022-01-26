package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import kotlinx.android.synthetic.main.rv_work_area_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkAreaContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkAreaContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: AreaInfo?, checkPosition: Int) {
        if (checkPosition == adapterPosition) {
            itemView.mTvWorkArea.setBackgroundResource(R.drawable.shape_ffd424_radius_3)
            itemView.mTvWorkArea.setTextColor(ResUtils.getColorRes(R.color.color_333333))
        } else {
            itemView.mTvWorkArea.setBackgroundResource(R.drawable.border_cccccc_radius_3)
            itemView.mTvWorkArea.setTextColor(ResUtils.getColorRes(R.color.color_999999))
        }
        itemView.mTvWorkArea.text = data?.name
        itemView.mTvWorkArea.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}