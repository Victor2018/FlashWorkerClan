package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentReleaseInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobReleaseReq
 * Author: Victor
 * Date: 2020/12/24 18:17
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseReq: BaseReq() {
    var data: ListData<TalentReleaseInfo>? = null
}