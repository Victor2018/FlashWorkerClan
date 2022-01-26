
package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.EmployerJobInviteInfo
import com.flash.worker.lib.coremodel.data.bean.ListData


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
class EmployerJobInviteReq: BaseReq() {
    var data: ListData<EmployerJobInviteInfo>? = null
}