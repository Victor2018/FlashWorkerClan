package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.HotKeywordParm
import com.flash.worker.lib.coremodel.data.parm.SearchEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.parm.SearchTaskParm
import com.flash.worker.lib.coremodel.data.req.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IHomeDS
 * Author: Victor
 * Date: 2021/4/13 14:14
 * Description: 
 * -----------------------------------------------------------------
 */
interface IHomeDS {
    val searchTalentReleaseData: LiveData<HttpResult<SearchTalentReleaseReq>>
    suspend fun searchTalentRelease(body: SearchTalentReleaseParm?)

    val searchEmployerReleaseData: LiveData<HttpResult<SearchEmployerReleaseReq>>
    suspend fun searchEmployerRelease(body: SearchEmployerReleaseParm?)

    val homeTalentDetailData: LiveData<HttpResult<HomeTalentDetailReq>>
    suspend fun fetchHomeTalentDetail(token: String?,releaseId: String?)

    val homeEmployerDetailData: LiveData<HttpResult<HomeEmployerDetailReq>>
    suspend fun fetchHomeEmployerDetail(token: String?,releaseId: String?)

    val hotKeywordData: LiveData<HttpResult<HotKeywordReq>>
    suspend fun fetchHotKeyword(token: String?,body: HotKeywordParm?)

    val bannerData: LiveData<HttpResult<BannerReq>>
    suspend fun fetchBanner(bannerType: Int)

    val announcementData: LiveData<HttpResult<AnnouncementReq>>
    suspend fun fetchAnnouncement()

    val searchTaskData: LiveData<HttpResult<SearchTaskReq>>
    suspend fun searchTask(token: String?,body: SearchTaskParm?)

    val taskDetailData: LiveData<HttpResult<TaskDetailReq>>
    suspend fun fetchTaskDetail(token: String?,releaseId: String?)

}