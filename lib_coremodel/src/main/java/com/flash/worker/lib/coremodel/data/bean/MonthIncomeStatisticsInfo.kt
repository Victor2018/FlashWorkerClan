package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MonthIncomeStatisticsInfo
 * Author: Victor
 * Date: 2021/5/24 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
class MonthIncomeStatisticsInfo {
    var id: String? = null//统计ID
    var guildId: String? = null//公会ID
    var statisticsMonth: String? = null//统计月份
    var incomeAmount: Double = 0.0//成员月收入总金额
    var commissionAmount: Double = 0.0//佣金金额
    var commissionRate: Double = 0.0//佣金比例
    var bizDesc: String? = null//业务描述
    var bizName: String? = null//业务名称
    var status: Int = 0//业务状态：1-待到账；2-已到账
    var paymentTime: String? = null//到账时间
}