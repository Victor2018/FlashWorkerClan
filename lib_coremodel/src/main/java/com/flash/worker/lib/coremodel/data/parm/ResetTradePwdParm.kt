package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ResetTradePwdParm
 * Author: Victor
 * Date: 2021/2/1 17:17
 * Description: 
 * -----------------------------------------------------------------
 */
class ResetTradePwdParm: BaseParm() {
    var realName: String? = null//姓名
    var idcard: String? = null//身份证号码
    var code: String? = null//验证码
    var newTradePassword: String? = null//新交易密码 MD5值
}