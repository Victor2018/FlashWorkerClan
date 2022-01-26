package com.flash.worker.lib.common.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.R
import com.flash.worker.lib.coremodel.data.bean.TalentCellInfo
import kotlinx.android.synthetic.main.rv_talent_item_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCellContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentCellContentHolder(itemView: View, var parentPosition: Int) : ContentViewHolder(itemView) {

    var mTalentCellInfo: TalentCellInfo? = null

    fun bindData (data: TalentCellInfo?, checkCellId: String?) {
        mTalentCellInfo = data

        if (TextUtils.equals(checkCellId,data?.id)) {
            itemView.mTvTalentCell.setBackgroundResource(R.drawable.bg_ffd424_shape_radius_7)
        } else {
            itemView.mTvTalentCell.setBackgroundResource(R.drawable.bg_f5f5f5_shape_radius_7)
        }

        itemView.mTvTalentCell.text = data?.name
        itemView.mTvTalentCell.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        mOnItemClickListener!!.onItemClick(null, view, adapterPosition, parentPosition.toLong())
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}