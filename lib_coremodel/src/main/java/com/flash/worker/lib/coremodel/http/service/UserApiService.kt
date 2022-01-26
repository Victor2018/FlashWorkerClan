package com.flash.worker.lib.coremodel.http.service

import com.flash.worker.lib.coremodel.data.bean.EmployerBaseInfo
import com.flash.worker.lib.coremodel.data.bean.HttpError
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.adapter.NetworkResponse
import com.flash.worker.lib.coremodel.http.api.UserApi
import com.flash.worker.lib.coremodel.data.req.*
import retrofit2.http.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserApiService
 * Author: Victor
 * Date: 2020/7/8 下午 04:49
 * Description:
 * -----------------------------------------------------------------
 */
interface UserApiService {

    @GET(UserApi.GET_USER_INFO)
    suspend fun getUserInfo(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<UserInfoReq, HttpError>

    @POST(UserApi.UPDATE_USER_AVATAR)
    suspend fun updateAvatar(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateAvatarParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_LIVE_CIGY)
    suspend fun updateLiveCity(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateLiveCityParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_USER_NAME)
    suspend fun updateUserName(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateUserNameParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_WORK_YEARS)
    suspend fun updateWorkYears(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateWorkYearsParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_IDENTITY)
    suspend fun updateIdentity(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateIdentityParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_HEIGHT)
    suspend fun updateHeight(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateHeightParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_WEIGHT)
    suspend fun updateWeight(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateWeightParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_EMERGENCY_CONTACT)
    suspend fun updateEmergencyContact(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateEmergencyContactParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.AUTH_REAL)
    suspend fun auth(
            @Header("X-TOKEN") token: String?,
            @Body body: AuthParm): NetworkResponse<AuthReq, HttpError>

    @POST(UserApi.CHECK_TALENT_BASE_INFO)
    suspend fun checkTalentBaseInfo(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<CheckTalentBaseInfoReq, HttpError>

    @POST(UserApi.UPDATE_TALENT_BASE_INFO)
    suspend fun updateTalentBaseInfo(
            @Header("X-TOKEN") token: String?,
            @Body body: ResumeBaseInfo?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.CHECK_EMPLOYER_BASE_INFO)
    suspend fun checkEmployerBaseInfo(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<CheckEmployerBaseInfoReq, HttpError>

    @POST(UserApi.UPDATE_EMPLOYER_BASE_INFO)
    suspend fun updateEmployerBaseInfo(
            @Header("X-TOKEN") token: String?,
            @Body body: EmployerBaseInfo?)
            : NetworkResponse<BaseReq, HttpError>

    @GET(UserApi.IM_LOGIN_INFO)
    suspend fun fetchImLoginInfo(
            @Header("X-TOKEN") token: String?,
            @Query("userId") userId : String?)
            : NetworkResponse<ImLoginInfoReq, HttpError>

    @GET(UserApi.IM_USER_INFO)
    suspend fun fetchImUserInfo(
            @Header("X-TOKEN") token: String?,
            @Query("imAccid") imAccid : String?)
            : NetworkResponse<ImUserInfoReq, HttpError>

    @POST(UserApi.CHECK_CANCEL_ACCOUNT)
    suspend fun checkCancelAccount(
            @Header("X-TOKEN") token: String?)
            : NetworkResponse<CheckCancelAccountReq, HttpError>

    @POST(UserApi.CANCEL_ACCOUNT_VERIFY)
    suspend fun cancelAccountVerify(
            @Header("X-TOKEN") token: String?,
            @Body body: CancelAccountVerifyParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.CANCEL_ACCOUNT)
    suspend fun cancelAccount(
            @Header("X-TOKEN") token: String?,
            @Body body: CancelAccountParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.REAL_NAME)
    suspend fun realName(
            @Header("X-TOKEN") token: String?,
            @Body body: RealNameParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_USER_INFO)
    suspend fun updateUserInfo(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateUserInfoParm?)
            : NetworkResponse<BaseReq, HttpError>

    @POST(UserApi.UPDATE_INVITE_USER)
    suspend fun updateInviteUser(
            @Header("X-TOKEN") token: String?,
            @Body body: UpdateInviteUserParm?)
            : NetworkResponse<BaseReq, HttpError>

}