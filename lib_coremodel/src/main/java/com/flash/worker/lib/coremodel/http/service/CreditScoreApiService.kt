package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.CreditScoreApi
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditScoreApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface CreditScoreApiService {

    @GET(CreditScoreApi.TALENT_CREDIT_SCORE)
    suspend fun fetchTalentCreditScore(
            @Header("X-TOKEN") token: String?): NetworkResponse<CreditScoreReq, HttpError>

    @GET(CreditScoreApi.EMPLOYER_CREDIT_SCORE)
    suspend fun fetchEmployerCreditScore(
            @Header("X-TOKEN") token: String?): NetworkResponse<CreditScoreReq, HttpError>

}