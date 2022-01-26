package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo
import com.flash.worker.lib.coremodel.data.req.BaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UserResumeReq
 * Author: Victor
 * Date: 2020/12/21 11:36
 * Description: 
 * -----------------------------------------------------------------
 */
class UserResumeReq: BaseReq() {
    var data :List<MyResumeInfo>? = null
}