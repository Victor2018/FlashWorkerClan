package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CancelAccountParm
 * Author: Victor
 * Date: 2021/6/3 9:42
 * Description: 
 * -----------------------------------------------------------------
 */
class CancelAccountParm: BaseParm() {
    var tradePassword: String? = null//交易密码
    var code: String? = null//验证码
}