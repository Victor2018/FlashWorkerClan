package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAuthDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class AuthVM(private val dataSource: IAuthDS): ViewModel() {

    val smsData = dataSource.smsData
    fun fetchSmsData(body: SmsParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSmsData(body)
        }
    }

    val bindPhoneSmsData = dataSource.bindPhoneSmsData
    fun fetchBindPhoneSmsData(body: SmsParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchBindPhoneSmsData(body)
        }
    }

    val smsLoginData = dataSource.smsLoginData
    fun smsLogin(body: SmsLoginParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.smsLogin(body)
        }
    }

    val logOutData = dataSource.logOutData
    fun logOut(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.logOut(token)
        }
    }

    val bindPhoneData = dataSource.bindPhoneData
    fun bindPhone(body: BindPhoneParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.bindPhone(body)
        }
    }

    val oneKeyLoginData = dataSource.oneKeyLoginData
    fun oneKeyLogin(body: OneKeyLoginParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.oneKeyLogin(body)
        }
    }

    val wechatLoginData = dataSource.wechatLoginData
    fun wechatLogin(body: WechatLoginParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.wechatLogin(body)
        }
    }

    val verifySmsData = dataSource.verifySmsData
    fun sendVerifySms(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.sendVerifySms(token)
        }
    }

}