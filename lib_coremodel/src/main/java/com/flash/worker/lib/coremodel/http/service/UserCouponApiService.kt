package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.UserCouponApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserCouponApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface UserCouponApiService {

    @GET(UserCouponApi.GUILD_COUPON)
    suspend fun fetchGuildCoupon(
            @Header("X-TOKEN") token: String?): NetworkResponse<GuildCouponReq, HttpError>

    @POST(UserCouponApi.CHECK_RECEIVE_COUPON)
    suspend fun checkReceiveCoupon(
            @Header("X-TOKEN") token: String?,
            @Body body: CheckReceiveCouponParm?): NetworkResponse<CheckReceiveCouponReq, HttpError>

    @POST(UserCouponApi.RECEIVE_COUPON)
    suspend fun receiveCoupon(
            @Header("X-TOKEN") token: String?,
            @Body body: ReceiveCouponParm?): NetworkResponse<BaseReq, HttpError>

    @POST(UserCouponApi.USER_COUPON)
    suspend fun fetchUserCoupon(
            @Header("X-TOKEN") token: String?,
            @Body body: UserCouponParm?): NetworkResponse<UserCouponReq, HttpError>

    @POST(UserCouponApi.USE_COUPON)
    suspend fun useCoupon(
            @Header("X-TOKEN") token: String?,
            @Body body: UseCouponParm?): NetworkResponse<BaseReq, HttpError>

}