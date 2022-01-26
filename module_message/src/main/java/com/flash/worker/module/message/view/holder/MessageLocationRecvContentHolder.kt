package com.flash.worker.module.message.view.holder

import android.view.View
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TeamNotificationUtil
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment
import kotlinx.android.synthetic.main.rv_message_location_recv_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageAddressRecvContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageLocationRecvContentHolder(itemView: View) : ContentViewHolder(itemView){

    fun bindData (data: IMMessage?) {
        itemView.mCvLocation.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        var team = TeamNotificationUtil.getTeam(data?.sessionId)
        if (team != null) {
            itemView.mTvNickName.visibility = View.VISIBLE
        } else {
            itemView.mTvNickName.visibility = View.GONE
        }

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, itemView.mTvNickName, itemView.mCivAvatar,R.mipmap.ic_avatar)

        val msgAttachment = data?.attachment
        if (msgAttachment is LocationAttachment) {
            itemView.mTvLocation.text = msgAttachment.address
        }

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}