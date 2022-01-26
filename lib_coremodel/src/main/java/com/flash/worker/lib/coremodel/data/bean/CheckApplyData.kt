package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckApplyData
 * Author: Victor
 * Date: 2021/5/18 17:06
 * Description: 
 * -----------------------------------------------------------------
 */
class CheckApplyData {
    var status: Boolean = false//是否满足申请条件
    var msg: String? = null//错误提示
    var msgType: Int = 0//消息类型：1-需要完成一单雇用或接活
}