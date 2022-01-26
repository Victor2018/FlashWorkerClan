package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IUserCouponDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IUserCouponDS {

    val guildCouponData: LiveData<HttpResult<GuildCouponReq>>
    suspend fun fetchGuildCoupon(token: String?)

    val checkReceiveCouponData: LiveData<HttpResult<CheckReceiveCouponReq>>
    suspend fun checkReceiveCoupon(token: String?,body: CheckReceiveCouponParm?)

    val receiveCouponData: LiveData<HttpResult<BaseReq>>
    suspend fun receiveCoupon(token: String?,body: ReceiveCouponParm?)

    val userCouponData: LiveData<HttpResult<UserCouponReq>>
    suspend fun fetchUserCoupon(token: String?,body: UserCouponParm?)

    val useCouponData: LiveData<HttpResult<BaseReq>>
    suspend fun useCoupon(token: String?,body: UseCouponParm?)


}