package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettledInfo
 * Author: Victor
 * Date: 2021/12/10 11:42
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettledInfo: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var settlementOrderId: String? = null//结算工单ID
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var prepaidAmount: Double = 0.0//已预付金额
    var settledAmount: Double = 0.0//已结算金额
    var finishDeadline: String? = null//任务完成期限
    var finishTime: String? = null//任务提交时间
    var settlementDeadline: String? = null//任务结算期限
    var settlementTime: String? = null//任务结算时间
    var talentUserId: String? = null//人才用户ID
    var resumeId: String? = null//简历ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
}