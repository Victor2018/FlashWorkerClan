package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.coremodel.data.bean.ViolationLabelInfo
import kotlinx.android.synthetic.main.rv_violation_content_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentViolationContentHolder
 * Author: Victor
 * Date: 2021/7/8 10:19
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ViolationLabelInfo?) {
        itemView.mTvTitle.text = data?.name
        itemView.mTvContent.text = data?.value
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition -1, 0)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}