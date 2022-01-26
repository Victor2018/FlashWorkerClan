package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberIncomeRankInfo
 * Author: Victor
 * Date: 2021/5/20 17:48
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberIncomeRankInfo {
    var guildId: String? = null//公会ID
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var headpic: String? = null//头像
    var sex: Int = 0//性别（0-女；1-男；
    var incomeAmount: Double = 0.0//收入金额
}