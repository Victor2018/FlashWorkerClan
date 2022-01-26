package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerUnReadStatusData
 * Author: Victor
 * Date: 2021/6/24 16:29
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerUnReadStatusData {
    var waitEmploymentListIsRead: Boolean = false//雇用中列表是否已读
    var waitEmploymentListCount: Int = 0//雇用中列表记录总数
    var employmentListCount: Int = 0//进行中列表记录总数
    var waitCommentListCount: Int = 0//待评价列表记录总数
}