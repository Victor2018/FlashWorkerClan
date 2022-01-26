package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InviteTalentEmployerReleaseInfo
 * Author: Victor
 * Date: 2021/4/20 17:33
 * Description: 
 * -----------------------------------------------------------------
 */
class InviteTalentEmployerReleaseInfo {
    var releaseId: String? = null//发布ID
    var title: String? = null//发布标题
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var employmentNum: Int = 0//拟雇用人数
    var realEmploymentNum: Int = 0//已雇用人数

    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var releaseTime: String? = null//发布时间
}