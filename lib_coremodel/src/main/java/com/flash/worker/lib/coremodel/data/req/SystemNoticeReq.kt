package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.SystemNoticeInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SystemNoticeReq
 * Author: Victor
 * Date: 2021/5/11 18:23
 * Description: 
 * -----------------------------------------------------------------
 */
class SystemNoticeReq: BaseReq() {
    var data: ListData<SystemNoticeInfo>? = null
}