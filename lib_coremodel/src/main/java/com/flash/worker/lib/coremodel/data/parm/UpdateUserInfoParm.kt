package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: UpdateUserInfoParm
 * Author: Victor
 * Date: 2021/10/25 16:12
 * Description: 
 * -----------------------------------------------------------------
 */
class UpdateUserInfoParm: BaseParm() {
    var username: String? = null//闪工名
    var inviterUserId: String? = null//邀请人ID号
}