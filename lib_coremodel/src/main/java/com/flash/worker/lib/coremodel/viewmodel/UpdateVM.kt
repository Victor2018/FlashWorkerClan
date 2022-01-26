package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IUpdateDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateVM(private val dataSource: IUpdateDS): ViewModel() {

    val latestVersionData = dataSource.latestVersionData
    fun fetchLatestVersion(body: LatestVersionParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchLatestVersion(body)
        }
    }

}