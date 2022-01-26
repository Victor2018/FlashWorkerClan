package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.worker.lib.coremodel.data.parm.ReportEmployerViolationParm
import com.flash.worker.lib.coremodel.data.parm.ReportTalentViolationParm
import com.flash.worker.lib.coremodel.http.interfaces.IViolationReportDS
import kotlinx.coroutines.launch


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationReportVM
 * Author: Victor
 * Date: 2021/7/15 17:47
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationReportVM(private val dataSource: IViolationReportDS): ViewModel() {

    val reportTalentViolationData = dataSource.reportTalentViolationData
    fun reportTalentViolation(token: String?,body: ReportTalentViolationParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.reportTalentViolation(token,body)
        }
    }

    val reportEmployerViolationData = dataSource.reportEmployerViolationData
    fun reportEmployerViolation(token: String?,body: ReportEmployerViolationParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.reportEmployerViolation(token,body)
        }
    }

    val violationUserData = dataSource.violationUserData
    fun fetchViolationUser(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchViolationUser(token)
        }
    }

}