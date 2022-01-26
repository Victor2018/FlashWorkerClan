package com.flash.worker.lib.coremodel.data.bean

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RequirementInfo
 * Author: Victor
 * Date: 2021/12/8 15:28
 * Description: 
 * -----------------------------------------------------------------
 */
class RequirementInfo: Serializable {
    var status: Boolean = false//是否满足报名条件：true-满足条件
    var msg: String? = null//错误消息
    var actionType: Int = 0//跳转类型：1-去实名；2-去新增简历；3-选择简历
}