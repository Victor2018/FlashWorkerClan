package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.GuildMemberInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildMemberReq
 * Author: Victor
 * Date: 2021/5/20 12:09
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildMemberReq: BaseReq() {
    var data: ListData<GuildMemberInfo>? = null
}