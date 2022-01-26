package com.flash.worker.module.camera.util


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FastClickUtil
 * Author: Victor
 * Date: 2020/12/18 17:16
 * Description: 
 * -----------------------------------------------------------------
 */
object FastClickUtil {
    private var lastClickTime: Long = 0

    /**
     * 判断是否是快速点击
     *
     * @return true：是，false：否
     */
    fun isFastClick(): Boolean {
        return isFastClick(1000)
    }

    /**
     * 判断是否是快速点击
     *
     * @param intervalTime 间隔时间，单位毫秒。
     * @return true：是，false：否
     */
    fun isFastClick(intervalTime: Long): Boolean {
        val time = System.currentTimeMillis()
        if (time - lastClickTime < intervalTime) {
            return true
        }
        lastClickTime = time
        return false
    }
}