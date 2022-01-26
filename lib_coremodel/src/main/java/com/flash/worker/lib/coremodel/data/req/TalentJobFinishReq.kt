package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentJobFinishInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobFinishReq
 * Author: Victor
 * Date: 2021/2/8 16:46
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobFinishReq: BaseReq() {
    var data: ListData<TalentJobFinishInfo>? = null
}