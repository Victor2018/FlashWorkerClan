package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerCommentParm
 * Author: Victor
 * Date: 2021/4/15 17:36
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerCommentParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var employerId: String? = null//雇主ID
    var label: Int? = 0//评价标签：1-好评；2-中评；3-差评
}