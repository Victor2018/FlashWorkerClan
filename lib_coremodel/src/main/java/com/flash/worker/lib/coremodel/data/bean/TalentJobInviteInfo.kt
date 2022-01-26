package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobInviteInfo
 * Author: Victor
 * Date: 2021/4/21 9:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobInviteInfo {
    var employerUserId: String? = null//雇主用户ID
    var inviteId: String? = null//邀请ID
    var inviteTime: String? = null//邀请时间
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//人才简历ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var totalDays: Int = 0//总天数
    var paidHour: Double = 0.0//日工作时长
    var workDistrict: String? = null//工作地址(区)
    var isAtHome: Boolean = false//在家可做
    var price: Double = 0.0//报酬单价
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var settlementAmount: Double = 0.0//结算金额
    var totalAmount: Double = 0.0//报酬总价
    var dailySalary: Double = 0.0//日薪
    var taskType: Int = 0//任务类型：1-工单；2-任务
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
}