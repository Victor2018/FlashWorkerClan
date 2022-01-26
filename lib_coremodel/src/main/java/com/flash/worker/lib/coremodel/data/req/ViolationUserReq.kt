package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ViolationUserInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationUserReq
 * Author: Victor
 * Date: 2021/7/15 17:37
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationUserReq: BaseReq() {
    var data: List<ViolationUserInfo>? = null
}