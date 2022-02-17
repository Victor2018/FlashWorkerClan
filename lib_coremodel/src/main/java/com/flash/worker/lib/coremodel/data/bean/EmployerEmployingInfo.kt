package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEmployingInfo
 * Author: Victor
 * Date: 2021/2/8 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEmployingInfo: Serializable {
    var id:	String? = null//ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始时间
    var jobEndTime: String? = null//工作结束时间
    var deadline: String? = null//录取截止时间
    var totalDays: Int = 0//总天数
    var paidHour: Double = 0.0//日工作时长
    var employmentNum: Int = 0//拟雇用人数
    var realEmploymentNum: Int = 0//已雇用人数
    var releaseSource: Int = 0//发布来源：1-直接发布；2-邀请发布
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-整单结）
    var settlementAmount: Double = 0.0//结算金额
    var price: Double = 0.0// 报酬单价
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-整单结
    var totalAmount: Double = 0.0// 报酬总价
    var signupFrozenAmount: Double = 0.0// 人才报名冻结金额
    var totalPrepaidAmount: Double = 0.0// 预付合计
    var totalSettledAmount: Double = 0.0// totalSettledAmount
    var shiftType: Int = 0//班次：1-当天班次；2-跨天班次
    var startTime: String? = null//每日开工时间：格式:09:30
    var endTime: String? = null//每日完工时间：格式:18:30
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)

    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var releaseTime: String? = null//发布时间
    var isRead: Boolean = false//是否已读
    var isAutoPrepaid: Boolean = false//自动预付
}