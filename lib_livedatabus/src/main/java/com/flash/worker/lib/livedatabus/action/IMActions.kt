package com.flash.worker.lib.livedatabus.action


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IMActions
 * Author: Victor
 * Date: 2020/12/11 17:11
 * Description: 
 * -----------------------------------------------------------------
 */
object IMActions {
    /**
     * 新会话
     */
    const val NEW_RECENT_CONTACT = "NEW_RECENT_CONTACT"

    /**
     * 会话消息变更
     */
    const val MESSAGE_STATUS = "MESSAGE_STATUS"

    /**
     * 新消息
     */
    const val NEW_MESSAGE = "NEW_MESSAGE"

    /**
     * 刷新消息未读数
     */
    const val REFRESH_IM_UNREAD_COUNT = "REFRESH_IM_UNREAD_COUNT"

    /**
     * 刷新tab未读小红点
     */
    const val REFRESH_UNREAD_COUNT = "REFRESH_UNREAD_COUNT"

    /**
     * 刷新消息状态
     */
    const val REFRESH_MESSAGE_STATUS = "REFRESH_MESSAGE_STATUS"


    /**
     * 清空最近聊天会话列表
     */
    const val CLEAR_RECENT_CONTACT = "CLEAR_RECENT_CONTACT"

    /**
     * 发送图片消息
     */
    const val SEND_IMAGE_MESSAGE = "SEND_IMAGE_MESSAGE"

    /**
     * 发送简历消息
     */
    const val SEND_RESUME_MESSAGE = "SEND_RESUME_MESSAGE"

    /**
     * 发送常用语消息
     */
    const val SEND_COMMON_WORDS_MESSAGE = "SEND_COMMON_WORDS_MESSAGE"

    /**
     * 发送岗位邀请消息
     */
    const val SEND_JOB_MESSAGE = "SEND_JOB_MESSAGE"

    /**
     * 发送任务邀请消息
     */
    const val SEND_TASK_MESSAGE = "SEND_TASK_MESSAGE"

    /**
     * 发送位置消息
     */
    const val SEND_LOCATION_MESSAGE = "SEND_LOCATION_MESSAGE"

    /**
     * 系统消息未读数
     */
    const val SYSTEM_NOTICE_UNREAD_COUNT = "SYSTEM_NOTICE_UNREAD_COUNT"

    /**
     * 云信登录成功
     */
    const val NIM_LOGIN_SUCCESS = "NIM_LOGIN_SUCCESS"


}