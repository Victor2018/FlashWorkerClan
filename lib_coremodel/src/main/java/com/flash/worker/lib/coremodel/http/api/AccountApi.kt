package com.flash.worker.lib.coremodel.http.api


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountApi
 * Author: Victor
 * Date: 2021/4/12 20:11
 * Description: 
 * -----------------------------------------------------------------
 */
object AccountApi {
    const val ACCOUNT_INFO = "account/getAccountInfo"
    const val BALANCE_FLOW = "account/getBalanceFlow"
    const val FROZEN_FLOW = "account/getFrozenFlow"
    const val SET_TRADE_PASSWORD = "account/setTradePassword"
    const val VERIFY_RESET_TRADE_PWD = "account/resetTradePasswordSafeVerify"
    const val RESET_TRADE_PWD = "account/resetTradePassword"
    const val MODIFY_TRADE_PWD = "account/changeTradePassword"
    const val WITHDRAW = "account/withdraw"
    const val WITHDRAW_CONFIRM_DETAIL = "account/withdrawConfirmDetail"
    const val BALANCE_FLOW_DETAIL = "account/getFundFlowDetail"
    const val INCOME_STATISTICS = "account/getIncomeStatistics"
    const val EXPEND_STATISTICS = "account/getExpendStatistics"
    const val WITHDRAW_HISTORY = "account/getWithdrawOrderList"
}