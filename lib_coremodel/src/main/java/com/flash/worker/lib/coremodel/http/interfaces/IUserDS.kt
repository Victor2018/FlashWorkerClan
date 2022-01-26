package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.EmployerBaseInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IUserDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IUserDS {
    val userInfoData: LiveData<HttpResult<UserInfoReq>>
    suspend fun fetchUserInfo(token: String?)

    val updateAvatarData: LiveData<HttpResult<BaseReq>>
    suspend fun updateAvatar(token: String?,body: UpdateAvatarParm?)

    val updateUserNameData: LiveData<HttpResult<BaseReq>>
    suspend fun updateUserName(token: String?,body: UpdateUserNameParm?)

    val updateWorkYearsData: LiveData<HttpResult<BaseReq>>
    suspend fun updateWorkYears(token: String?,body: UpdateWorkYearsParm?)

    val updateIdentityData: LiveData<HttpResult<BaseReq>>
    suspend fun updateIdentity(token: String?,body: UpdateIdentityParm?)

    val updateHeightData: LiveData<HttpResult<BaseReq>>
    suspend fun updateHeight(token: String?,body: UpdateHeightParm?)

    val updateWeightData: LiveData<HttpResult<BaseReq>>
    suspend fun updateWeight(token: String?,body: UpdateWeightParm?)

    val updateLiveCityData: LiveData<HttpResult<BaseReq>>
    suspend fun updateLiveCity(token: String?,body: UpdateLiveCityParm?)

    val updateEmergencyContactData: LiveData<HttpResult<BaseReq>>
    suspend fun updateEmergencyContact(token: String?,body: UpdateEmergencyContactParm?)

    val imLoginInfoData: LiveData<HttpResult<ImLoginInfoReq>>
    suspend fun fetchImLoginInfo(token: String?, userId: String?)

    val imUserInfoData: LiveData<HttpResult<ImUserInfoReq>>
    suspend fun fetchImUserInfo(token: String?, imAccid: String?)

    val authData: LiveData<HttpResult<AuthReq>>
    suspend fun auth(token: String?,body: AuthParm)

    val checkTalentBaseInfoData: LiveData<HttpResult<CheckTalentBaseInfoReq>>
    suspend fun checkTalentBaseInfo(token: String?)

    val updateTalentBaseInfoData: LiveData<HttpResult<BaseReq>>
    suspend fun updateTalentBaseInfo(token: String?,body: ResumeBaseInfo?)

    val checkEmployerBaseInfoData: LiveData<HttpResult<CheckEmployerBaseInfoReq>>
    suspend fun checkEmployerBaseInfo(token: String?)

    val updateEmployerBaseInfoData: LiveData<HttpResult<BaseReq>>
    suspend fun updateEmployerBaseInfo(token: String?,body: EmployerBaseInfo?)

    val checkCancelAccountData: LiveData<HttpResult<CheckCancelAccountReq>>
    suspend fun checkCancelAccount(token: String?)

    val cancelAccountVerifyData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelAccountVerify(token: String?,body: CancelAccountVerifyParm?)

    val cancelAccountData: LiveData<HttpResult<BaseReq>>
    suspend fun cancelAccount(token: String?,body: CancelAccountParm?)

    val realNameData: LiveData<HttpResult<BaseReq>>
    suspend fun realName(token: String?,body: RealNameParm?)

    val updateUserInfoData: LiveData<HttpResult<BaseReq>>
    suspend fun updateUserInfo(token: String?,body: UpdateUserInfoParm?)

    val updateInviteUserData: LiveData<HttpResult<BaseReq>>
    suspend fun updateInviteUser(token: String?,body: UpdateInviteUserParm?)

}