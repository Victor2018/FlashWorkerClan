package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.http.interfaces.ICommonDS
import kotlinx.coroutines.launch

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommonVM
 * Author: Victor
 * Date: 2020/8/5 上午 11:49
 * Description: 
 * -----------------------------------------------------------------
 */
class CommonVM(private val dataSource: ICommonDS): ViewModel() {

    val areaTreeData = dataSource.areaTreeData
    fun fetchAreaTreeData(body: AreaTreeParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchAreaTreeData(body)
        }
    }

    val talentTypeData = dataSource.talentTypeData
    fun fetchTalentTypeData() {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentTypeData()
        }
    }

    val cityAreaData = dataSource.cityAreaData
    fun fetchAreaTreeData(token: String?,city: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchCityAreaData(token,city)
        }
    }


    val systemTimeData = dataSource.systemTimeData
    fun fetchSystemTime(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchSystemTime(token)
        }
    }

    val rewardLabelData = dataSource.rewardLabelData
    fun fetchRewardLabel(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchRewardLabel(token)
        }
    }

    val talentViolationLabelData = dataSource.talentViolationLabelData
    fun fetchTalentViolationLabel(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTalentViolationLabel(token)
        }
    }

    val employerViolationLabelData = dataSource.employerViolationLabelData
    fun fetchEmployerViolationLabel(token: String?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchEmployerViolationLabel(token)
        }
    }

}