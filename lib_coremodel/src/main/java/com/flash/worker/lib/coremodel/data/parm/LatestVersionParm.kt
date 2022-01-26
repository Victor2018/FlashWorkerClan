package com.flash.worker.lib.coremodel.data.parm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LatestVersionParm
 * Author: Victor
 * Date: 2021/6/2 11:29
 * Description: 
 * -----------------------------------------------------------------
 */
class LatestVersionParm: BaseParm() {
    var versionCode: Int = 0//版本代码
    var systemType: String? = "Android"//系统类型：iOS；Android
}