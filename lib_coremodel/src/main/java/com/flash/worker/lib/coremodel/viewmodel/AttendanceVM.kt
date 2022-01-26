package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.EmployerConfirmAttendanceParm
import com.flash.worker.lib.coremodel.data.parm.EmployerAttendanceParm
import com.flash.worker.lib.coremodel.data.parm.TalentOffdutyParm
import com.flash.worker.lib.coremodel.data.parm.TalentOndutyParm
import com.flash.worker.lib.coremodel.data.parm.TalentAttendanceParm
import com.flash.worker.lib.coremodel.http.interfaces.IAttendanceDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class AttendanceVM(private val dataSource: IAttendanceDS): ViewModel() {

    val talentAttendanceData = dataSource.talentAttendanceData
    fun fetchTalentAttendance(token: String?,body: TalentAttendanceParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentAttendance(token,body)
        }
    }

    val talentOnDutyData = dataSource.talentOnDutyData
    fun talentOnDuty(token: String?,body: TalentOndutyParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentOnDuty(token,body)
        }
    }

    val talentOffDutyData = dataSource.talentOffDutyData
    fun talentOffDuty(token: String?,body: TalentOffdutyParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentOffDuty(token,body)
        }
    }

    val employerAttendanceData = dataSource.employerAttendanceData
    fun fetchEmployerAttendance(token: String?,body: EmployerAttendanceParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerAttendance(token,body)
        }
    }

    val attendanceDateData = dataSource.attendanceDateData
    fun fetchAttendanceDate(token: String?,employerReleaseId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchAttendanceDate(token,employerReleaseId)
        }
    }

    val employerConfirmAttendanceData = dataSource.employerConfirmAttendanceData
    fun employerConfirmAttendance(token: String?,body: EmployerConfirmAttendanceParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerConfirmAttendance(token,body)
        }
    }

}