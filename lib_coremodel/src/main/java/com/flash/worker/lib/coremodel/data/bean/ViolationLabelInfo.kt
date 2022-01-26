package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViolationLabelInfo
 * Author: Victor
 * Date: 2021/7/15 17:19
 * Description: 
 * -----------------------------------------------------------------
 */
class ViolationLabelInfo: Serializable {
    var id: String? = null//ID
    var name: String? = null//名称
    var code: String? = null//编码
    var value: String? = null//值

    var isReportTalentViolation: Boolean = false
}