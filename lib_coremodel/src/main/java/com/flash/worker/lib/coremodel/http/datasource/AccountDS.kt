package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import com.flash.worker.lib.coremodel.http.service.AccountApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class AccountDS: AbsDS(),IAccountDS {

    override val accountInfoData = MutableLiveData<HttpResult<AccountInfoReq>>()
    override suspend fun fetchAccountInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            accountInfoData.value = fetchAccountInfoReq(token)
        }
    }

    override val balanceFlowData = MutableLiveData<HttpResult<BalanceFlowReq>>()
    override suspend fun fetchBalanceFlow(token: String?,body: BalanceFlowParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            balanceFlowData.value = fetchBalanceFlowReq(token,body)
        }
    }

    override val frozenFlowData = MutableLiveData<HttpResult<FrozenFlowReq>>()
    override suspend fun fetchFrozenFlow(token: String?,body: FrozenFlowParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            frozenFlowData.value = fetchFrozenFlowReq(token,body)
        }
    }

    override val setTradePasswordData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun setTradePassword(token: String?,body: SetTradePasswordParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            setTradePasswordData.value = setTradePasswordReq(token,body)
        }
    }

    override val verifyResetTradePwdData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun verifyResetTradePwd(token: String?,body: VerifyResetTradePwdParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            verifyResetTradePwdData.value = verifyResetTradePwdReq(token,body)
        }
    }

    override val resetTradePwdData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun resetTradePwd(token: String?,body: ResetTradePwdParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            resetTradePwdData.value = resetTradePwdReq(token,body)
        }
    }

    override val modifyTradePwdData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun modifyTradePwd(token: String?,body: ModifyTradePasswordParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            modifyTradePwdData.value = modifyTradePwdReq(token,body)
        }
    }

    override val withdrawData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun withdraw(token: String?,body: WithdrawParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            withdrawData.value = withdrawReq(token,body)
        }
    }

    override val withdrawConfirmDetailData = MutableLiveData<HttpResult<WithdrawConfirmDetailReq>>()
    override suspend fun fetchWithdrawConfirmDetail(token: String?,body: WithdrawConfirmDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            withdrawConfirmDetailData.value = withdrawConfirmDetailReq(token,body)
        }
    }

    override val balanceFlowDetailData = MutableLiveData<HttpResult<BalanceFlowDetailReq>>()
    override suspend fun fetchBalanceFlowDetail(token: String?,outTradeNo: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            balanceFlowDetailData.value = balanceFlowDetailReq(token,outTradeNo)
        }
    }

    override val incomeStatisticsData = MutableLiveData<HttpResult<IncomeStatisticsReq>>()
    override suspend fun fetchIncomeStatistics(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            incomeStatisticsData.value = incomeStatisticsReq(token)
        }
    }

    override val expendStatisticsData = MutableLiveData<HttpResult<ExpendStatisticsReq>>()
    override suspend fun fetchExpendStatistics(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            expendStatisticsData.value = expendStatisticsReq(token)
        }
    }

    override val withdrawHistoryData = MutableLiveData<HttpResult<WithdrawHistoryReq>>()
    override suspend fun fetchWithdrawHistory(token: String?,body: WithdrawHistoryParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            withdrawHistoryData.value = fetchWithdrawHistoryReq(token,body)
        }
    }

    private suspend fun <T> fetchAccountInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                        .fetchAccountInfo(token)) as T
    }

    private suspend fun <T> fetchBalanceFlowReq(token: String?,body: BalanceFlowParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                        .fetchBalanceFlow(token,body)) as T
    }

    private suspend fun <T> fetchFrozenFlowReq(token: String?,body: FrozenFlowParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                        .fetchFrozenFlow(token,body)) as T
    }

    private suspend fun <T> setTradePasswordReq(
            token: String?,body: SetTradePasswordParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                        .setTradePassword(token,body)) as T
    }

    private suspend fun <T> verifyResetTradePwdReq(
            token: String?,body: VerifyResetTradePwdParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                        .verifyResetTradePwd(token,body)) as T
    }


    private suspend fun <T> resetTradePwdReq(
            token: String?,body: ResetTradePwdParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .resetTradePwd(token,body)) as T
    }

    private suspend fun <T> modifyTradePwdReq(
            token: String?,body: ModifyTradePasswordParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .modifyTradePwd(token,body)) as T
    }

    private suspend fun <T> withdrawReq(token: String?,body: WithdrawParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .withdraw(token,body)) as T
    }

    private suspend fun <T> withdrawConfirmDetailReq(token: String?,body: WithdrawConfirmDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .withdrawConfirmDetail(token,body)) as T
    }

    private suspend fun <T> balanceFlowDetailReq(token: String?,outTradeNo: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .fetchBalanceFlowDetail(token,outTradeNo)) as T
    }

    private suspend fun <T> incomeStatisticsReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .fetchIncomeStatistics(token)) as T
    }

    private suspend fun <T> expendStatisticsReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .fetchExpendStatistics(token)) as T
    }
    private suspend fun <T> fetchWithdrawHistoryReq(token: String?,body: WithdrawHistoryParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AccountApiService::class.java)
                .fetchWithdrawHistory(token,body)) as T
    }


}