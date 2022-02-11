package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.ITalentReleaseDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseVM(private val dataSource: ITalentReleaseDS): ViewModel() {

    val saveTalentDraftsData = dataSource.saveTalentDraftsData
    fun saveTalentDrafts(token: String?,body: SaveTalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveTalentDrafts(token,body)
        }
    }

    val saveTalentReleaseData = dataSource.saveTalentReleaseData
    fun saveTalentRelease(token: String?,body: SaveTalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveTalentRelease(token,body)
        }
    }

    val updateTalentDraftsData = dataSource.updateTalentDraftsData

    fun updateTalentDrafts(token: String?,body: UpdateTalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateTalentDrafts(token,body)
        }
    }

    val updateTalentReleaseData = dataSource.updateTalentReleaseData

    fun updateTalentRelease(token: String?,body: UpdateTalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateTalentRelease(token,body)
        }
    }

    val talentReleaseData = dataSource.talentReleaseData

    fun fetchTalentJobRelease(token: String?,body: TalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentRelease(token,body)
        }
    }

    val talentReleaseRefreshData = dataSource.talentReleaseRefreshData

    fun talentJobReleaseRefresh(token: String?,body: TalentReleaseRefreshParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentReleaseRefresh(token,body)
        }
    }

    val talentReleaseOffShelfData = dataSource.talentReleaseOffShelfData

    fun talentJobReleaseOffShelf(token: String?,body: TalentReleaseOffShelfParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentReleaseOffShelf(token,body)
        }
    }

    val talentReleaseNewReleaseData = dataSource.talentReleaseNewReleaseData

    fun talentJobReleaseNewRelease(token: String?,body: TalentReleaseNewReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentReleaseNewRelease(token,body)
        }
    }

    val talentReleaseDeleteData = dataSource.talentReleaseDeleteData

    fun talentJobReleaseDelete(token: String?,body: TalentReleaseDeleteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentReleaseDelete(token,body)
        }
    }

    val talentReleaseDetailData = dataSource.talentReleaseDetailData

    fun fetchTalentReleaseDetail(token: String?,releaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentReleaseDetail(token,releaseId)
        }
    }

}