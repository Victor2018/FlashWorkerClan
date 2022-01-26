package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.SearchTalentReleaseInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTalentReleaseReq
 * Author: Victor
 * Date: 2020/12/30 19:14
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTalentReleaseReq: BaseReq() {
    var data: ListData<SearchTalentReleaseInfo>? = null
}