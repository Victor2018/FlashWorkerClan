package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitEmployInfo
 * Author: Victor
 * Date: 2021/2/8 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitEmployInfo: Serializable {
    var id:	String? = null//ID
    var employerUserId:	String? = null//雇主用户ID
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始时间
    var jobEndTime: String? = null//工作结束时间
    var totalDays: Int = 0//总天数
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var settlementAmount: Double = 0.0//结算金额
    var price: Double = 0.0// 报酬单价
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var totalAmount: Double = 0.0// 合计金额
    var dailySalary: Double = 0.0// 日薪
    var workDistrict: String? = null//工作地址(区)
    var paidHour: Double = 0.0//日工作时长
    var shiftType: Int = 0//班次：1-当天班次；2-跨天班次
    var startTime: String? = null//每日开工时间：格式:09:30
    var endTime: String? = null//每日完工时间：格式:18:30
    var signupTime: String? = null//报名时间
    var signupTradeNo: String? = null//报名单号
    var source: Int = 0//报名来源：1-直接报名；2-受邀报名
    var isAtHome: Boolean = false//在家可做

}