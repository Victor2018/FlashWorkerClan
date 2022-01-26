package com.flash.worker.lib.coremodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.SaveTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.http.interfaces.ITaskDS
import kotlinx.coroutines.launch


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskVM
 * Author: Victor
 * Date: 2021/12/6 15:31
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskVM(private val dataSource: ITaskDS): ViewModel() {
    val saveTaskData = dataSource.saveTaskData
    fun saveTask(token: String?,body: SaveTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.saveTask(token,body)
        }
    }

    val releaseTaskData = dataSource.releaseTaskData
    fun releaseTask(token: String?,body: ReleaseTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.releaseTask(token,body)
        }
    }

    val taskPrepaidDetailData = dataSource.taskPrepaidDetailData
    fun fetchTaskPrepaidDetail(token: String?,body: TaskPrepaidDetailParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.fetchTaskPrepaidDetail(token,body)
        }
    }

    val updateSaveTaskData = dataSource.updateSaveTaskData
    fun updateSaveTask(token: String?,body: SaveTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateSaveTask(token,body)
        }
    }

    val updateReleaseTaskData = dataSource.updateReleaseTaskData
    fun updateReleaseTask(token: String?,body: ReleaseTaskParm?) {
        // Launch a coroutine that reads from a remote data source and updates cache
        viewModelScope.launch {
            dataSource.updateReleaseTask(token,body)
        }
    }
}