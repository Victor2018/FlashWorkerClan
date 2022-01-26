package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.SaveTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TaskPrepaidDetailReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.TaskApi
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskApiService
 * Author: Victor
 * Date: 2021/12/6 15:07
 * Description: 
 * -----------------------------------------------------------------
 */
interface TaskApiService {

    @POST(TaskApi.SAVE_TASK)
    suspend fun saveTask(
        @Header("X-TOKEN") token: String?,
        @Body body: SaveTaskParm?): NetworkResponse<BaseReq,HttpError>

    @POST(TaskApi.UPDATE_SAVE_TASK)
    suspend fun updateSaveTask(
        @Header("X-TOKEN") token: String?,
        @Body body: SaveTaskParm?): NetworkResponse<BaseReq,HttpError>

    @POST(TaskApi.RELEASE_TASK)
    suspend fun releaseTask(
        @Header("X-TOKEN") token: String?,
        @Body body: ReleaseTaskParm?): NetworkResponse<BaseReq,HttpError>

    @POST(TaskApi.UPDATE_RELEASE_TASK)
    suspend fun updateReleaseTask(
        @Header("X-TOKEN") token: String?,
        @Body body: ReleaseTaskParm?): NetworkResponse<BaseReq,HttpError>

    @POST(TaskApi.PREPAID_DETAIL)
    suspend fun fetchPrepaidDetail(
        @Header("X-TOKEN") token: String?,
        @Body body: TaskPrepaidDetailParm?): NetworkResponse<TaskPrepaidDetailReq,HttpError>


}