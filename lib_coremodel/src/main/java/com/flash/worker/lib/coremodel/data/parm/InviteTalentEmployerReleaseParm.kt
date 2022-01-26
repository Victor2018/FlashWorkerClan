package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InviteTalentEmployerReleaseParm
 * Author: Victor
 * Date: 2021/4/20 17:30
 * Description: 
 * -----------------------------------------------------------------
 */
class InviteTalentEmployerReleaseParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var employerId: String? = null//雇主ID
}