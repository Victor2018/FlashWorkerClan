package com.flash.worker.lib.coremodel.data.bean


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTypeInfo
 * Author: Victor
 * Date: 2020/12/22 19:44
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentTypeInfo {
    var id: String? = null
    var parentId: String? = null
    var name: String? = null
    var childs: List<TalentInfo>? = null
}