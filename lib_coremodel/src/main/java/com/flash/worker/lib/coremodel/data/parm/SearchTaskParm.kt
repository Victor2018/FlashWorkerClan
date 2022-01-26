package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.util.WebConfig


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SearchTaskParm
 * Author: Victor
 * Date: 2021/12/7 11:46
 * Description: 
 * -----------------------------------------------------------------
 */
class SearchTaskParm: BaseParm() {
    var pageSize: Int = WebConfig.PAGE_SIZE//页大小
    var pageNum: Int = 0//页编号
    var keywords: String? = null//关键词
    var sortType: String? = null//排序字段：credit-信用；price-单价
    var sortOrder: String? = null//排序方式：asc-升序；desc-降序
}