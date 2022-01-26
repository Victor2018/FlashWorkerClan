package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IAuthDS
import com.flash.worker.lib.coremodel.http.service.AuthApiService
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.BindPhoneReq
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.data.req.WechatLoginReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class AuthDS:AbsDS(),IAuthDS {

    override val smsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun fetchSmsData(body: SmsParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            smsData.value = smsDataReq(body)
        }
    }

    override val bindPhoneSmsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun fetchBindPhoneSmsData(body: SmsParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            bindPhoneSmsData.value = bindPhoneSmsDataReq(body)
        }
    }

    override val smsLoginData = MutableLiveData<HttpResult<LoginReq>>()
    override suspend fun smsLogin(body: SmsLoginParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            smsLoginData.value = smsDataReq(body)
        }
    }

    override val logOutData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun logOut(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            logOutData.value = logOutReq(token)
        }
    }

    override val bindPhoneData = MutableLiveData<HttpResult<BindPhoneReq>>()
    override suspend fun bindPhone(body: BindPhoneParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            bindPhoneData.value = bindPhoneReq(body)
        }
    }

    override val oneKeyLoginData = MutableLiveData<HttpResult<LoginReq>>()
    override suspend fun oneKeyLogin(body: OneKeyLoginParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            oneKeyLoginData.value = oneKeyLoginReq(body)
        }
    }

    override val wechatLoginData = MutableLiveData<HttpResult<WechatLoginReq>>()
    override suspend fun wechatLogin(body: WechatLoginParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            wechatLoginData.value = wechatLoginReq(body)
        }
    }

    override val verifySmsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun sendVerifySms(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            verifySmsData.value = sendVerifySmsReq(token)
        }
    }

    private suspend fun <T> smsDataReq(body: SmsParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .getSms(body)) as T
    }

    private suspend fun <T> bindPhoneSmsDataReq(body: SmsParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .getBindPhoneSms(body)) as T
    }

    private suspend fun <T> smsDataReq(body: SmsLoginParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .smsLogin(body)) as T
    }

    private suspend fun <T> logOutReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .logOut(token)) as T
    }

    private suspend fun <T> bindPhoneReq(body: BindPhoneParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .bindPhone(body)) as T
    }

    private suspend fun <T> oneKeyLoginReq(body: OneKeyLoginParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .oneKeyLogin(body)) as T
    }

    private suspend fun <T> wechatLoginReq(body: WechatLoginParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .wechatLogin(body)) as T
    }

    private suspend fun <T> sendVerifySmsReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(AuthApiService::class.java)
                .sendVerifySms(token)) as T
    }





}