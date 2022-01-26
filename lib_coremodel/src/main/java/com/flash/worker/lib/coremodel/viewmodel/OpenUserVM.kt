package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.BindAliPayParm
import com.flash.worker.lib.coremodel.data.parm.OpenUserUnBindParm
import com.flash.worker.lib.coremodel.http.interfaces.IOpenUserDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OpenUserVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class OpenUserVM(private val dataSource: IOpenUserDS): ViewModel() {

    val bindAliPayData = dataSource.bindAliPayData
    fun bindAliPay(token: String?,body: BindAliPayParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.bindAliPay(token,body)
        }
    }

    val aliAuthData = dataSource.aliAuthData
    fun fetchAliAuthInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchAliAuthInfo(token)
        }
    }

    val openUserInfoData = dataSource.openUserInfoData
    fun fetchOpenUserInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchOpenUserInfo(token)
        }
    }

    val openUserUnBindData = dataSource.openUserUnBindData
    fun openUserUnBind(token: String?,body: OpenUserUnBindParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.openUserUnBind(token,body)
        }
    }

}