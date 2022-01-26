package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentOrderReleaseInfo
 * Author: Victor
 * Date: 2021/3/4 15:32
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentOrderReleaseInfo: Serializable {
    var id: String? = null//发布ID
    var headpic: String? = null//头像
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var title: String? = null//发布标题
    var employerId: String? = null//雇主ID
    var employerName: String? = null//雇主名称
    var jobStartTime: String? = null//工作开始时间 示例格式:2021.01.18
    var jobEndTime: String? = null//工作结束时间 示例格式:2021.01.18
    var startTime: String? = null//
    var endTime: String? = null//
    var workDescription: String? = null//工作描述
    var workProvince: String? = null//工作地址(省)
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var address: String? = null//详细地址
    var ageRequirement: String? = null//年龄要求：格式18-35;不限传空
    var eduRequirement: String? = null//学历要求：不限传空
    var deadline: String? = null//录取截止时间：示例格式:2021.01.18 09:00
    var employerCredit: Int = 0//信用分
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var peopleCount: Int = 0//雇用人数
    var realPeopleCount: Int = 0//已雇人数
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var shiftType: Int = 0//班次：1-当天班次；2-跨天班次
    var sexRequirement: Int = 0//性别要求：0-女；1-男；2-男女不限
    var identityRequirement: Int = 0//身份要求：1-职场人士；2-学生；3-不限
    var type: Int = 0//发布类型：1-普通发布；2-紧急发布
    var totalDays: Int = 0//总天数
    var settlementPieceCount: Int = 0//结算件数
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var price: Double = 0.0//报酬单价
    var paidHour: Double = 0.0//日工作时长
    var dailySalary: Double = 0.0//日薪
    var totalAmount: Double = 0.0//报酬总价
    var settlementAmount: Double = 0.0//结算金额
    var employerCreditScore: Int = 0//雇主信用分
    var isAtHome: Boolean = false//在家可做
    var pics: List<String>? = null

    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var taskReceiveQty: Int = 0//任务已领取数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var submitLabel: String? = null//任务提交：多个使用逗号分隔
}