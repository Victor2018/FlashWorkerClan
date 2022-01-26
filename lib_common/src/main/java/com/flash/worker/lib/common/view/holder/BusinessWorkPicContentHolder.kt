package com.flash.worker.lib.common.view.holder

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import kotlinx.android.synthetic.main.rv_business_work_pic_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BusinessWorkPicContentHolder
 * Author: Victor
 * Date: 2020/12/19 11:38
 * Description: 
 * -----------------------------------------------------------------
 */
class BusinessWorkPicContentHolder(itemView: View): ContentViewHolder(itemView) {

    fun bindData (context: Context, data: WorkPicInfo?, showOnly: Boolean,workPicTitle: String?) {
        if (TextUtils.isEmpty(data?.pic)) {
            itemView.mIvWorkPicBorder.visibility = View.VISIBLE
            itemView.mIvWorkPicAdd.visibility = View.VISIBLE
            itemView.mTvWorkPicTitle.visibility = View.VISIBLE

            itemView.mIvWorkPic.visibility = View.GONE
            itemView.mIvWorkPicDel.visibility = View.GONE
        } else {
            ImageUtils.instance.loadImage(context,itemView.mIvWorkPic,data?.pic)

            itemView.mIvWorkPicBorder.visibility = View.GONE
            itemView.mIvWorkPicAdd.visibility = View.GONE
            itemView.mTvWorkPicTitle.visibility = View.GONE

            itemView.mIvWorkPic.visibility = View.VISIBLE

            if (showOnly) {
                itemView.mIvWorkPicDel.visibility = View.GONE
            } else {
                itemView.mIvWorkPicDel.visibility = View.VISIBLE
            }
        }

        if (!TextUtils.isEmpty(workPicTitle)) {
            itemView.mTvWorkPicTitle.text = workPicTitle
        }
        itemView.mIvWorkPic.setOnClickListener(this)
        itemView.mIvWorkPicBorder.setOnClickListener(this)
        itemView.mIvWorkPicDel.setOnClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}