package com.flash.worker.lib.common.view.holder

import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.ResUtils
import kotlinx.android.synthetic.main.rv_complex_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ComplexContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ComplexContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (data: String?, checkPosition: Int) {
        itemView.mClComplexCell.setOnClickListener(this)

        itemView.mTvTitle.text = data

        if (checkPosition == adapterPosition) {
            itemView.mTvTitle.setTextColor(ResUtils.getColorRes(R.color.color_333333))
            itemView.mIvComplexChecked.visibility = View.VISIBLE
        } else {
            itemView.mTvTitle.setTextColor(ResUtils.getColorRes(R.color.color_666666))
            itemView.mIvComplexChecked.visibility = View.GONE
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}