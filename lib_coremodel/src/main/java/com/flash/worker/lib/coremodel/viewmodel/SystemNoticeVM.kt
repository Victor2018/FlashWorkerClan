package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAuthDS
import com.flash.worker.lib.coremodel.http.interfaces.ISystemNoticeDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeVM(private val dataSource: ISystemNoticeDS): ViewModel() {

    val unReadNumData = dataSource.unReadNumData
    fun fetchUnreadNum(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUnreadNum(token)
        }
    }

    val markReadData = dataSource.markReadData
    fun markRead(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.markRead(token)
        }
    }

    val noticeData = dataSource.noticeData
    fun fetchNotice(token: String?,body: SystemNoticeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchNotice(token,body)
        }
    }

}