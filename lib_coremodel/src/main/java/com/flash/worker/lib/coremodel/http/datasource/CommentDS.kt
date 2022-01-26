package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ICommentDS
import com.flash.worker.lib.coremodel.http.service.CommentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommentDS
 * Author: Victor
 * Date: 2021/4/13 10:26
 * Description: 
 * -----------------------------------------------------------------
 */
class CommentDS: AbsDS(),ICommentDS {

    override val commentTalentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun commentTalent(token: String?,body: CommentTalentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            commentTalentData.value = commentTalentReq(token,body)
        }
    }

    override val commentAllTalentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun commentAllTalent(token: String?,body: CommentAllTalentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            commentAllTalentData.value = commentAllTalentReq(token,body)
        }
    }

    override val commentEmployerData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun commentEmployer(token: String?,body: CommentEmployerParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            commentEmployerData.value = commentEmployerReq(token,body)
        }
    }

    override val talentCommentCenterData = MutableLiveData<HttpResult<TalentCommentCenterReq>>()
    override suspend fun fetchTalentCommentCenter(token: String?,body: TalentCommentCenterParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentCommentCenterData.value = talentCommentCenterReq(token,body)
        }
    }

    override val employerCommentCenterData = MutableLiveData<HttpResult<EmployerCommentCenterReq>>()
    override suspend fun fetchEmployerCommentCenter(token: String?,body: EmployerCommentCenterParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerCommentCenterData.value = employerCommentCenterReq(token,body)
        }
    }

    override val talentDeleteCommentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun talentDeleteComment(token: String?,body: TalentDeleteCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentDeleteCommentData.value = talentDeleteCommentReq(token,body)
        }
    }

    override val employerDeleteCommentData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun employerDeleteComment(token: String?,body: EmployerDeleteCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerDeleteCommentData.value = employerDeleteCommentReq(token,body)
        }
    }

    override val talentLastCommentData = MutableLiveData<HttpResult<TalentLastCommentReq>>()
    override suspend fun fetchTalentLastComment(token: String?,body: TalentLastCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentLastCommentData.value = talentLastCommentReq(token,body)
        }
    }

    override val talentCommentStatisticsData = MutableLiveData<HttpResult<TalentCommentStatisticsReq>>()
    override suspend fun fetchTalentCommentStatistics(token: String?,body: TalentCommentStatisticsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentCommentStatisticsData.value = talentCommentStatisticsReq(token,body)
        }
    }

    override val talentCommentData = MutableLiveData<HttpResult<TalentCommentReq>>()
    override suspend fun fetchTalentComment(token: String?,body: TalentCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            talentCommentData.value = fetchTalentCommentReq(token,body)
        }
    }

    override val employerLastCommentData = MutableLiveData<HttpResult<EmployerLastCommentReq>>()
    override suspend fun fetchEmployerLastComment(token: String?,body: EmployerLastCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerLastCommentData.value = employerLastCommentReq(token,body)
        }
    }

    override val employerCommentStatisticsData = MutableLiveData<HttpResult<EmployerCommentStatisticsReq>>()
    override suspend fun fetchEmployerCommentStatistics(token: String?,body: EmployerCommentStatisticsParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerCommentStatisticsData.value = employerCommentStatisticsReq(token,body)
        }
    }

    override val employerCommentData = MutableLiveData<HttpResult<EmployerCommentReq>>()
    override suspend fun fetchEmployerComment(token: String?,body: EmployerCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerCommentData.value = employerCommentReq(token,body)
        }
    }

    override val employerUserCommentData = MutableLiveData<HttpResult<EmployerUserCommentReq>>()
    override suspend fun fetchEmployerUserComment(token: String?,body: EmployerUserCommentParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            employerUserCommentData.value = employerUserCommentReq(token,body)
        }
    }

    private suspend fun <T> commentTalentReq(
            token: String?,
            body: CommentTalentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .commentTalent(token,body)) as T
    }

    private suspend fun <T> commentAllTalentReq(
            token: String?,
            body: CommentAllTalentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .commentAllTalent(token,body)) as T
    }

    private suspend fun <T> commentEmployerReq(
            token: String?,
            body: CommentEmployerParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .commentEmployer(token,body)) as T
    }

    private suspend fun <T> talentCommentCenterReq(
            token: String?,
            body: TalentCommentCenterParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchTalentCommentCenter(token,body)) as T
    }

    private suspend fun <T> employerCommentCenterReq(
            token: String?,
            body: EmployerCommentCenterParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchEmployerCommentCenter(token,body)) as T
    }

    private suspend fun <T> talentDeleteCommentReq(
            token: String?,
            body: TalentDeleteCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .talentDeleteComment(token,body)) as T
    }

    private suspend fun <T> employerDeleteCommentReq(
            token: String?,
            body: EmployerDeleteCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .employerDeleteComment(token,body)) as T
    }

    private suspend fun <T> talentLastCommentReq(
            token: String?,
            body: TalentLastCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchTalentLastComment(token,body)) as T
    }

    private suspend fun <T> talentCommentStatisticsReq(
            token: String?,
            body: TalentCommentStatisticsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchTalentCommentStatistics(token,body)) as T
    }

    private suspend fun <T> fetchTalentCommentReq(
            token: String?,
            body: TalentCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchTalentComment(token,body)) as T
    }

    private suspend fun <T> employerLastCommentReq(
            token: String?,
            body: EmployerLastCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchEmployerLastComment(token,body)) as T
    }

    private suspend fun <T> employerCommentStatisticsReq(
            token: String?,
            body: EmployerCommentStatisticsParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchEmployerCommentStatistics(token,body)) as T
    }

    private suspend fun <T> employerCommentReq(
            token: String?,
            body: EmployerCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchEmployerComment(token,body)) as T
    }

    private suspend fun <T> employerUserCommentReq(
            token: String?,
            body: EmployerUserCommentParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(CommentApiService::class.java)
                .fetchEmployerUserComment(token,body)) as T
    }

}