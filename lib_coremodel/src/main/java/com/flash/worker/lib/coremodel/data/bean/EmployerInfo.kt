package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerInfo
 * Author: Victor
 * Date: 2021/1/18 17:51
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerInfo: Serializable {
    var id: String? = null
    var userId: String? = null
    var name: String? = null
    var identity: Int = 0
    var isLicenceAuth: Boolean = false
}