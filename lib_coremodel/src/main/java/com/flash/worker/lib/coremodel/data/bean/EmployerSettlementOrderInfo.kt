package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettlementOrderInfo
 * Author: Victor
 * Date: 2021/3/22 18:09
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettlementOrderInfo: Serializable {
    var jobOrderId: String? = null//人才工单ID
    var settlementOrderId: String? = null//结算工单ID
    var talentUserId: String? = null//人才用户ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//简历ID
    var headpic: String? = null//头像
    var username: String? = null//用户名
    var settlementStartTime: String? = null//结算开始时间 示例格式:2021.01.18
    var settlementEndTime: String? = null//结算结束时间 示例格式:2021.01.18
    var sex: Int = 0//性别（0-女；1-男；2-其它）
    var age: Int = 0//年龄
    var height: Int = 0//身高
    var weight: Int = 0//体重
    var status: Int = 0//业务状态：1-待到岗；2-已到岗(待预付)；3-已预付(工作中)；4-已完工(待结算)；5-已结算
    var userIdentity: Int = 0//用户身份：1-职场人士；2-学生
    var prepaidAmount: Double = 0.0//已预付金额
    var settledAmount: Double = 0.0//已结算金额
    var totalSettledAmount: Double = 0.0//结算合计
    var rewardAmount: Double = 0.0//奖励金额
    var activePrepaid: Boolean = false//预付按钮：true-激活；false-禁用
}