package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ICommentDS
 * Author: Victor
 * Date: 2021/4/13 10:26
 * Description: 
 * -----------------------------------------------------------------
 */
interface ICommentDS {
    val commentTalentData: LiveData<HttpResult<BaseReq>>
    suspend fun commentTalent(token: String?,body: CommentTalentParm?)

    val commentAllTalentData: LiveData<HttpResult<BaseReq>>
    suspend fun commentAllTalent(token: String?,body: CommentAllTalentParm?)

    val commentEmployerData: LiveData<HttpResult<BaseReq>>
    suspend fun commentEmployer(token: String?,body: CommentEmployerParm?)

    val talentCommentCenterData: LiveData<HttpResult<TalentCommentCenterReq>>
    suspend fun fetchTalentCommentCenter(token: String?,body: TalentCommentCenterParm?)

    val employerCommentCenterData: LiveData<HttpResult<EmployerCommentCenterReq>>
    suspend fun fetchEmployerCommentCenter(token: String?,body: EmployerCommentCenterParm?)

    val talentDeleteCommentData: LiveData<HttpResult<BaseReq>>
    suspend fun talentDeleteComment(token: String?,body: TalentDeleteCommentParm?)

    val employerDeleteCommentData: LiveData<HttpResult<BaseReq>>
    suspend fun employerDeleteComment(token: String?,body: EmployerDeleteCommentParm?)

    val talentLastCommentData: LiveData<HttpResult<TalentLastCommentReq>>
    suspend fun fetchTalentLastComment(token: String?,body: TalentLastCommentParm?)

    val talentCommentStatisticsData: LiveData<HttpResult<TalentCommentStatisticsReq>>
    suspend fun fetchTalentCommentStatistics(token: String?,body: TalentCommentStatisticsParm?)

    val talentCommentData: LiveData<HttpResult<TalentCommentReq>>
    suspend fun fetchTalentComment(token: String?,body: TalentCommentParm?)

    val employerLastCommentData: LiveData<HttpResult<EmployerLastCommentReq>>
    suspend fun fetchEmployerLastComment(token: String?,body: EmployerLastCommentParm?)

    val employerCommentStatisticsData: LiveData<HttpResult<EmployerCommentStatisticsReq>>
    suspend fun fetchEmployerCommentStatistics(token: String?,body: EmployerCommentStatisticsParm?)

    val employerCommentData: LiveData<HttpResult<EmployerCommentReq>>
    suspend fun fetchEmployerComment(token: String?,body: EmployerCommentParm?)

    val employerUserCommentData: LiveData<HttpResult<EmployerUserCommentReq>>
    suspend fun fetchEmployerUserComment(token: String?,body: EmployerUserCommentParm?)

}