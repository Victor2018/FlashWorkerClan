package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AccountVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class AccountVM(private val dataSource: IAccountDS): ViewModel() {

    val accountInfoData = dataSource.accountInfoData
    fun fetchAccountInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchAccountInfo(token)
        }
    }

    val balanceFlowData = dataSource.balanceFlowData
    fun fetchBalanceFlow(token: String?,body: BalanceFlowParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchBalanceFlow(token,body)
        }
    }

    val frozenFlowData = dataSource.frozenFlowData
    fun fetchFrozenFlow(token: String?,body: FrozenFlowParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchFrozenFlow(token,body)
        }
    }

    val setTradePasswordData = dataSource.setTradePasswordData
    fun setTradePassword(token: String?,body: SetTradePasswordParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.setTradePassword(token,body)
        }
    }

    val verifyResetTradePwdData = dataSource.verifyResetTradePwdData
    fun verifyResetTradePwd(token: String?,body: VerifyResetTradePwdParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.verifyResetTradePwd(token,body)
        }
    }

    val resetTradePwdData = dataSource.resetTradePwdData
    fun resetTradePwd(token: String?,body: ResetTradePwdParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.resetTradePwd(token,body)
        }
    }

    val modifyTradePwdData = dataSource.modifyTradePwdData
    fun modifyTradePwd(token: String?,body: ModifyTradePasswordParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.modifyTradePwd(token,body)
        }
    }

    val withdrawData = dataSource.withdrawData
    fun withdraw(token: String?,body: WithdrawParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.withdraw(token,body)
        }
    }

    val withdrawConfirmDetailData = dataSource.withdrawConfirmDetailData
    fun fetchWithdrawConfirmDetail(token: String?,body: WithdrawConfirmDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchWithdrawConfirmDetail(token,body)
        }
    }

    val balanceFlowDetailData = dataSource.balanceFlowDetailData
    fun fetchBalanceFlowDetail(token: String?,outTradeNo: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchBalanceFlowDetail(token,outTradeNo)
        }
    }

    val incomeStatisticsData = dataSource.incomeStatisticsData
    fun fetchIncomeStatistics(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchIncomeStatistics(token)
        }
    }

    val expendStatisticsData = dataSource.expendStatisticsData
    fun fetchExpendStatistics(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchExpendStatistics(token)
        }
    }

    val withdrawHistoryData = dataSource.withdrawHistoryData
    fun fetchWithdrawHistory(token: String?,body: WithdrawHistoryParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchWithdrawHistory(token,body)
        }
    }

}