package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.SearchGuildInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchGuildReq
 * Author: Victor
 * Date: 2021/5/18 16:57
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchGuildReq: BaseReq() {
    var data: ListData<SearchGuildInfo>? = null
}