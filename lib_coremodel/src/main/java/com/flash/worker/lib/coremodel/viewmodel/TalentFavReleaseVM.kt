package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.ITalentFavReleaseDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavReleaseVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavReleaseVM(private val dataSource: ITalentFavReleaseDS): ViewModel() {

    val addFavData = dataSource.addFavData
    fun addFavRelease(token: String?,body: TalentAddFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.addFavRelease(token,body)
        }
    }

    val cancelFavData = dataSource.cancelFavData
    fun cancelFavRelease(token: String?,body: TalentCancelFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelFavRelease(token,body)
        }
    }

    val favReleaseData = dataSource.favReleaseData
    fun fetchFavRelease(token: String?,body: TalentFavReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchFavRelease(token,body)
        }
    }


}