package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.SaveResumeParm
import com.flash.worker.lib.coremodel.data.parm.UpdateJobResumeParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ResumeDetailReq
import com.flash.worker.lib.coremodel.data.req.UserResumeReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IResumeDS
import com.flash.worker.lib.coremodel.http.service.ResumeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeDS
 * Author: Victor
 * Date: 2020/12/19 10:00
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeDS: AbsDS(),IResumeDS {

    override val saveResumeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun saveResume(token: String?, body: SaveResumeParm) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveResumeData.value = saveResumeReq(token,body)
        }
    }

    override val userResumesData = MutableLiveData<HttpResult<UserResumeReq>>()
    override suspend fun fetchUserResumes(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            userResumesData.value = fetchUserResumesReq(token)
        }
    }

    override val resumeDetailData = MutableLiveData<HttpResult<ResumeDetailReq>>()
    override suspend fun fetchResumeDetail(token: String?,resumeId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            resumeDetailData.value = fetchResumeDetailReq(token,resumeId)
        }
    }

    override val updateBaseResumeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateBaseResume(token: String?,body: ResumeBaseInfo?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateBaseResumeData.value = updateBaseResumeReq(token,body)
        }
    }

    override val updateJobResumeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateJobResume(token: String?,body: UpdateJobResumeParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateJobResumeData.value = updateJobResumeReq(token,body)
        }
    }

    override val deleteResumeData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun deleteResume(token: String?,resumeId: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            deleteResumeData.value = deleteResumeReq(token,resumeId)
        }
    }


    private suspend fun <T> saveResumeReq(
            token: String?,
            body: SaveResumeParm): T = withContext(Dispatchers.IO) {
                handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                                .saveResume(token,body)) as T
    }

    private suspend fun <T> fetchUserResumesReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                        .getUserResumes(token)) as T
    }

    private suspend fun <T> fetchResumeDetailReq(token: String?,resumeId: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                        .getResumeDetail(token,resumeId)) as T
    }

    private suspend fun <T> updateBaseResumeReq(
            token: String?,
            body: ResumeBaseInfo?): T = withContext(Dispatchers.IO) {
                handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                                .updateBaseResume(token,body)) as T
    }

    private suspend fun <T> updateJobResumeReq(
            token: String?,
            body: UpdateJobResumeParm?): T = withContext(Dispatchers.IO) {
                handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                                .updateJobResume(token,body)) as T
    }

    private suspend fun <T> deleteResumeReq(
            token: String?,
            resumeId: String?): T = withContext(Dispatchers.IO) {
                handleRespone(ApiClient.getApiService(ResumeApiService::class.java)
                                .deleteResume(token,resumeId)) as T
    }

}