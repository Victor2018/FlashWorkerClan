package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseInfo
 * Author: Victor
 * Date: 2021/1/20 18:12
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleaseInfo: Serializable {
    var id: String? = null//发布ID
    var remark: String? = null//驳回原因
    var employerName: String? = null//雇主名称
    var title: String? = null//发布标题
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var peopleCount: Int = 0//雇佣人数
    var realPeopleCount: Int = 0//已雇佣人数
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var price: Double = 0.0//报酬单价
    var jobStartTime: String? = null//工作开始时间
    var jobEndTime: String? = null//工作结束时间
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var countDownTime:Long = 0//剩余截止时间,单位：秒
    var isAtHome: Boolean = false//在家可做
    var identityRequirement: Int = 0//身份要求：1-职场人士；2-学生；3-不限
    var releaseTime: String? = null//发布时间
    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
}