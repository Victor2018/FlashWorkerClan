package com.flash.worker.lib.coremodel.data.parm

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReceiveTaskDetailParm
 * Author: Victor
 * Date: 2021/12/8 15:37
 * Description: 
 * -----------------------------------------------------------------
 */
class ReceiveTaskDetailParm: BaseParm(), Serializable {
    var employerReleaseId: String? = null//雇主发布ID
    var taskReceiveQty: Int = 0//领取数量
}