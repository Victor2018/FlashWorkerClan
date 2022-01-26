package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentDisputeInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeReq
 * Author: Victor
 * Date: 2021/4/27 19:38
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDisputeReq: BaseReq() {
    var data: ListData<TalentDisputeInfo>? = null
}