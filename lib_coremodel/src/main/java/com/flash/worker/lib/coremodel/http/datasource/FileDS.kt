package com.flash.worker.lib.coremodel.http.datasource

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.coremodel.data.req.CredentialReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.http.ApiClient
import com.flash.worker.lib.coremodel.http.interfaces.IFileDS
import com.flash.worker.lib.coremodel.http.service.FileApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FileDS
 * Author: Victor
 * Date: 2021/4/13 12:19
 * Description: 
 * -----------------------------------------------------------------
 */
class FileDS: AbsDS(),IFileDS {

    override val uploadConfigData = MutableLiveData<HttpResult<UploadConfigReq>>()
    override suspend fun fetchUploadConfig(token: String?) {
        // Force Main thread
        withContext(Dispatchers.Main) {
            uploadConfigData.value = uploadConfigReq(token)
        }
    }

    private suspend fun <T> uploadConfigReq(token: String?): T = withContext(Dispatchers.IO) {
        handleRespone(ApiClient.getApiService(FileApiService::class.java)
                .getUploadConfig(token)) as T
    }

}