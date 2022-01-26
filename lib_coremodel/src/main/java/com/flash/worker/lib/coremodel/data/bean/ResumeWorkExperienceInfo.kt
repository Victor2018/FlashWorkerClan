package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResumeWorkExperienceInfo
 * Author: Victor
 * Date: 2020/12/19 10:35
 * Description: 
 * -----------------------------------------------------------------
 */
class ResumeWorkExperienceInfo: Serializable {
    var companyName: String? = null//公司名称
    var positionName: String? = null//职位名称
    var startTime: String? = null//开始时间
    var endTime: String? = null//结束时间

    var index: Int = 0
}