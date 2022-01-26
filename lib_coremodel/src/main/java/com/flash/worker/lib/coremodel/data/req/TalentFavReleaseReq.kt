package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentFavReleaseInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavReleaseReq
 * Author: Victor
 * Date: 2021/4/23 17:02
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavReleaseReq: BaseReq() {
    var data: ListData<TalentFavReleaseInfo>? = null
}