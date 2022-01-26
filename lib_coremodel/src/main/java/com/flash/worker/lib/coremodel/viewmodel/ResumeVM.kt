package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.worker.lib.coremodel.data.bean.ResumeBaseInfo
import com.flash.worker.lib.coremodel.data.parm.SaveResumeParm
import com.flash.worker.lib.coremodel.data.parm.UpdateJobResumeParm
import com.flash.worker.lib.coremodel.http.interfaces.IResumeDS
import kotlinx.coroutines.launch


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeVM
 * Author: Victor
 * Date: 2020/12/19 10:04
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeVM(private val dataSource: IResumeDS):ViewModel() {
    val saveResumeData = dataSource.saveResumeData

    fun saveResume(token: String?,body: SaveResumeParm) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveResume(token,body)
        }
    }

    val userResumesData = dataSource.userResumesData

    fun fetchUserResumes(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchUserResumes(token)
        }
    }

    val resumeDetailData = dataSource.resumeDetailData

    fun fetchResumeDetail(token: String?,resumeId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchResumeDetail(token,resumeId)
        }
    }

    val updateBaseResumeData = dataSource.updateBaseResumeData

    fun updateBaseResume(token: String?,body: ResumeBaseInfo?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateBaseResume(token,body)
        }
    }

    val updateJobResumeData = dataSource.updateJobResumeData

    fun updateJobResume(token: String?,body: UpdateJobResumeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateJobResume(token,body)
        }
    }

    val deleteResumeData = dataSource.deleteResumeData

    fun deleteResume(token: String?,resumeId: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.deleteResume(token,resumeId)
        }
    }

}