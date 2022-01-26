package com.flash.worker.module.message.view.holder

import android.text.TextUtils
import android.view.View
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.JsonUtils
import com.flash.worker.lib.common.util.TimeUtils
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.TaskDetailData
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.TaskAttachment
import com.flash.worker.module.message.R
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.rv_message_task_send_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageTaskSendContentHolder
 * Author: Victor
 * Date: 2020/12/30 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageTaskSendContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData (data: IMMessage?) {
        itemView.mRlTask.setOnClickListener(this)
        itemView.mIvReSend.setOnClickListener(this)
        itemView.mCivAvatar.setOnClickListener(this)

        NimMessageManager.instance.setNimNickNameAvatar(
            itemView.context, data?.fromAccount, null, itemView.mCivAvatar,R.mipmap.ic_avatar)

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

        setMessageStatus(data)
    }

    /**
     * 设置消息发送状态
     */
    private fun setMessageStatus(message: IMMessage?) {
        when (message?.status) {
            MsgStatusEnum.fail -> {
                itemView.mPbSendLoading.visibility = View.GONE
                itemView.mIvReSend.visibility = View.VISIBLE
            }
            MsgStatusEnum.sending -> {
                itemView.mPbSendLoading.visibility = View.VISIBLE
                itemView.mIvReSend.visibility = View.GONE
            }
            else -> {
                itemView.mPbSendLoading.visibility = View.GONE
                itemView.mIvReSend.visibility = View.GONE
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}