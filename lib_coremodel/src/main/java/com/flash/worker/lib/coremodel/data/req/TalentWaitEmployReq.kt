package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentWaitEmployInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitEmployReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentWaitEmployReq: BaseReq() {
    var data: ListData<TalentWaitEmployInfo>? = null
}