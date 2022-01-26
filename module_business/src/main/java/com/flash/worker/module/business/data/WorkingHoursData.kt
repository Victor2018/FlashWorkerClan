package com.flash.worker.module.business.data

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkingHoursData
 * Author: Victor
 * Date: 2021/1/19 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkingHoursData: Serializable {
    var startTime: String? = null
    var endTime: String? = null
    var index: Int = 0
}