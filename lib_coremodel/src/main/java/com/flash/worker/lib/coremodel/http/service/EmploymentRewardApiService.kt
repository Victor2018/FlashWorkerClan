package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmploymentRewardApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmploymentRewardApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmploymentRewardApiService {

    @POST(EmploymentRewardApi.EMPLOYER_REWARD)
    suspend fun fetchEmployerReward(
            @Header("X-TOKEN") token: String?,
            @Body body: EmploymentRewardParm?): NetworkResponse<EmploymentRewardReq, HttpError>

    @POST(EmploymentRewardApi.RECEIVE_REWARD)
    suspend fun receiveEmploymentReward(
            @Header("X-TOKEN") token: String?,
            @Body body: ReceiveEmploymentRewardParm?): NetworkResponse<ReceiveEmploymentRewardReq, HttpError>

}