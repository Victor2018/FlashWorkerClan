package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.HotKeywordParm
import com.flash.worker.lib.coremodel.data.parm.SearchEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTaskParm
import com.flash.worker.lib.coremodel.http.interfaces.IHomeDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HomeVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class HomeVM(private val dataSource: IHomeDS): ViewModel() {

    val searchTalentReleaseData = dataSource.searchTalentReleaseData
    fun searchTalentRelease(body: SearchTalentReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.searchTalentRelease(body)
        }
    }

    val searchEmployerReleaseData = dataSource.searchEmployerReleaseData
    fun searchEmployerRelease(body: SearchEmployerReleaseParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.searchEmployerRelease(body)
        }
    }


    val homeTalentDetailData = dataSource.homeTalentDetailData
    fun fetchHomeTalentDetail(token: String?,releaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchHomeTalentDetail(token,releaseId)
        }
    }

    val homeEmployerDetailData = dataSource.homeEmployerDetailData
    fun fetchHomeEmployerDetail(token: String?,releaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchHomeEmployerDetail(token,releaseId)
        }
    }

    val hotKeywordData = dataSource.hotKeywordData
    fun fetchHotKeyword(token: String?,body: HotKeywordParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchHotKeyword(token,body)
        }
    }

    val bannerData = dataSource.bannerData
    fun fetchBanner(bannerType: Int) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchBanner(bannerType)
        }
    }

    val announcementData = dataSource.announcementData
    fun fetchAnnouncement() {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchAnnouncement()
        }
    }

    val searchTaskData = dataSource.searchTaskData
    fun searchTask(token: String?,body: SearchTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.searchTask(token,body)
        }
    }

    val taskDetailData = dataSource.taskDetailData
    fun fetchTaskDetail(token: String?,releaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskDetail(token,releaseId)
        }
    }

}