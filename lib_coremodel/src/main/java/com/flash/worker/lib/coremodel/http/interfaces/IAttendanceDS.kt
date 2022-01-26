package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.AttendanceDateReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.EmployerAttendanceReq
import com.flash.worker.lib.coremodel.data.req.TalentAttendanceReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IAttendanceDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface IAttendanceDS {
    val talentAttendanceData: LiveData<HttpResult<TalentAttendanceReq>>
    suspend fun fetchTalentAttendance(token: String?,body: TalentAttendanceParm?)

    val talentOnDutyData: LiveData<HttpResult<BaseReq>>
    suspend fun talentOnDuty(token: String?,body: TalentOndutyParm?)

    val talentOffDutyData: LiveData<HttpResult<BaseReq>>
    suspend fun talentOffDuty(token: String?,body: TalentOffdutyParm?)

    val employerAttendanceData: LiveData<HttpResult<EmployerAttendanceReq>>
    suspend fun fetchEmployerAttendance(token: String?,body: EmployerAttendanceParm?)

    val attendanceDateData: LiveData<HttpResult<AttendanceDateReq>>
    suspend fun fetchAttendanceDate(token: String?,employerReleaseId: String?)

    val employerConfirmAttendanceData: LiveData<HttpResult<BaseReq>>
    suspend fun employerConfirmAttendance(token: String?,body: EmployerConfirmAttendanceParm?)

}