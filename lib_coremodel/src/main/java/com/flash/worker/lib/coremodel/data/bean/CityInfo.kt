package com.flash.worker.lib.coremodel.data.bean

import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CityInfo
 * Author: Victor
 * Date: 2020/12/17 16:25
 * Description: 
 * -----------------------------------------------------------------
 */
class CityInfo: Serializable {
    var id: Int = 0
    var parentId: Int = 0
    var name: String? = null
    var childs: ArrayList<AreaInfo>? = null
}