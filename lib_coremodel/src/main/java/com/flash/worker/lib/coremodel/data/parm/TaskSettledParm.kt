package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementParm
 * Author: Victor
 * Date: 2021/12/10 11:39
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettledParm: BaseParm(){
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var employerReleaseId:String? = null//雇主发布ID
    var type: Int = 0//业务分类：10-已领取；20-已提交；30-已结算；
}