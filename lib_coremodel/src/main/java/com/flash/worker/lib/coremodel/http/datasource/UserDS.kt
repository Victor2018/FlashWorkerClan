package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.EmployerBaseInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IUserDS
import com.flash.worker.lib.coremodel.http.service.UserApiService
import com.flash.worker.lib.coremodel.data.req.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class UserDS: AbsDS(), IUserDS {

    override val userInfoData = MutableLiveData<HttpResult<UserInfoReq>>()
    override suspend fun fetchUserInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            userInfoData.value = userInfoReq(token)
        }
    }

    override val updateAvatarData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateAvatar(token: String?, body: UpdateAvatarParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateAvatarData.value = updateAvatarReq(token,body)
        }
    }

    override val updateUserNameData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateUserName(token: String?, body: UpdateUserNameParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateUserNameData.value = updateUserNameReq(token,body)
        }
    }

    override val updateWorkYearsData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateWorkYears(token: String?, body: UpdateWorkYearsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateWorkYearsData.value = updateWorkYearsReq(token,body)
        }
    }

    override val updateIdentityData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateIdentity(token: String?, body: UpdateIdentityParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateIdentityData.value = updateIdentityReq(token,body)
        }
    }

    override val updateHeightData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateHeight(token: String?, body: UpdateHeightParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateHeightData.value = updateHeightReq(token,body)
        }
    }

    override val updateWeightData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateWeight(token: String?, body: UpdateWeightParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateWeightData.value = updateWeightReq(token,body)
        }
    }

    override val updateLiveCityData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateLiveCity(token: String?, body: UpdateLiveCityParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateLiveCityData.value = updateLiveCityReq(token,body)
        }
    }

    override val updateEmergencyContactData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateEmergencyContact(token: String?, body: UpdateEmergencyContactParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateEmergencyContactData.value = updateEmergencyContactReq(token,body)
        }
    }

    override val imLoginInfoData = MutableLiveData<HttpResult<ImLoginInfoReq>>()
    override suspend fun fetchImLoginInfo(token: String?, userId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            imLoginInfoData.value = fetchImLoginInfoReq(token,userId)
        }
    }

    override val imUserInfoData = MutableLiveData<HttpResult<ImUserInfoReq>>()
    override suspend fun fetchImUserInfo(token: String?, imAccid: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            imUserInfoData.value = fetchImUserInfoReq(token,imAccid)
        }
    }

    override val authData = MutableLiveData<HttpResult<AuthReq>>()
    override suspend fun auth(token: String?,body: AuthParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            authData.value = authReq(token,body)
        }
    }

    override val checkTalentBaseInfoData = MutableLiveData<HttpResult<CheckTalentBaseInfoReq>>()
    override suspend fun checkTalentBaseInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkTalentBaseInfoData.value = checkTalentBaseInfoReq(token)
        }
    }

    override val updateTalentBaseInfoData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateTalentBaseInfo(token: String?,body: ResumeBaseInfo?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateTalentBaseInfoData.value = updateTalentBaseInfoReq(token,body)
        }
    }

    override val checkEmployerBaseInfoData = MutableLiveData<HttpResult<CheckEmployerBaseInfoReq>>()
    override suspend fun checkEmployerBaseInfo(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkEmployerBaseInfoData.value = checkEmployerBaseInfoReq(token)
        }
    }

    override val updateEmployerBaseInfoData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateEmployerBaseInfo(token: String?,body: EmployerBaseInfo?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateEmployerBaseInfoData.value = updateEmployerBaseInfoReq(token,body)
        }
    }

    override val checkCancelAccountData = MutableLiveData<HttpResult<CheckCancelAccountReq>>()
    override suspend fun checkCancelAccount(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            checkCancelAccountData.value = checkCancelAccountReq(token)
        }
    }

    override val cancelAccountVerifyData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelAccountVerify(token: String?,body: CancelAccountVerifyParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelAccountVerifyData.value = cancelAccountVerifyReq(token,body)
        }
    }

    override val cancelAccountData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun cancelAccount(token: String?,body: CancelAccountParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            cancelAccountData.value = cancelAccountReq(token,body)
        }
    }

    override val realNameData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun realName(token: String?,body: RealNameParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            realNameData.value = realNameReq(token,body)
        }
    }

    override val updateUserInfoData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateUserInfo(token: String?,body: UpdateUserInfoParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateUserInfoData.value = updateUserInfoReq(token,body)
        }
    }

    override val updateInviteUserData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateInviteUser(token: String?,body: UpdateInviteUserParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateInviteUserData.value = updateInviteUserReq(token,body)
        }
    }

    private suspend fun <T> userInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .getUserInfo(token)) as T
    }

    private suspend fun <T> updateUserNameReq(
            token: String?,
            body: UpdateUserNameParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateUserName(token,body)) as T
    }

    private suspend fun <T> updateWorkYearsReq(
            token: String?,
            body: UpdateWorkYearsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateWorkYears(token,body)) as T
    }

    private suspend fun <T> updateIdentityReq(
            token: String?,
            body: UpdateIdentityParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateIdentity(token,body)) as T
    }

    private suspend fun <T> updateHeightReq(
            token: String?,
            body: UpdateHeightParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateHeight(token,body)) as T
    }

    private suspend fun <T> updateWeightReq(
            token: String?,
            body: UpdateWeightParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateWeight(token,body)) as T
    }

    private suspend fun <T> updateAvatarReq(
            token: String?,
            body: UpdateAvatarParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateAvatar(token,body)) as T
    }

    private suspend fun <T> updateLiveCityReq(
            token: String?,
            body: UpdateLiveCityParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateLiveCity(token,body)) as T
    }

    private suspend fun <T> updateEmergencyContactReq(
            token: String?,
            body: UpdateEmergencyContactParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .updateEmergencyContact(token,body)) as T
    }

    private suspend fun <T> fetchImLoginInfoReq(
            token: String?,
            userId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .fetchImLoginInfo(token,userId)) as T
    }

    private suspend fun <T> fetchImUserInfoReq(
            token: String?,
            imAccid: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .fetchImUserInfo(token,imAccid)) as T
    }

    private suspend fun <T> authReq(token: String?,body: AuthParm): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .auth(token,body)) as T
    }

    private suspend fun <T> checkTalentBaseInfoReq(token: String?): T =
            withContext(Dispatchers.IO) {
                handleRespone(ApiClient.getApiService(UserApiService::class.java)
                        .checkTalentBaseInfo(token)) as T
    }

    private suspend fun <T> updateTalentBaseInfoReq(
            token: String?,
            body: ResumeBaseInfo?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .updateTalentBaseInfo(token,body)) as T
    }

    private suspend fun <T> checkEmployerBaseInfoReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .checkEmployerBaseInfo(token)) as T
    }

    private suspend fun <T> updateEmployerBaseInfoReq(
            token: String?,
            body: EmployerBaseInfo?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .updateEmployerBaseInfo(token,body)) as T
    }

    private suspend fun <T> checkCancelAccountReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .checkCancelAccount(token)) as T
    }

    private suspend fun <T> cancelAccountVerifyReq(token: String?,body: CancelAccountVerifyParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .cancelAccountVerify(token,body)) as T
    }

    private suspend fun <T> cancelAccountReq(token: String?,body: CancelAccountParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .cancelAccount(token,body)) as T
    }

    private suspend fun <T> realNameReq(token: String?,body: RealNameParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .realName(token,body)) as T
    }

    private suspend fun <T> updateUserInfoReq(token: String?,body: UpdateUserInfoParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .updateUserInfo(token,body)) as T
    }

    private suspend fun <T> updateInviteUserReq(token: String?,body: UpdateInviteUserParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(UserApiService::class.java)
                .updateInviteUser(token,body)) as T
    }


}