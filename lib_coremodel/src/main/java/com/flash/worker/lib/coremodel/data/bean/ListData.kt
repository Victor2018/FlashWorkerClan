package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ListData
 * Author: Victor
 * Date: 2021/12/21 17:16
 * Description: 
 * -----------------------------------------------------------------
 */
open class ListData<T> {
    var total: Int = 0//总记录数
    var pageNum: Int = 0//当前页号
    var pageSize: Int = 0//每页的数量
    var size: Int = 0//当前页的记录数量
    var pages: Int = 0//总页数
    var list: List<T>? = null
}