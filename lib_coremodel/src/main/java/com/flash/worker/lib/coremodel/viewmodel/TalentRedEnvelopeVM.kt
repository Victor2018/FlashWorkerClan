package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.ITalentRedEnvelopeDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentRedEnvelopeVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentRedEnvelopeVM(private val dataSource: ITalentRedEnvelopeDS): ViewModel() {

    val activitytInfoData = dataSource.activitytInfoData
    fun fetchActivityInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchActivityInfo(token)
        }
    }

    val receiveTalentRedEnvelopeData = dataSource.receiveTalentRedEnvelopeData
    fun receiveTalentRedEnvelope(token: String?,body: ReceiveTalentRedEnvelopeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.receiveTalentRedEnvelope(token,body)
        }
    }

    val waitReceiveCountData = dataSource.waitReceiveCountData
    fun fetchWaitReceiveCount(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchWaitReceiveCount(token)
        }
    }

}