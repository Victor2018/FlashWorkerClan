package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ICreditScoreDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface ICreditScoreDS {

    val talentCreditScoreData: LiveData<HttpResult<CreditScoreReq>>
    suspend fun fetchTalentCreditScore(token: String?)

    val employerCreditScoreData: LiveData<HttpResult<CreditScoreReq>>
    suspend fun fetchEmployerCreditScore(token: String?)

}