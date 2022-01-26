package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.SaveTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.data.req.AccountInfoReq
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TaskPrepaidDetailReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.ITaskDS
import com.flash.worker.lib.coremodel.http.service.AccountApiService
import com.flash.worker.lib.coremodel.http.service.TaskApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskDS
 * Author: Victor
 * Date: 2021/12/6 15:25
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskDS: AbsDS(), ITaskDS {
    override val saveTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun saveTask(token: String?,body: SaveTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            saveTaskData.value = saveTaskReq(token,body)
        }
    }

    override val releaseTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun releaseTask(token: String?,body: ReleaseTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            releaseTaskData.value = releaseTaskReq(token,body)
        }
    }

    override val taskPrepaidDetailData = MutableLiveData<HttpResult<TaskPrepaidDetailReq>>()
    override suspend fun fetchTaskPrepaidDetail(token: String?,body: TaskPrepaidDetailParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            taskPrepaidDetailData.value = taskPrepaidDetailReq(token,body)
        }
    }

    override val updateSaveTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateSaveTask(token: String?,body: SaveTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateSaveTaskData.value = updateSaveTaskReq(token,body)
        }
    }

    override val updateReleaseTaskData = MutableLiveData<HttpResult<BaseReq>>()
    override suspend fun updateReleaseTask(token: String?,body: ReleaseTaskParm?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            updateReleaseTaskData.value = updateReleaseTaskReq(token,body)
        }
    }

    private suspend fun <T> saveTaskReq(
        token: String?,
        body: SaveTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TaskApiService::class.java).saveTask(token,body)) as T
    }

    private suspend fun <T> releaseTaskReq(
        token: String?,
        body: ReleaseTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TaskApiService::class.java).releaseTask(token,body)) as T
    }

    private suspend fun <T> taskPrepaidDetailReq(
        token: String?,
        body: TaskPrepaidDetailParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TaskApiService::class.java).fetchPrepaidDetail(token,body)) as T
    }

    private suspend fun <T> updateSaveTaskReq(
        token: String?,
        body: SaveTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TaskApiService::class.java).updateSaveTask(token,body)) as T
    }

    private suspend fun <T> updateReleaseTaskReq(
        token: String?,
        body: ReleaseTaskParm?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(TaskApiService::class.java).updateReleaseTask(token,body)) as T
    }

}