package com.flash.worker.lib.coremodel.http.interfaces

import androidx.lifecycle.LiveData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ISystemNoticeDS
 * Author: Victor
 * Date: 2020/8/5 上午 11:47
 * Description: 
 * -----------------------------------------------------------------
 */
interface ISystemNoticeDS {

    val unReadNumData: LiveData<HttpResult<SystemNoticeUnreadReq>>
    suspend fun fetchUnreadNum(token: String?)

    val markReadData: LiveData<HttpResult<BaseReq>>
    suspend fun markRead(token: String?)

    val noticeData: LiveData<HttpResult<SystemNoticeReq>>
    suspend fun fetchNotice(token: String?,body: SystemNoticeParm?)


}