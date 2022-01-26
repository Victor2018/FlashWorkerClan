package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.http.interfaces.ICommentDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class CommentVM(private val dataSource: ICommentDS): ViewModel() {

    val commentTalentData = dataSource.commentTalentData
    fun commentTalent(token: String?,body: CommentTalentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.commentTalent(token,body)
        }
    }

    val commentAllTalentData = dataSource.commentAllTalentData
    fun commentAllTalent(token: String?,body: CommentAllTalentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.commentAllTalent(token,body)
        }
    }

    val commentEmployerData = dataSource.commentEmployerData
    fun commentEmployer(token: String?,body: CommentEmployerParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.commentEmployer(token,body)
        }
    }

    val talentCommentCenterData = dataSource.talentCommentCenterData
    fun fetchTalentCommentCenter(token: String?,body: TalentCommentCenterParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentCommentCenter(token,body)
        }
    }

    val employerCommentCenterData = dataSource.employerCommentCenterData
    fun fetchEmployerCommentCenter(token: String?,body: EmployerCommentCenterParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerCommentCenter(token,body)
        }
    }

    val talentDeleteCommentData = dataSource.talentDeleteCommentData
    fun talentDeleteComment(token: String?,body: TalentDeleteCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.talentDeleteComment(token,body)
        }
    }

    val employerDeleteCommentData = dataSource.employerDeleteCommentData
    fun employerDeleteComment(token: String?,body: EmployerDeleteCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.employerDeleteComment(token,body)
        }
    }

    val talentLastCommentData = dataSource.talentLastCommentData
    fun fetchTalentLastComment(token: String?,body: TalentLastCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentLastComment(token,body)
        }
    }

    val talentCommentStatisticsData = dataSource.talentCommentStatisticsData
    fun fetchTalentCommentStatistics(token: String?,body: TalentCommentStatisticsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentCommentStatistics(token,body)
        }
    }

    val talentCommentData = dataSource.talentCommentData
    fun fetchTalentComment(token: String?,body: TalentCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentComment(token,body)
        }
    }

    val employerLastCommentData = dataSource.employerLastCommentData
    fun fetchEmployerLastComment(token: String?,body: EmployerLastCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerLastComment(token,body)
        }
    }

    val employerCommentStatisticsData = dataSource.employerCommentStatisticsData
    fun fetchEmployerCommentStatistics(token: String?,body: EmployerCommentStatisticsParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerCommentStatistics(token,body)
        }
    }

    val employerCommentData = dataSource.employerCommentData
    fun fetchEmployerComment(token: String?,body: EmployerCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerComment(token,body)
        }
    }

    val employerUserCommentData = dataSource.employerUserCommentData
    fun fetchEmployerUserComment(token: String?,body: EmployerUserCommentParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerUserComment(token,body)
        }
    }

}