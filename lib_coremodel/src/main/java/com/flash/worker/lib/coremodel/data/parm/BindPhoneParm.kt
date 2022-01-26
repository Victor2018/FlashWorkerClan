package com.flash.worker.lib.coremodel.data.parm

import com.flash.worker.lib.coremodel.data.parm.BaseParm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BindPhoneParm
 * Author: Victor
 * Date: 2020/12/7 14:33
 * Description: 
 * -----------------------------------------------------------------
 */
class BindPhoneParm: BaseParm() {
    var phone: String? = null//手机号码
    var code: String? = null//验证码
    var openId: String? = null//第三方唯一ID
}