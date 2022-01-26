package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountInfoData
 * Author: Victor
 * Date: 2021/1/29 15:34
 * Description: 
 * -----------------------------------------------------------------
 */
class AccountInfoData: Serializable {
    var availableBalance: Double = 0.0//可用余额
    var frozenAmount: Double = 0.0//冻结金额
    var totalBalance: Double = 0.0//账户余额
    var hasTradePassword: Boolean = false//是否设置过交易密码：true-已设置；false-未设置
}