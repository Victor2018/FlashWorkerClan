package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ISystemNoticeDS
import com.flash.worker.lib.coremodel.http.service.SystemNoticeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:48
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeDS: AbsDS(), ISystemNoticeDS {

    override val unReadNumData = MutableLiveData<HttpResult<SystemNoticeUnreadReq>>()
    override suspend fun fetchUnreadNum(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            unReadNumData.value = unReadNumReq(token)
        }
    }

    override val markReadData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun markRead(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            markReadData.value = markReadReq(token)
        }
    }

    override val noticeData = MutableLiveData<HttpResult<SystemNoticeReq>>()
    override suspend fun fetchNotice(token: String?,body: SystemNoticeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            noticeData.value = noticeReq(token,body)
        }
    }


    private suspend fun <T> unReadNumReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(SystemNoticeApiService::class.java)
                        .fetchUnreadNum(token)) as T
    }

    private suspend fun <T> markReadReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(SystemNoticeApiService::class.java)
                        .markRead(token)) as T
    }

    private suspend fun <T> noticeReq(token: String?,body: SystemNoticeParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(SystemNoticeApiService::class.java)
                        .fetchNotice(token,body)) as T
    }


}