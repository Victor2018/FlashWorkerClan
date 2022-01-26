package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CheckSignUpData
 * Author: Victor
 * Date: 2021/2/7 14:27
 * Description: 
 * -----------------------------------------------------------------
 */
class CheckSignUpData: Serializable {
    var status: Boolean = false//是否满足报名条件：true-满足条件
    var msg: String? = null//错误消息
    var actionType: Int = 0//跳转类型：1-去实名；2-去新增简历；3-选择简历
}