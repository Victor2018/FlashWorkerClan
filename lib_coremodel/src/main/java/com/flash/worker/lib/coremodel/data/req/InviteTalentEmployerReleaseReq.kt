package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.InviteTalentEmployerReleaseInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InviteTalentEmployerReleaseReq
 * Author: Victor
 * Date: 2021/4/20 17:32
 * Description: 
 * -----------------------------------------------------------------
 */
class InviteTalentEmployerReleaseReq: BaseReq() {
    var data: ListData<InviteTalentEmployerReleaseInfo>? = null
}