package com.flash.worker.module.mine.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.ViolationUserInfo
import kotlinx.android.synthetic.main.rv_violation_user_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationUserContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationUserContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: ViolationUserInfo?) {
        itemView.mTvViolationUserId.text = data?.violationUserId
        itemView.mTvActualViolationDesc.text = data?.actualViolationDesc
        itemView.mTvReportResult.text = data?.reportResult
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}