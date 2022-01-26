package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IUserCouponDS
import com.flash.worker.lib.coremodel.http.service.UserCouponApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class UserCouponDS: AbsDS(), IUserCouponDS {

    override val guildCouponData = MutableLiveData<HttpResult<GuildCouponReq>>()
    override suspend fun fetchGuildCoupon(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            guildCouponData.value = guildCouponReq(token)
        }
    }

    override val checkReceiveCouponData = MutableLiveData<HttpResult<CheckReceiveCouponReq>>()
    override suspend fun checkReceiveCoupon(token: String?,body: CheckReceiveCouponParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkReceiveCouponData.value = checkReceiveCouponReq(token,body)
        }
    }

    override val receiveCouponData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun receiveCoupon(token: String?,body: ReceiveCouponParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            receiveCouponData.value = receiveCouponReq(token,body)
        }
    }

    override val userCouponData = MutableLiveData<HttpResult<UserCouponReq>>()
    override suspend fun fetchUserCoupon(token: String?,body: UserCouponParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            userCouponData.value = userCouponReq(token,body)
        }
    }

    override val useCouponData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun useCoupon(token: String?,body: UseCouponParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            useCouponData.value = useCouponReq(token,body)
        }
    }


    private suspend fun <T> guildCouponReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserCouponApiService::class.java)
                        .fetchGuildCoupon(token)) as T
    }

    private suspend fun <T> checkReceiveCouponReq(token: String?,body: CheckReceiveCouponParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserCouponApiService::class.java)
                        .checkReceiveCoupon(token,body)) as T
    }

    private suspend fun <T> receiveCouponReq(token: String?,body: ReceiveCouponParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserCouponApiService::class.java)
                        .receiveCoupon(token,body)) as T
    }

    private suspend fun <T> userCouponReq(token: String?,body: UserCouponParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserCouponApiService::class.java)
                        .fetchUserCoupon(token,body)) as T
    }

    private suspend fun <T> useCouponReq(token: String?,body: UseCouponParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserCouponApiService::class.java)
                        .useCoupon(token,body)) as T
    }



}