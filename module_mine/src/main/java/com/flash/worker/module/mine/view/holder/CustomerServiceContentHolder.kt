package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.ImageUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.CustomerServiceInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_customer_service_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CustomerServiceContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class CustomerServiceContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: CustomerServiceInfo?,count: Int) {
        if (adapterPosition == count - 1) {
            itemView.line_space.visibility = View.INVISIBLE
        } else {
            itemView.line_space.visibility = View.VISIBLE
        }

        ImageUtils.instance.loadImage(itemView.context,itemView.mCivAvatar,data?.headpic,R.mipmap.ic_avatar)
        itemView.mTvUserName.text = data?.username
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}