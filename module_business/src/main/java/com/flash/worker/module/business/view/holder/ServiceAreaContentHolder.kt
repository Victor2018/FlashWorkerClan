package com.flash.worker.module.business.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import kotlinx.android.synthetic.main.rv_service_area_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ServiceAreaContentHolder
 * Author: Victor
 * Date: 2020/12/22 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class ServiceAreaContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: AreaInfo?) {
        itemView.mTvServiceArea.setOnClickListener(this)
        
        if (TextUtils.isEmpty(data?.name)) {
            itemView.mTvServiceArea.text = "+"
        } else {
            itemView.mTvServiceArea.text = data?.name
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}