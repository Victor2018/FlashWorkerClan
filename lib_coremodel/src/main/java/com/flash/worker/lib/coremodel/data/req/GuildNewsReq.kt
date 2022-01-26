package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.GuildNewsInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildNewsReq
 * Author: Victor
 * Date: 2021/5/19 17:27
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildNewsReq: BaseReq() {
    var data: ListData<GuildNewsInfo>? = null
}