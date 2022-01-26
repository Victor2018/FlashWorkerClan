package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.req.BaseReq
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FrozenFlowParm
 * Author: Victor
 * Date: 2021/1/29 15:51
 * Description: 
 * -----------------------------------------------------------------
 */
class FrozenFlowParm: BaseReq() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var yearMonth: String? = null//交易日期
}