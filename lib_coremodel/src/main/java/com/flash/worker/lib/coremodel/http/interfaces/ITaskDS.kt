package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.SaveTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.data.req.TaskPrepaidDetailReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ITaskDS
 * Author: Victor
 * Date: 2021/12/6 15:23
 * Description: 
 * -----------------------------------------------------------------
 */
interface ITaskDS {
    val saveTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun saveTask(token: String?,body: SaveTaskParm?)

    val releaseTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun releaseTask(token: String?,body: ReleaseTaskParm?)

    val taskPrepaidDetailData: LiveData<HttpResult<TaskPrepaidDetailReq>>
    suspend fun fetchTaskPrepaidDetail(token: String?,body: TaskPrepaidDetailParm?)

    val updateSaveTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun updateSaveTask(token: String?,body: SaveTaskParm?)

    val updateReleaseTaskData: LiveData<HttpResult<BaseReq>>
    suspend fun updateReleaseTask(token: String?,body: ReleaseTaskParm?)

}