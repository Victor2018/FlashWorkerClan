package com.flash.worker.lib.common.interfaces

import com.amap.api.location.AMapLocation


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnLocationListener
 * Author: Victor
 * Date: 2021/8/17 14:26
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnLocationListener {
    fun OnLocation(location: AMapLocation?, errorCode: Int,error: String?)
}