package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: VerifyResetTradePwdParm
 * Author: Victor
 * Date: 2021/2/1 16:39
 * Description: 
 * -----------------------------------------------------------------
 */
class VerifyResetTradePwdParm: BaseParm() {
    var realName: String? = null//姓名
    var idcard: String? = null//身份证号码
    var code: String? = null//验证码
}