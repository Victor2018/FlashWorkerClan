package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitEmployInfo
 * Author: Victor
 * Date: 2021/2/8 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitEmployInfo: Serializable {
    var id:	String? = null//ID
    var employerReleaseId:	String? = null//雇主发布ID
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
    var signupNum: Int = 0//已报名人数
    var releaseSource: Int = 0//发布来源：1-雇用发布；2-直接雇用
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var settlementAmount: Double = 0.0//结算金额
    var price: Double = 0.0// 报酬单价
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var totalAmount: Double = 0.0// 报酬总价
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var isRead: Boolean = false//是否已读
}