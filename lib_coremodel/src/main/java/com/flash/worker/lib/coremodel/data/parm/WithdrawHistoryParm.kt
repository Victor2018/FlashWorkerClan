package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawHistoryParm
 * Author: Victor
 * Date: 2021/11/3 15:04
 * Description: 
 * -----------------------------------------------------------------
 */
class WithdrawHistoryParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
}