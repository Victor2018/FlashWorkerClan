package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.GuildApplyRecordInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildApplyRecordReq
 * Author: Victor
 * Date: 2021/5/18 17:01
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildApplyRecordReq: BaseReq() {
    var data: ListData<GuildApplyRecordInfo>? = null
}