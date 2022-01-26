package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerParm
import com.flash.worker.lib.coremodel.data.parm.UpdateEmployerParm
import com.flash.worker.lib.coremodel.data.req.EditEmployerDetailReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployersReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IEmployerDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IEmployerDS {

    val saveEmployerData: LiveData<HttpResult<BaseReq>>
    suspend fun saveEmployer(token: String?,body: SaveEmployerParm?)

    val employersData: LiveData<HttpResult<EmployersReq>>
    suspend fun getEmployers(token: String?)

    val editEmployerDetailData: LiveData<HttpResult<EditEmployerDetailReq>>
    suspend fun getEditEmployerDetail(token: String?,employerId: String?)

    val updateEmployerData: LiveData<HttpResult<BaseReq>>
    suspend fun updateEmployer(token: String?,body: UpdateEmployerParm?)

    val deleteEmployerData: LiveData<HttpResult<BaseReq>>
    suspend fun deleteEmployer(token: String?,employerId: String?)

    val inviteTalentEmployerData: LiveData<HttpResult<EmployersReq>>
    suspend fun fetchInviteTalentEmployer(token: String?)

}