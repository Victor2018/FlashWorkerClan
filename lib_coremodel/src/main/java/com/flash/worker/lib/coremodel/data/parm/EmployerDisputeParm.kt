package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerDisputeParm
 * Author: Victor
 * Date: 2021/4/27 19:37
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerDisputeParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
}