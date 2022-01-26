package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementOrderData
 * Author: Victor
 * Date: 2021/3/22 11:01
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentSettlementOrderData: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var settlementOrderId:String? = null//结算工单ID
    var employerUserId:String? = null//雇主用户ID
    var employerReleaseId:String? = null//雇主发布ID
    var title:String? = null//发布标题
    var employerName:String? = null//雇主名称
    var employerUsername:String? = null//雇主闪工名
    var workDistrict:String? = null//工作地址(区)
    var identity: Int = 0//雇主类型：1-企业；2-商户；3-个人
    var totalDays: Int = 0//总天数
    var payrollMethod: Int = 0//记薪方式：1-时薪；2-件薪
    var settlementMethod: Int = 0//结算方式（1-日结；2-周结；3-件结）
    var shiftType: Int = 0//班次：1-当天班次；2-跨天班次
    var status: Int = 0//业务状态：1-待到岗；2-已到岗(待预付)；3-已预付(工作中)；4-已完工(待结算)；5-已结算
    var licenceAuth: Boolean = false//是否通过营业执照认证
    var activeArrivePost: Boolean = false//到岗按钮：true-激活；false-禁用
    var activeFinished: Boolean = false//完工按钮：true-激活；false-禁用
    var jobStartTime: String? = null//工作开始时间 示例格式:2021.01.18
    var jobEndTime: String? = null//工作结束时间 示例格式:2021.01.18
    var startTime: String? = null//每日开工时间：格式:09:30
    var endTime: String? = null//每日完工时间：格式:18:30
    var settlementStartTime: String? = null//结算开始时间 示例格式:2021.01.18
    var settlementEndTime: String? = null//结算结束时间 示例格式:2021.01.18
    var settlementAmount: Double = 0.0//结算金额
    var price: Double = 0.0//报酬单价
    var dailySalary: Double = 0.0//日薪
    var totalAmount: Double = 0.0//报酬总价
    var paidHour: Double = 0.0//日工作时长
    var rewardAmount: Double = 0.0//奖励金额
    var prepaidAmount: Double = 0.0//已预付金额
    var settledAmount: Double = 0.0//已结算金额
    var totalSettledAmount: Double = 0.0//结算合计
    var isAtHome: Boolean = false//在家可做

    var taskType: Int = 0//任务类型：1-工单；2-任务

}