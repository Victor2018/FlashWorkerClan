package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementConfirmDetailParm
 * Author: Victor
 * Date: 2021/3/26 10:12
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementConfirmDetailParm: BaseParm(),Serializable {
    var employerReleaseId: String? = null//雇主发布ID
    var settlementOrderIds: List<String>? = null//结算工单ID列表
}