package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentRewardInfo
 * Author: Victor
 * Date: 2021/6/1 10:11
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentRewardInfo {
    var employerReleaseId: String? = null//雇主发布ID
    var title: String? = null//发布标题
    var employerName: String? = null//雇主名称
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var jobStartTime: String? = null//工作开始日期
    var jobEndTime: String? = null//工作结束日期
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var totalSettledAmount: Double = 0.0//结算合计
    var totalDays: Int = 0//总天数
    var createTime: String? = null//创建时间
    var expireTime: String? = null//领取过期时间
    var status: Int = 0//业务状态：1-待领取；2-已领取；3-已过期
}