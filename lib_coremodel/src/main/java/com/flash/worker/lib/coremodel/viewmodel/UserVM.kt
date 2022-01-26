package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.bean.EmployerBaseInfo
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IUserDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class UserVM(private val dataSource: IUserDS): ViewModel() {

    val userInfoData = dataSource.userInfoData
    fun fetchUserInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUserInfo(token)
        }
    }

    val updateAvatarData = dataSource.updateAvatarData
    fun updateAvatar(token: String?,body: UpdateAvatarParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateAvatar(token,body)
        }
    }

    val updateUserNameData = dataSource.updateUserNameData
    fun updateUserName(token: String?,body: UpdateUserNameParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateUserName(token,body)
        }
    }

    val updateWorkYearsData = dataSource.updateWorkYearsData
    fun updateWorkYears(token: String?,body: UpdateWorkYearsParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateWorkYears(token,body)
        }
    }

    val updateIdentityData = dataSource.updateIdentityData
    fun updateIdentity(token: String?,body: UpdateIdentityParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateIdentity(token,body)
        }
    }

    val updateHeightData = dataSource.updateHeightData
    fun updateHeight(token: String?,body: UpdateHeightParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateHeight(token,body)
        }
    }

    val updateWeightData = dataSource.updateWeightData
    fun updateWeight(token: String?,body: UpdateWeightParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateWeight(token,body)
        }
    }

    val updateLiveCityData = dataSource.updateLiveCityData
    fun updateLiveCity(token: String?,body: UpdateLiveCityParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateLiveCity(token,body)
        }
    }

    val updateEmergencyContactData = dataSource.updateEmergencyContactData
    fun updateEmergencyContact(token: String?,body: UpdateEmergencyContactParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateEmergencyContact(token,body)
        }
    }

    val imLoginInfoData = dataSource.imLoginInfoData
    fun fetchImLoginInfo(token: String?,userId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchImLoginInfo(token,userId)
        }
    }

    val imUserInfoData = dataSource.imUserInfoData
    fun fetchImUserInfo(token: String?,imAccid: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchImUserInfo(token,imAccid)
        }
    }

    val authData = dataSource.authData
    fun auth(token: String?,body: AuthParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.auth(token,body)
        }
    }

    val checkTalentBaseInfoData = dataSource.checkTalentBaseInfoData
    fun checkTalentBaseInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkTalentBaseInfo(token)
        }
    }

    val updateTalentBaseInfoData = dataSource.updateTalentBaseInfoData
    fun updateBaseInfo(token: String?,body: ResumeBaseInfo?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateTalentBaseInfo(token,body)
        }
    }

    val checkEmployerBaseInfoData = dataSource.checkEmployerBaseInfoData
    fun checkEmployerBaseInfo(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkEmployerBaseInfo(token)
        }
    }

    val updateEmployerBaseInfoData = dataSource.updateEmployerBaseInfoData
    fun updateEmployerBaseInfo(token: String?,body: EmployerBaseInfo?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateEmployerBaseInfo(token,body)
        }
    }

    val checkCancelAccountData = dataSource.checkCancelAccountData
    fun checkCancelAccount(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.checkCancelAccount(token)
        }
    }

    val cancelAccountVerifyData = dataSource.cancelAccountVerifyData
    fun cancelAccountVerify(token: String?,body: CancelAccountVerifyParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelAccountVerify(token,body)
        }
    }

    val cancelAccountData = dataSource.cancelAccountData
    fun cancelAccount(token: String?,body: CancelAccountParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.cancelAccount(token,body)
        }
    }

    val realNameData = dataSource.realNameData
    fun realName(token: String?,body: RealNameParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.realName(token,body)
        }
    }

    val updateUserInfoData = dataSource.updateUserInfoData
    fun updateUserInfo(token: String?,body: UpdateUserInfoParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateUserInfo(token,body)
        }
    }

    val updateInviteUserData = dataSource.updateInviteUserData
    fun updateInviteUser(token: String?,body: UpdateInviteUserParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateInviteUser(token,body)
        }
    }

}