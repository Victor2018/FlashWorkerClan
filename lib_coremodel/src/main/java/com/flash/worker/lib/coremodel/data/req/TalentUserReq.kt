package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentUserReq
 * Author: Victor
 * Date: 2021/3/18 16:32
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentUserReq: BaseReq() {
    var data: ListData<TalentUserInfo>? = null
}