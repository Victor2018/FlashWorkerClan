package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskDetailData
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TaskAttachment
import com.flash.worker.lib.im.TeamNotificationUtil
import kotlinx.android.synthetic.main.rv_message_task_recv_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageTaskRecvContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageTaskRecvContentHolder(itemView: View) : ContentViewHolder(itemView){

    fun bindData (data: IMMessage?) {
        itemView.mRlTask.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        var team = TeamNotificationUtil.getTeam(data?.sessionId)
        if (team != null) {
            itemView.mTvNickName.visibility = View.VISIBLE
        } else {
            itemView.mTvNickName.visibility = View.GONE
        }

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, itemView.mTvNickName, itemView.mCivAvatar,R.mipmap.ic_avatar)

        TimeUtils.bindShowTime(itemView.mTvTime, data?.time)

        val msgAttachment = data?.attachment
        if (msgAttachment is TaskAttachment) {
            var taskJson = msgAttachment.taskData ?: ""
            if (!TextUtils.isEmpty(taskJson)) {
                val taskDetailData =
                    JsonUtils.parseObject(taskJson, TaskDetailData::class.java)

                itemView.mTvTitle.text = taskDetailData?.title
                var finishTimeLimitUnit = taskDetailData?.finishTimeLimitUnit ?: 0
                if (finishTimeLimitUnit == 1) {
                    itemView.mTvFinishTimeLimit.text = "任务时限:限${taskDetailData?.finishTimeLimit}小时完成"
                } else if (finishTimeLimitUnit == 2) {
                    itemView.mTvFinishTimeLimit.text = "任务时限:限${taskDetailData?.finishTimeLimit}天完成"
                }
                itemView.mTvPrice.text = "${AmountUtil.addCommaDots(taskDetailData?.price)}元"
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}