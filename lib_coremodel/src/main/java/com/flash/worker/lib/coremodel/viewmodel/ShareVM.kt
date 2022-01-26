package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IShareDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareVM(private val dataSource: IShareDS): ViewModel() {

    val shareInfoData = dataSource.shareInfoData
    fun fetchShareInfo(token: String?,body: ShareInfoParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchShareInfo(token,body)
        }
    }

    val shareGuildInfoData = dataSource.shareGuildInfoData
    fun fetchShareGuildInfo(token: String?,body: ShareGuidInfoParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchShareGuildInfo(token,body)
        }
    }

    val shareTaskInfoData = dataSource.shareTaskInfoData
    fun fetchShareTaskInfo(token: String?,body: ShareTaskInfoParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchShareTaskInfo(token,body)
        }
    }


}