package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerParm
import com.flash.worker.lib.coremodel.data.parm.UpdateEmployerParm
import com.flash.worker.lib.coremodel.data.req.EditEmployerDetailReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployersReq
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.EmployerApi
import retrofit2.http.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface EmployerApiService {

    @POST(EmployerApi.SAVE_EMPLOYER)
    suspend fun saveEmployer(
            @Header("X-TOKEN") token: String?,
            @Body body: SaveEmployerParm?): NetworkResponse<BaseReq, HttpError>

    @GET(EmployerApi.EMPLOYER_LIST)
    suspend fun getEmployers(
            @Header("X-TOKEN") token: String?): NetworkResponse<EmployersReq, HttpError>

    @GET(EmployerApi.EDIT_EMPLOYER_DETAIL)
    suspend fun getEditEmployerDetail(
            @Header("X-TOKEN") token: String?,
            @Query("employerId") employerId: String?): NetworkResponse<EditEmployerDetailReq, HttpError>

    @POST(EmployerApi.UPDATE_EMPLOYER)
    suspend fun updateEmployer(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateEmployerParm?): NetworkResponse<BaseReq, HttpError>

    @POST(EmployerApi.DELETE_EMPLOYER)
    suspend fun deleteEmployer(
            @Header("X-TOKEN") token: String?,
            @Query("employerId") employerId: String?): NetworkResponse<BaseReq, HttpError>

    @GET(EmployerApi.INVITE_TALENT_EMPLOYER)
    suspend fun fetchInviteTalentEmployer(
            @Header("X-TOKEN") token: String?): NetworkResponse<EmployersReq, HttpError>

}