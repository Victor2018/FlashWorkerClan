package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployConfirmDetailParm
 * Author: Victor
 * Date: 2021/3/19 11:31
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployConfirmDetailParm: BaseParm(),Serializable {
    var employerReleaseId: String? = null//雇主发布ID
    var jobOrderIds: List<String>? = null//人才工单ID
}