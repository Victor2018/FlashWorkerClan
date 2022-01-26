package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmployerRewardDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmploymentRewardDS {

    val employmentRewardData: LiveData<HttpResult<EmploymentRewardReq>>
    suspend fun fetchEmploymentReward(token: String?,body: EmploymentRewardParm?)

    val receiveEmploymentRewardData: LiveData<HttpResult<ReceiveEmploymentRewardReq>>
    suspend fun receiveEmploymentReward(token: String?,body: ReceiveEmploymentRewardParm?)

}