package com.flash.worker.lib.pay.interfaces


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OnPayCompleteListener
 * Author: Victor
 * Date: 2020/11/30 18:10
 * Description: 
 * -----------------------------------------------------------------
 */
interface OnPayCompleteListener {
    fun OnPlayComplete(msg: String?, isPaySuccess: Boolean)
    fun OnAuthComplete(msg: String?, isAuthSuccess: Boolean)
}