package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentEmployingInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEmployingReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentEmployingReq: BaseReq() {
    var data: ListData<TalentEmployingInfo>? = null
}