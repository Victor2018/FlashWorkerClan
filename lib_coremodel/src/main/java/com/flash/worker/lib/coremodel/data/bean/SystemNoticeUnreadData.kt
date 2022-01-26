package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeData
 * Author: Victor
 * Date: 2021/5/11 18:17
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeUnreadData {
    var unreadNum: Int = 0//未读数量
    var latestSummary: String? = null//最新的消息摘要
    var latestTime: String? = null//最新的消息时间
}