package com.flash.worker.module.mine.view.holder

import android.view.View
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.GuildApplyRecordInfo
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.rv_guild_apply_record_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildApplyRecordContentHolder
 * Author: Victor
 * Date: 2021/1/28 16:28
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildApplyRecordContentHolder(itemView: View) : ContentViewHolder(itemView){

    fun bindData (data: GuildApplyRecordInfo?) {
        var auditTime = DateUtil.transDate(data?.auditTime,
                "yyyy.MM.dd HH:mm:ss","yyyy.MM.dd HH:mm")
        itemView.mTvAuditTime.text = "通过时间：$auditTime"
        itemView.mTvRejectionReason.text = data?.remark

        var rejectTime = DateUtil.transDate(data?.rejectTime,
                "yyyy.MM.dd HH:mm:ss","yyyy.MM.dd HH:mm")
        itemView.mTvRejectionTime.text = "驳回时间：$rejectTime"

        itemView.mTvApplyCity.text = "申请城市：${data?.guildProvince + data?.guildCity}"
        itemView.mTvGuildName.text = "公会名称：${data?.guildName}"

        var applyTime = DateUtil.transDate(data?.applyTime,
                "yyyy.MM.dd HH:mm:ss","yyyy.MM.dd HH:mm")
        itemView.mTvApplyTime.text = "申请时间：$applyTime"

        if (data?.status == 1) {
            itemView.mTvStatus.text = "审核中"
            itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))
            itemView.mTvAuditTime.visibility = View.GONE
            itemView.mClRejection.visibility = View.GONE
        } else if (data?.status == 2) {
            itemView.mTvStatus.text = "审批未通过"
            itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_E26853))

            itemView.mTvAuditTime.visibility = View.GONE
            itemView.mClRejection.visibility = View.VISIBLE
        } else if (data?.status == 3) {
            itemView.mTvStatus.text = "审批通过"
            itemView.mTvStatus.setTextColor(ResUtils.getColorRes(R.color.color_0CA400))

            itemView.mTvAuditTime.visibility = View.VISIBLE
            itemView.mClRejection.visibility = View.GONE
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}