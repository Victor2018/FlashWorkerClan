package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAccountDS
import com.flash.worker.lib.coremodel.http.interfaces.ICreditScoreDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditScoreVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class CreditScoreVM(private val dataSource: ICreditScoreDS): ViewModel() {

    val talentCreditScoreData = dataSource.talentCreditScoreData
    fun fetchTalentCreditScore(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentCreditScore(token)
        }
    }

    val employerCreditScoreData = dataSource.employerCreditScoreData
    fun fetchEmployerCreditScore(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerCreditScore(token)
        }
    }


}