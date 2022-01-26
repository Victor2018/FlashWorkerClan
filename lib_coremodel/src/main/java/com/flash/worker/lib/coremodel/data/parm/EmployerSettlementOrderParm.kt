package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettlementOrderParm
 * Author: Victor
 * Date: 2021/3/22 18:04
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerSettlementOrderParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var arrivedStatus: Int = 0//到岗状态: 0-全部；1-只查询已到岗
    var finishedStatus: Int = 0//完工状态: 0-全部；1-只查询已完工
    var type: Int = 0//业务分类：10-待预付；20-待结算；30-已结算
    var settlementStartTime: String? = null//结算开始时间 示例格式:2021.01.18
    var employerReleaseId: String? = null//雇主发布ID
}