package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SetTradePasswordParm
 * Author: Victor
 * Date: 2021/2/1 15:27
 * Description: 
 * -----------------------------------------------------------------
 */
class SetTradePasswordParm: BaseParm() {
    var tradePassword: String? = null//交易密码 MD5值
}