package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SmsLoginParm
 * Author: Victor
 * Date: 2020/12/7 14:33
 * Description: 
 * -----------------------------------------------------------------
 */
class SmsLoginParm: BaseParm() {
    var code: String? = null
    var phone: String? = null
}