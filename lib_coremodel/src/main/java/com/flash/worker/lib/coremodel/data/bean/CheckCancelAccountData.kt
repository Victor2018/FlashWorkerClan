package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckCancelAccountData
 * Author: Victor
 * Date: 2021/6/3 9:39
 * Description: 
 * -----------------------------------------------------------------
 */
class CheckCancelAccountData {
    var status: Boolean = false//是否满足注销条件：true-可以注销
    var msg: String? = null//错误提示
}