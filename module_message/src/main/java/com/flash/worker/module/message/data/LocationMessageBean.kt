package com.flash.worker.module.message.data

import java.io.Serializable


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LocationMessageBean
 * Author: Victor
 * Date: 2022/1/5 10:24
 * Description: 
 * -----------------------------------------------------------------
 */

class LocationMessageBean: Serializable {
    var latitude: Double = 0.0//维度
    var longitude: Double = 0.0//经度
    var address: String? = null//详细地址
}