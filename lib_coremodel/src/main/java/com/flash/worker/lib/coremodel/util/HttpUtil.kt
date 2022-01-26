package com.flash.worker.lib.coremodel.util

import android.content.Context
import com.flash.worker.lib.coremodel.data.bean.HttpHeaderParm
import com.flash.worker.lib.coremodel.data.req.LoginReq

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HttpUtil
 * Author: Victor
 * Date: 2020/9/2 下午 03:33
 * Description: 
 * -----------------------------------------------------------------
 */

object HttpUtil {
    fun getHttpHeaderParm(context: Context?): HttpHeaderParm {
        val header = HttpHeaderParm()
        header.deviceId = DeviceUtils.getUDID(context)
        header.deviceBrand = DeviceUtils.getPhoneBrand()
        header.deviceModel = DeviceUtils.getPhoneModel()
        header.systemVersion = DeviceUtils.getSysVersion()
        header.systemType = "Android"
        header.appVersionName = AppUtil.getAppVersionName(context)
        return header
    }
}