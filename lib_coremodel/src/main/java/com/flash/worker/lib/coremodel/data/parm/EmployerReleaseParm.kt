package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerReleaseParm
 * Author: Victor
 * Date: 2020/12/24 18:14
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerReleaseParm: BaseParm() {
    var pageNum: Int = 0//页编号
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var status: Int = 0//发布状态: 1-已保存；2-已发布；3-已下架；4-已驳回
}