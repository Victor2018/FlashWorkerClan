package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.ListData
import com.flash.worker.lib.coremodel.data.bean.SearchTaskInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTaskReq
 * Author: Victor
 * Date: 2021/12/7 11:50
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTaskReq: BaseReq() {
    var data: ListData<SearchTaskInfo>? = null
}