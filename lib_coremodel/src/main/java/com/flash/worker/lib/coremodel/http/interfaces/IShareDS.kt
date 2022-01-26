package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ShareGuidInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareTaskInfoParm
import com.flash.worker.lib.coremodel.data.req.ShareInfoReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IShareDs
 * Author: Victor
 * Date: 2021/8/19 17:14
 * Description: 
 * -----------------------------------------------------------------
 */
interface IShareDS {
    val shareInfoData: LiveData<HttpResult<ShareInfoReq>>
    suspend fun fetchShareInfo(token: String?,body: ShareInfoParm?)

    val shareGuildInfoData: LiveData<HttpResult<ShareInfoReq>>
    suspend fun fetchShareGuildInfo(token: String?,body: ShareGuidInfoParm?)

    val shareTaskInfoData: LiveData<HttpResult<ShareInfoReq>>
    suspend fun fetchShareTaskInfo(token: String?,body: ShareTaskInfoParm?)
}