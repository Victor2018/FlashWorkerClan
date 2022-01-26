package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildIncomeStatisticsData
 * Author: Victor
 * Date: 2021/5/24 11:42
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildIncomeStatisticsData {
    var guildId: String? = null//公会ID
    var dayIncomeAmount: Double = 0.0//今日成员收入
    var monthIncomeAmount: Double = 0.0//本月成员收入
    var totalIncomeAmount: Double = 0.0//累计成员收入
}