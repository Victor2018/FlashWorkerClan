package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.http.interfaces.IGuildRedEnvelopeDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildRedEnvelopeVM(private val dataSource: IGuildRedEnvelopeDS): ViewModel() {

    val redEnvelopeInfoData = dataSource.redEnvelopeInfoData
    fun fetchRedEnvelopeInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchRedEnvelopeInfo(token)
        }
    }

    val receiveRedEnvelopeData = dataSource.receiveRedEnvelopeData
    fun receiveRedEnvelope(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.receiveRedEnvelope(token)
        }
    }

    val guildRedEnvelopeTipsData = dataSource.guildRedEnvelopeTipsData
    fun fetchGuildRedEnvelopeTips(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchGuildRedEnvelopeTips(token)
        }
    }

}