package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.PayParm
import com.flash.worker.lib.coremodel.data.parm.PayQueryParm
import com.flash.worker.lib.coremodel.http.interfaces.IPayDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PayVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class PayVM(private val dataSource: IPayDS): ViewModel() {

    val payData = dataSource.payData
    fun pay(token: String?,body: PayParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.pay(token,body)
        }
    }

    val payQueryData = dataSource.payQueryData
    fun payQuery(token: String?,body: PayQueryParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.payQuery(token,body)
        }
    }

}