package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IAccountDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IAccountDS {

    val accountInfoData: LiveData<HttpResult<AccountInfoReq>>
    suspend fun fetchAccountInfo(token: String?)

    val balanceFlowData: LiveData<HttpResult<BalanceFlowReq>>
    suspend fun fetchBalanceFlow(token: String?,body: BalanceFlowParm?)

    val frozenFlowData: LiveData<HttpResult<FrozenFlowReq>>
    suspend fun fetchFrozenFlow(token: String?,body: FrozenFlowParm?)

    val setTradePasswordData: LiveData<HttpResult<BaseReq>>
    suspend fun setTradePassword(token: String?,body: SetTradePasswordParm?)

    val verifyResetTradePwdData: LiveData<HttpResult<BaseReq>>
    suspend fun verifyResetTradePwd(token: String?,body: VerifyResetTradePwdParm?)

    val resetTradePwdData: LiveData<HttpResult<BaseReq>>
    suspend fun resetTradePwd(token: String?,body: ResetTradePwdParm?)

    val modifyTradePwdData: LiveData<HttpResult<BaseReq>>
    suspend fun modifyTradePwd(token: String?,body: ModifyTradePasswordParm?)

    val withdrawData: LiveData<HttpResult<BaseReq>>
    suspend fun withdraw(token: String?,body: WithdrawParm?)

    val withdrawConfirmDetailData: LiveData<HttpResult<WithdrawConfirmDetailReq>>
    suspend fun fetchWithdrawConfirmDetail(token: String?,body: WithdrawConfirmDetailParm?)

    val balanceFlowDetailData: LiveData<HttpResult<BalanceFlowDetailReq>>
    suspend fun fetchBalanceFlowDetail(token: String?,outTradeNo: String?)

    val incomeStatisticsData: LiveData<HttpResult<IncomeStatisticsReq>>
    suspend fun fetchIncomeStatistics(token: String?)

    val expendStatisticsData: LiveData<HttpResult<ExpendStatisticsReq>>
    suspend fun fetchExpendStatistics(token: String?)

    val withdrawHistoryData: LiveData<HttpResult<WithdrawHistoryReq>>
    suspend fun fetchWithdrawHistory(token: String?,body: WithdrawHistoryParm?)

}