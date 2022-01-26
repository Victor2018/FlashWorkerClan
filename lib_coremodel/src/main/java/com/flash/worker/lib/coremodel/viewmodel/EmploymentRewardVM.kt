package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IEmploymentRewardDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentRewardVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmploymentRewardVM(private val dataSource: IEmploymentRewardDS): ViewModel() {

    val employmentRewardData = dataSource.employmentRewardData
    fun fetchEmployerReward(token: String?,body: EmploymentRewardParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmploymentReward(token,body)
        }
    }

    val receiveEmploymentRewardData = dataSource.receiveEmploymentRewardData
    fun receiveEmploymentReward(token: String?,body: ReceiveEmploymentRewardParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.receiveEmploymentReward(token,body)
        }
    }


}