package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchGuildParm
 * Author: Victor
 * Date: 2021/5/18 16:56
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchGuildParm: BaseParm() {
    var pageSize = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var guildCity: String? = null//公会城市
    var keywords: String? = null//关键词（公会名称）
}