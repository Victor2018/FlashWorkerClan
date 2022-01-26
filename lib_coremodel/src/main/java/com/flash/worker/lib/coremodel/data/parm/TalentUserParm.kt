package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentUserParm
 * Author: Victor
 * Date: 2021/3/18 16:30
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentUserParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var status: Int = 0//业务状态：1-已报名；2-已取消；3-已雇用
    var employerReleaseId:String? = null//雇主发布ID
}