package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSettledReq
 * Author: Victor
 * Date: 2021/12/10 11:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSettledReq: BaseReq() {
    var data: ListData<TaskSettledInfo>? = null
}