package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.MonthIncomeStatisticsInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MonthIncomeStatisticsReq
 * Author: Victor
 * Date: 2021/5/24 11:45
 * Description: 
 * -----------------------------------------------------------------
 */
class MonthIncomeStatisticsReq: BaseReq() {
    var data: ListData<MonthIncomeStatisticsInfo>? = null
}