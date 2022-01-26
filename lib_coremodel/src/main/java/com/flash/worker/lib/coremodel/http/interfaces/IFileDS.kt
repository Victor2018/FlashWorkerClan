package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.req.CredentialReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IFileDS
 * Author: Victor
 * Date: 2021/4/13 12:19
 * Description: 
 * -----------------------------------------------------------------
 */
interface IFileDS {
    val uploadConfigData: LiveData<HttpResult<UploadConfigReq>>
    suspend fun fetchUploadConfig(token: String?)
}