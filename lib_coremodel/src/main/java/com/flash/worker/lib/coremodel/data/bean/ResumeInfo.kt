package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeInfo
 * Author: Victor
 * Date: 2020/12/19 10:34
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeInfo: Serializable {
    var highestEducation: String? = null//最高学历
    var selfDescription: String? = null//自我描述
    var id: String? = null//简历id
    var name: String? = null//简历名称
    var identity: Int = 0
}