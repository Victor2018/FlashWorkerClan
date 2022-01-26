package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BalanceFlowInfo
 * Author: Victor
 * Date: 2021/1/29 16:30
 * Description: 
 * -----------------------------------------------------------------
 */
class BalanceFlowInfo: Serializable {
    var outTradeNo: String? = null//交易流水号
    var tradeTime: String? = null//交易时间
    var tradeAmount: Double = 0.0//交易金额
    var totalBalance: Double = 0.0//当期账户余额
    var tradeType: Int = 0//交易类型：1-收入；2-支出；3-充值；4-提现；5-其他；6-冻结；7-解冻

    /**
     * 业务类型: 10-充值；15-提现；60-报酬支出;65-报酬收入;70-平台服务费;
     * 75-信用赔付;80-信用获赔;85-报酬赔付;90-报酬获赔;95-经费收入；100-公会红包;110-雇用奖励
     */
    var bizType: Int = 0
    var bizName: String? = null//业务名称
    var bizDesc: String? = null//业务描述
}