package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ICommonDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface ICommonDS {
    val areaTreeData: LiveData<HttpResult<AreaTreeReq>>
    suspend fun fetchAreaTreeData(body: AreaTreeParm?)

    val talentTypeData: LiveData<HttpResult<TalentTypeReq>>
    suspend fun fetchTalentTypeData()

    val cityAreaData: LiveData<HttpResult<CityAreaReq>>
    suspend fun fetchCityAreaData(token: String?,city: String?)

    val systemTimeData: LiveData<HttpResult<SystemTimeReq>>
    suspend fun fetchSystemTime(token: String?)

    val rewardLabelData: LiveData<HttpResult<RewardLabelReq>>
    suspend fun fetchRewardLabel(token: String?)

    val talentViolationLabelData: LiveData<HttpResult<ViolationLabelReq>>
    suspend fun fetchTalentViolationLabel(token: String?)

    val employerViolationLabelData: LiveData<HttpResult<ViolationLabelReq>>
    suspend fun fetchEmployerViolationLabel(token: String?)
}