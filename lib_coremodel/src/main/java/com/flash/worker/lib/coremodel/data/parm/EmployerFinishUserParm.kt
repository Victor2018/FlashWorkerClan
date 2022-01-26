package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFinishUserParm
 * Author: Victor
 * Date: 2021/4/7 14:25
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFinishUserParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var employerReleaseId: String? = null//雇主发布ID
}