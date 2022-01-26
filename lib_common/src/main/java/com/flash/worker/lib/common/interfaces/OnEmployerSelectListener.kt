package com.flash.worker.lib.common.interfaces

import com.flash.worker.lib.coremodel.data.bean.EmployerInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnEmployerSelectListener
 * Author: Victor
 * Date: 2020/12/23 16:39
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnEmployerSelectListener {
    fun  OnEmployerSelect (data: EmployerInfo?, employerCount: Int)
}