package com.flash.worker.lib.coremodel.data.parm

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettlementConfirmDetailParm
 * Author: Victor
 * Date: 2021/12/13 14:51
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettlementConfirmDetailParm: BaseParm(), Serializable {
    var employerReleaseId: String? = null//雇主发布ID
    var settlementOrderIds: List<String>? = null//结算工单ID
}