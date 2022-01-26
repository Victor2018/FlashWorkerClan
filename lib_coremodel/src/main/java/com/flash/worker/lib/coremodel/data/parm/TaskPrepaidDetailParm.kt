package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskPrepaidDetailParm
 * Author: Victor
 * Date: 2021/12/6 15:20
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskPrepaidDetailParm: BaseParm() {
    var title: String? = null//任务名称 12
    var price: Double? = 0.0//报酬单价
    var taskQty: Int = 0//任务数量
}