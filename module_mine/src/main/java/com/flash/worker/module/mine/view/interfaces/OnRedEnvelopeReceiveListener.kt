package com.flash.worker.module.mine.view.interfaces

import com.flash.worker.lib.coremodel.data.bean.AcivityInfo


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnRedEnvelopeReceiveListener
 * Author: Victor
 * Date: 2021/6/1 9:54
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnRedEnvelopeReceiveListener {
    fun OnRedEnvelopeReceive(data: AcivityInfo?)
}