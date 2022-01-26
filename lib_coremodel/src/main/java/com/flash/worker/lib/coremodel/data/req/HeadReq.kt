package com.flash.worker.lib.coremodel.data.req

import com.flash.worker.lib.coremodel.data.bean.MenuInfo
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HeadReq
 * Author: Victor
 * Date: 2020/12/2 15:22
 * Description: 
 * -----------------------------------------------------------------
 */
class HeadReq: Serializable, BaseReq() {
    var resultCode = 0
    var description: String? = null
    var menus: List<MenuInfo>? = null
}