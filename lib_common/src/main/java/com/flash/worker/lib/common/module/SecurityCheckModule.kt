package com.flash.worker.lib.common.module

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.SecurityCheckUtil


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SecurityCheckModule
 * Author: Victor
 * Date: 2021/9/27 17:04
 * Description: 
 * -----------------------------------------------------------------
 */
object SecurityCheckModule {
    val isDeviceSecurity = checkDeviceSecurity()

    fun checkDeviceSecurity(): LiveData<Boolean> = liveData {
        emit(SecurityCheckUtil.isDeviceSecurity(App.get()))
    }

}