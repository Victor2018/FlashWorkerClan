package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.AccountApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface AccountApiService {

    @GET(AccountApi.ACCOUNT_INFO)
    suspend fun fetchAccountInfo(
            @Header("X-TOKEN") token: String?): NetworkResponse<AccountInfoReq, HttpError>

    @POST(AccountApi.BALANCE_FLOW)
    suspend fun fetchBalanceFlow(
            @Header("X-TOKEN") token: String?,
            @Body body: BalanceFlowParm?): NetworkResponse<BalanceFlowReq, HttpError>

    @POST(AccountApi.FROZEN_FLOW)
    suspend fun fetchFrozenFlow(
            @Header("X-TOKEN") token: String?,
            @Body body: FrozenFlowParm?): NetworkResponse<FrozenFlowReq, HttpError>

    @POST(AccountApi.SET_TRADE_PASSWORD)
    suspend fun setTradePassword (
            @Header("X-TOKEN") token: String?,
            @Body body: SetTradePasswordParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AccountApi.VERIFY_RESET_TRADE_PWD)
    suspend fun verifyResetTradePwd (
            @Header("X-TOKEN") token: String?,
            @Body body: VerifyResetTradePwdParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AccountApi.RESET_TRADE_PWD)
    suspend fun resetTradePwd (
            @Header("X-TOKEN") token: String?,
            @Body body: ResetTradePwdParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AccountApi.MODIFY_TRADE_PWD)
    suspend fun modifyTradePwd (
            @Header("X-TOKEN") token: String?,
            @Body body: ModifyTradePasswordParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AccountApi.WITHDRAW)
    suspend fun withdraw (
            @Header("X-TOKEN") token: String?,
            @Body body: WithdrawParm?): NetworkResponse<BaseReq, HttpError>

    @POST(AccountApi.WITHDRAW_CONFIRM_DETAIL)
    suspend fun withdrawConfirmDetail (
            @Header("X-TOKEN") token: String?,
            @Body body: WithdrawConfirmDetailParm?): NetworkResponse<WithdrawConfirmDetailReq, HttpError>

    @GET(AccountApi.BALANCE_FLOW_DETAIL)
    suspend fun fetchBalanceFlowDetail (
            @Header("X-TOKEN") token: String?,
            @Query("outTradeNo") outTradeNo : String?): NetworkResponse<BalanceFlowDetailReq, HttpError>

    @GET(AccountApi.INCOME_STATISTICS)
    suspend fun fetchIncomeStatistics (
            @Header("X-TOKEN") token: String?): NetworkResponse<IncomeStatisticsReq, HttpError>

    @GET(AccountApi.EXPEND_STATISTICS)
    suspend fun fetchExpendStatistics (
            @Header("X-TOKEN") token: String?): NetworkResponse<ExpendStatisticsReq, HttpError>

    @POST(AccountApi.WITHDRAW_HISTORY)
    suspend fun fetchWithdrawHistory (
        @Header("X-TOKEN") token: String?,
        @Body body: WithdrawHistoryParm?): NetworkResponse<WithdrawHistoryReq, HttpError>
}