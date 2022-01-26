package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.DisputeHistoryInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DisputeHistoryReq
 * Author: Victor
 * Date: 2022/1/13 14:18
 * Description: 
 * -----------------------------------------------------------------
 */

class DisputeHistoryReq: BaseReq() {
    var data: List<DisputeHistoryInfo>? = null
}