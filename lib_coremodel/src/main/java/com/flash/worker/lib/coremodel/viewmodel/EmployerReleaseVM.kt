package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerReleaseDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleaseVM(private val dataSource: IEmployerReleaseDS): ViewModel() {

    val saveEmployerDraftsData = dataSource.saveEmployerDraftsData
    fun saveEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveEmployerDrafts(token,body)
        }
    }

    val saveEmployerReleaseData = dataSource.saveEmployerReleaseData
    fun saveEmployerRelease(token: String?,body: SaveEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveEmployerRelease(token,body)
        }
    }

    val employerReleaseData = dataSource.employerReleaseData
    fun fetchEmployerRelease(token: String?,body: EmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerRelease(token,body)
        }
    }

    val employerReleaseRefreshData = dataSource.employerReleaseRefreshData
    fun employerReleaseRefresh(token: String?,body: EmployerReleaseRefreshParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerReleaseRefresh(token,body)
        }
    }

    val employerReleaseOffShelfData = dataSource.employerReleaseOffShelfData
    fun employerReleaseOffShelf(token: String?,body: EmployerReleaseOffShelfParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerReleaseOffShelf(token,body)
        }
    }

    val employerReleaseNewReleaseData = dataSource.employerReleaseNewReleaseData
    fun employerReleaseNewRelease(token: String?,body: EmployerReleaseNewReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerReleaseNewRelease(token,body)
        }
    }

    val employerReleaseDeleteData = dataSource.employerReleaseDeleteData
    fun employerReleaseDelete(token: String?,body: EmployerReleaseDeleteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerReleaseDelete(token,body)
        }
    }

    val employerReleaseDetailData = dataSource.employerReleaseDetailData
    fun fetchEmployerReleaseDetail(token: String?,releaseId : String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerReleaseDetail(token,releaseId)
        }
    }

    val updateEmployerDraftsData = dataSource.updateEmployerDraftsData
    fun updateEmployerDrafts(token: String?,body: SaveEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateEmployerDrafts(token,body)
        }
    }

    val updateEmployerReleaseData = dataSource.updateEmployerReleaseData
    fun updateEmployerRelease(token: String?,body: SaveEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateEmployerRelease(token,body)
        }
    }

    val inviteTalentEmployerReleaseData = dataSource.inviteTalentEmployerReleaseData
    fun fetchInviteTalentEmployerRelease(token: String?,body: InviteTalentEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchInviteTalentEmployerRelease(token,body)
        }
    }
}