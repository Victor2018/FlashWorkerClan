package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.TalentTypeInfo
import com.flash.worker.lib.coremodel.data.req.BaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTypeReq
 * Author: Victor
 * Date: 2020/12/22 19:31
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTypeReq: BaseReq() {
    var data: List<TalentTypeInfo> ? = null
}