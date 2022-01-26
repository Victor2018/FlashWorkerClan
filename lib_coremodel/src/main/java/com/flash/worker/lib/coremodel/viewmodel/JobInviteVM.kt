package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.IAttendanceDS
import com.flash.worker.lib.coremodel.http.interfaces.IJobInviteDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobInviteVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class JobInviteVM(private val dataSource: IJobInviteDS): ViewModel() {

    val talentJobInviteData = dataSource.talentJobInviteData
    fun fetchTalentJobInvite(token: String?,body: TalentJobInviteParm?)  {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentJobInvite(token,body)
        }
    }

    val employerSendInviteData = dataSource.employerSendInviteData
    fun employerSendInvite(token: String?,body: EmployerSendInviteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerSendInvite(token,body)
        }
    }

    val employerJobInviteData = dataSource.employerJobInviteData
    fun fetchEmployerJobInvite(token: String?,body: EmployerJobInviteParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerJobInvite(token,body)
        }
    }

    val inviteNumData = dataSource.inviteNumData
    fun fetchInviteNum(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchInviteNum(token)
        }
    }

}