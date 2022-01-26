package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerJobInviteParm
import com.flash.worker.lib.coremodel.data.parm.EmployerSendInviteParm
import com.flash.worker.lib.coremodel.data.parm.TalentJobInviteParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerJobInviteReq
import com.flash.worker.lib.coremodel.data.req.InviteNumReq
import com.flash.worker.lib.coremodel.data.req.TalentJobInviteReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IJobInviteDS
 * Author: Victor
 * Date: 2021/4/21 10:02
 * Description: 
 * -----------------------------------------------------------------
 */
interface IJobInviteDS {
    val talentJobInviteData: LiveData<HttpResult<TalentJobInviteReq>>
    suspend fun fetchTalentJobInvite(token: String?,body: TalentJobInviteParm?)

    val employerSendInviteData: LiveData<HttpResult<BaseReq>>
    suspend fun employerSendInvite(token: String?,body: EmployerSendInviteParm?)

    val employerJobInviteData: LiveData<HttpResult<EmployerJobInviteReq>>
    suspend fun fetchEmployerJobInvite(token: String?,body: EmployerJobInviteParm?)

    val inviteNumData: LiveData<HttpResult<InviteNumReq>>
    suspend fun fetchInviteNum(token: String?)

}