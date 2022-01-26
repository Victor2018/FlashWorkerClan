package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentOrderInfo
 * Author: Victor
 * Date: 2021/3/4 15:33
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentOrderInfo {
    var jobOrderId:String? = null//人才工单ID
    var signupTradeNo:String? = null//报名单号
    var signupTime:String? = null//报名时间
    var cancelSignupTime:String? = null//取消报名时间
    var employmentTime:String? = null//录取时间
    var submitTime:String? = null//提交时间
    var employmentTradeNo:String? = null//录取单号
    var finishTime:String? = null//完工时间或解约时间
    var finishType: Int = 0//结束类型：1-人才解约；2-雇主解约；3-完工结束
    var cancelSignupType: Int = 0//取消类型：1-人才取消；2-系统取消；3-雇主取消
    var status: Int = 0//业务状态：1-已报名；2-已取消；3-已雇用；4-待评价；5-已结束
}