package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TalentJobInviteInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentJobInviteReq
 * Author: Victor
 * Date: 2021/4/21 9:44
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentJobInviteReq: BaseReq() {
    var data: ListData<TalentJobInviteInfo>? = null
}