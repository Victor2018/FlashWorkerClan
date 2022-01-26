package com.flash.worker.lib.common.interfaces


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnCountDownTimerTickListener
 * Author: Victor
 * Date: 2020/12/10 18:21
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnCountDownTimerListener {
    fun onTick(millisUntilFinished: Long)
    fun onFinish()
}