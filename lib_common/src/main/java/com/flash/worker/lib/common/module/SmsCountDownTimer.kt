package com.flash.worker.lib.common.module

import android.os.CountDownTimer
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SmsCountDownTimer
 * Author: Victor
 * Date: 2020/12/10 18:19
 * Description: 
 * -----------------------------------------------------------------
 */
class SmsCountDownTimer(
    millisInFuture: Long,
    countDownInterval: Long,
    var listener: OnCountDownTimerListener
): CountDownTimer(millisInFuture,countDownInterval) {

    var isRunning: Boolean = false

    override fun onFinish() {
        isRunning = false
        listener.onFinish()
    }

    override fun onTick(millisUntilFinished: Long) {
        isRunning = true
        listener.onTick(millisUntilFinished)
    }
}