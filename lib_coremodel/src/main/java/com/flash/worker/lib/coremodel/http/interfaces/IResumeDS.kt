package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.SaveResumeParm
import com.flash.worker.lib.coremodel.data.parm.UpdateJobResumeParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.ResumeDetailReq
import com.flash.worker.lib.coremodel.data.req.UserResumeReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IResumeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IResumeDS {
    val saveResumeData: LiveData<HttpResult<BaseReq>>
    suspend fun saveResume(token: String?,body: SaveResumeParm)

    val userResumesData: LiveData<HttpResult<UserResumeReq>>
    suspend fun fetchUserResumes(token: String?)

    val resumeDetailData: LiveData<HttpResult<ResumeDetailReq>>
    suspend fun fetchResumeDetail(token: String?,resumeId: String?)

    val updateBaseResumeData: LiveData<HttpResult<BaseReq>>
    suspend fun updateBaseResume(token: String?,body: ResumeBaseInfo?)

    val updateJobResumeData: LiveData<HttpResult<BaseReq>>
    suspend fun updateJobResume(token: String?,body: UpdateJobResumeParm?)

    val deleteResumeData: LiveData<HttpResult<BaseReq>>
    suspend fun deleteResume(token: String?,resumeId: String?)

}