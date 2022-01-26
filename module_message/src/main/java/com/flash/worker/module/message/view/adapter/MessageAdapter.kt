package com.flash.worker.module.message.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.im.*
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.MessageType
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.flash.worker.module.message.view.holder.*
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageAdapter
 * Author: Victor
 * Date: 2020/12/30 15:58
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageAdapter (context: Context, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<IMMessage, RecyclerView.ViewHolder>(context, listener)  {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: IMMessage?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            MessageType.TYPE_SEND_TEXT -> {
                return MessageTextSendContentHolder(inflate(R.layout.rv_message_text_send_cell ,parent))
            }
            MessageType.TYPE_RECV_TEXT -> {
                return MessageTextRecvContentHolder(inflate(R.layout.rv_message_text_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_IMAGE -> {
                return MessageImageSendContentHolder(inflate(R.layout.rv_message_image_send_cell ,parent))
            }
            MessageType.TYPE_RECV_IMAGE -> {
                return MessageImageRecvContentHolder(inflate(R.layout.rv_message_image_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_RESUME -> {
                return MessageResumeSendContentHolder(inflate(R.layout.rv_message_resume_send_cell ,parent))
            }
            MessageType.TYPE_RECV_RESUME -> {
                return MessageResumeRecvContentHolder(inflate(R.layout.rv_message_resume_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_JOB -> {
                return MessageJobSendContentHolder(inflate(R.layout.rv_message_job_send_cell ,parent))
            }
            MessageType.TYPE_RECV_JOB-> {
                return MessageJobRecvContentHolder(inflate(R.layout.rv_message_job_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_LOCATION -> {
                return MessageLocationSendContentHolder(inflate(R.layout.rv_message_location_send_cell ,parent))
            }
            MessageType.TYPE_RECV_LOCATION-> {
                return MessageLocationRecvContentHolder(inflate(R.layout.rv_message_location_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_T_RELEASE -> {
                return MessageTalentReleaseSendContentHolder(inflate(R.layout.rv_message_talent_release_send_cell ,parent))
            }
            MessageType.TYPE_RECV_T_RELEASE-> {
                return MessageTalentReleaseRecvContentHolder(inflate(R.layout.rv_message_talent_release_recv_cell ,parent))
            }
            MessageType.TYPE_SEND_TASK -> {
                return MessageTaskSendContentHolder(inflate(R.layout.rv_message_task_send_cell ,parent))
            }
            MessageType.TYPE_RECV_TASK-> {
                return MessageTaskRecvContentHolder(inflate(R.layout.rv_message_task_recv_cell ,parent))
            }
            MessageType.TYPE_NOTIFY -> {
                return MessageNotifyContentHolder(inflate(R.layout.rv_message_notify_cell ,parent))
            }
        }
        return MessageNotifyContentHolder(inflate(R.layout.rv_message_notify_cell ,parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: IMMessage?, position: Int) {
        if (viewHolder is MessageTextSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageTextRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageImageSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageImageRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageResumeSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageResumeRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageJobSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageJobRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageLocationSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageLocationRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageTalentReleaseSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageTalentReleaseRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageTaskSendContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageTaskRecvContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        } else if (viewHolder is MessageNotifyContentHolder) {
            viewHolder.bindData(data)
            viewHolder.mOnItemClickListener = listener
        }

    }

    override fun getItemViewType(position: Int): Int {
        var viewType = super.getItemViewType(position)
        if (viewType == ITEM_TYPE_CONTENT) {
            val message = mDatas?.get(position)

            val isMessageSend: Boolean = NimMessageUtil.isMessageSend(message)

            val msgType = message?.msgType

            Loger.e(TAG,"msgType = $msgType")

            when (msgType) {
                MsgTypeEnum.text -> {
                    viewType = if (isMessageSend) MessageType.TYPE_SEND_TEXT else MessageType.TYPE_RECV_TEXT
                }
                MsgTypeEnum.image,MsgTypeEnum.custom -> {
                    var msgAttachment = message.attachment
                    if (msgAttachment is ImageAttachment) {
                        viewType = if (isMessageSend) MessageType.TYPE_SEND_IMAGE else MessageType.TYPE_RECV_IMAGE
                    }
                    if (msgAttachment is ResumeAttachment) {
                        viewType = if (isMessageSend) MessageType.TYPE_SEND_RESUME else MessageType.TYPE_RECV_RESUME
                    }
                    if (msgAttachment is JobAttachment) {
                        viewType = if (isMessageSend) MessageType.TYPE_SEND_JOB else MessageType.TYPE_RECV_JOB
                    }
                    if (msgAttachment is TalentReleaseAttachment) {
                        viewType = if (isMessageSend) MessageType.TYPE_SEND_T_RELEASE else MessageType.TYPE_RECV_T_RELEASE
                    }
                    if (msgAttachment is TaskAttachment) {
                        viewType = if (isMessageSend) MessageType.TYPE_SEND_TASK else MessageType.TYPE_RECV_TASK
                    }
                }
                MsgTypeEnum.location -> {
                    viewType = if (isMessageSend) MessageType.TYPE_SEND_LOCATION else MessageType.TYPE_RECV_LOCATION
                }
                MsgTypeEnum.tip -> {
                    MessageType.TYPE_NOTIFY
                }
                MsgTypeEnum.notification -> {
                    MessageType.TYPE_NOTIFY
                }
            }

        }
        return viewType
    }
}