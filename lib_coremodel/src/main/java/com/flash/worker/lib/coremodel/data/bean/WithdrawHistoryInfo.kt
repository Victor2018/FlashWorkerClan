package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawHistoryInfo
 * Author: Victor
 * Date: 2021/11/3 15:06
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawHistoryInfo {
    var outTradeNo: String? = null//交易单号
    var tradeTime: String? = null//交易时间
    var channel: String? = null//交易渠道
    var tradeAmount: Double = 0.0//交易金额
    var actualAmount: Double = 0.0//实际到账金额
    var feeAmount: Double = 0.0//手续费金额
    var tradeStatus: Int = 0//交易状态：1-审核中；2-审核通过；3-审核不通过；4-提现成功；5-提现失败
    var remark: String? = null//备注
}