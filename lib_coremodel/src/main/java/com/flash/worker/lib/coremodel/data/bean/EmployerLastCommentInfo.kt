package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerLastCommentInfo
 * Author: Victor
 * Date: 2021/4/13 16:47
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerLastCommentInfo {
    var commentId: String? = null//评价ID
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//用户头像
    var anonymous: Boolean = false//是否匿名
    var commentTime: String? = null//评价时间
    var content: String? = null//评价内容
    var label: Int = 0//评价标签：1-好评；2-中评；3-差评
    var jobOrderId: String? = null//人才工单ID
    var taskType: Int = 0//任务类型：1-工单；2-任务
}