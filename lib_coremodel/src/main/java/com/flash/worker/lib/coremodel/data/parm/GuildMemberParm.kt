package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildMemberParm
 * Author: Victor
 * Date: 2021/5/19 17:25
 * Description: 
 * -----------------------------------------------------------------
 */
class GuildMemberParm: BaseParm () {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var guildId: String? = null//公会ID
    var keywords: String? = null//搜索关键字
}