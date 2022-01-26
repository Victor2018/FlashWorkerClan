package com.flash.worker.module.message.view.holder

import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.SystemNoticeInfo
import kotlinx.android.synthetic.main.rv_system_notice_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeContentHolder
 * Author: Victor
 * Date: 2021/5/11 17:28
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: SystemNoticeInfo?) {
        itemView.mTvTitle.text = "【${data?.title}】"
        itemView.mTvContent.text = data?.content
        itemView.mTvCreateTime.text = data?.createTime
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}