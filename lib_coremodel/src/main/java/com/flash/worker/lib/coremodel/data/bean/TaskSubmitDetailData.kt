package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskSubmitDetailData
 * Author: Victor
 * Date: 2021/12/13 14:43
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskSubmitDetailData: Serializable {
    var userId: String? = null//用户ID
    var username: String? = null//闪工名
    var submitTime: String? = null//提交时间
    var content: String? = null//提交内容
    var pics: List<String>? = null
}