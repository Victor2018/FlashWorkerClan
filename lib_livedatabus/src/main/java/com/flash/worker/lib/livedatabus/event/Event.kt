package com.flash.worker.lib.livedatabus.event

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: Event
 * Author: Victor
 * Date: 2020/12/8 10:08
 * Description: 
 * -----------------------------------------------------------------
 */
interface Event {
    /**
     * 仅更新处于活动生命周期状态的应用程序组件观察者
     */
    fun observe(activity: AppCompatActivity, observer: Observer<Any?>)

    /**
     * 仅更新处于活动生命周期状态的应用程序组件观察者
     */
    fun observe(fragment: Fragment, observer: Observer<Any?>)

    /**
     * 不受生命周期的影响，只要数据更新就会收到通知
     */
    fun observeForever(activity: AppCompatActivity, observer: Observer<Any?>)

    /**
     * 不受生命周期的影响，只要数据更新就会收到通知
     */
    fun observeForever(fragment: Fragment, observer: Observer<Any?>)
}