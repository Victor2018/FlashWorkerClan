package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerParm
import com.flash.worker.lib.coremodel.data.parm.UpdateEmployerParm
import com.flash.worker.lib.coremodel.http.interfaces.IEmployerDS
import kotlinx.coroutines.launch


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerVM
 * Author: Victor
 * Date: 2020/12/19 10:04
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerVM(private val dataSource: IEmployerDS):ViewModel() {

    val saveEmployerData = dataSource.saveEmployerData
    fun saveEmployer(token: String?,body: SaveEmployerParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveEmployer(token,body)
        }
    }

    val employersData = dataSource.employersData
    fun getEmployers(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.getEmployers(token)
        }
    }

    val editEmployerDetailData = dataSource.editEmployerDetailData
    fun getEditEmployerDetail(token: String?,employerId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.getEditEmployerDetail(token,employerId)
        }
    }

    val updateEmployerData = dataSource.updateEmployerData
    fun updateEmployer(token: String?,body: UpdateEmployerParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateEmployer(token,body)
        }
    }

    val deleteEmployerData = dataSource.deleteEmployerData
    fun deleteEmployer(token: String?,employerId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.deleteEmployer(token,employerId)
        }
    }

    val inviteTalentEmployerData = dataSource.inviteTalentEmployerData
    fun fetchInviteTalentEmployer(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchInviteTalentEmployer(token)
        }
    }

}