package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerFavReleaseDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFavReleaseVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFavReleaseVM(private val dataSource: IEmployerFavReleaseDS): ViewModel() {

    val addFavData = dataSource.addFavData
    fun addFavRelease(token: String?,body: EmployerAddFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.addFavRelease(token,body)
        }
    }

    val cancelFavData = dataSource.cancelFavData
    fun cancelFavRelease(token: String?,body: EmployerCancelFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelFavRelease(token,body)
        }
    }

    val favReleaseData = dataSource.favReleaseData
    fun fetchFavRelease(token: String?,body: EmployerFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchFavRelease(token,body)
        }
    }


}