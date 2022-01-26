package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ModifyTradePasswordParm
 * Author: Victor
 * Date: 2021/2/10 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class ModifyTradePasswordParm: BaseParm() {
    var oldTradePassword: String? = null//旧交易密码 MD5值
    var newTradePassword: String? = null//新交易密码 MD5值
}