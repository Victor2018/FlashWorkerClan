package com.flash.worker.lib.common.event


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageEvent
 * Author: Victor
 * Date: 2021/7/14 17:39
 * Description: 
 * -----------------------------------------------------------------
 */
object MessageEvent {
    /**
     * 全部标记已读
     */
    const val mark_all_read = "mark_all_read"

    /**
     * 查看系统通知
     */
    const val view_system_notice = "view_system_notice"

    /**
     * 删除聊天记录
     */
    const val delete_chat_history = "delete_chat_history"

    /**
     * 发送简历消息
     */
    const val send_resume_message = "send_resume_message"

    /**
     * 发送岗位消息
     */
    const val send_job_message = "send_job_message"

    /**
     * 发送图片消息
     */
    const val send_image_message = "send_image_message"
}