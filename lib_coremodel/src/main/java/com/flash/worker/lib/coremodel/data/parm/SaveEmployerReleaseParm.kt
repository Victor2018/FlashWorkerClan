package com.flash.worker.lib.coremodel.data.parm

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SaveEmployerReleaseParm
 * Author: Victor
 * Date: 2021/1/18 20:17
 * Description: 
 * -----------------------------------------------------------------
 */
class SaveEmployerReleaseParm: BaseParm() {
    var id: String? = null//雇主ID
    var employerName: String? = null//雇主名称
    var jobCategoryName: String? = null//人才类型
    var employerId: String? = null//雇主ID
    var jobCategoryId: String? = null//人才类型ID
    var title: String? = null//发布标题
    var peopleCount: Int? = null//雇佣人数
    var settlementMethod: Int? = null//结算方式（1-日结；2-周结；3-件结）
    var payrollMethod: Int? = null//记薪方式：1-时薪；2-件薪
    var paidHour: Double? = null//日记薪小时
    var price: Double? = null//报酬单价
    var dailySalary: Double? = null//日薪
    var jobStartTime: String? = null//工作开始时间 示例格式:2021-01-18 00:00:00
    var jobEndTime: String? = null//工作结束时间 示例格式:2021-01-18 00:00:00
    var workDescription: String? = null//工作描述
    var workProvince: String? = null//工作地址(省)
    var workCity: String? = null//工作地址(市)
    var workDistrict: String? = null//工作地址(区)
    var address: String? = null//详细地址
    var startTime: String? = null//每日开工时间
    var shiftType: Int? = null//班次：1-当天班次；2-跨天班次
    var endTime: String? = null//每日完工时间
    var sexRequirement: Int? = null//性别要求：0-女；1-男；2-男女不限
    var type: Int = 0//发布类型：1-普通发布；2-紧急发布
    var totalDays: Int? = null//总天数
    var ageRequirement: String? = null//年龄要求：格式18-35;不限传空
    var eduRequirement: String? = null//学历要求：不限传空
    var isAtHome: Boolean = false//在家可做
    var identityRequirement: Int? = null//身份要求：1-职场人士；2-学生；3-不限
    var totalAmount: Double? = null//总金额
    var settlementAmount: Double? = null//当期结算金额
    var pics: List<String>? = null//工作环境图片

    var taskType: Int = 0//任务类型：1-工单；2-任务
    var taskQty: Int = 0//任务数量
    var timesLimit: Int = 0//可接件数：1-(一人一件)；2-(一人多件)
    var finishTimeLimitUnit: Int = 0//完成时限单位：1-小时；2-天
    var finishTimeLimit: Int = 0//完成时限
    var settlementTimeLimit: Int = 0//结算时限
    var submitLabel: String? = null//任务提交：多个使用逗号分隔
}