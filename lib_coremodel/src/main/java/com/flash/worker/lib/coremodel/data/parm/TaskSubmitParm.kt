package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSubmitParm
 * Author: Victor
 * Date: 2021/12/11 10:14
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSubmitParm: BaseParm() {
    var settlementOrderId: String? = null//结算工单ID
    var content: String? = null//提交内容
    var pics: List<String>? = null
}