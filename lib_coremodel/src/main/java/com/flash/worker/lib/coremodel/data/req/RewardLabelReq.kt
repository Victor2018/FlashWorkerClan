package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import com.flash.worker.lib.coremodel.data.req.BaseReq


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardLabelReq
 * Author: Victor
 * Date: 2021/3/24 11:28
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardLabelReq: BaseReq() {
    var data: List<RewardLabelInfo>? = null
}