package com.flash.worker.lib.livedatabus.core

import androidx.lifecycle.MutableLiveData
import com.flash.worker.lib.livedatabus.event.DefaultEvent
import com.flash.worker.lib.livedatabus.event.Event
import com.flash.worker.lib.livedatabus.event.StickinessEvent
import java.util.HashMap


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LiveDataBus
 * Author: Victor
 * Date: 2020/12/8 10:07
 * Description: 事件消息总线
 * -----------------------------------------------------------------
 */
object LiveDataBus {

    private val liveDataMap = HashMap<String, MutableLiveData<Any?>>()

    /**
     * 关联一个普通事件消息，需要关联后才能收到发送的消息
     * @param tag 事件标识
     */
    fun with(tag: Int): Event {
        return with(tag.toString())
    }

    /**
     * 关联一个普通事件消息，需要关联后才能收到发送的消息
     * @param tag 事件标识
     */
    fun with(tag: String): Event {
        return DefaultEvent(
            liveDataMap,
            tag
        )
    }

    /**
     * 关联一个粘性事件消息，可以收到关联之前发送的消息
     */
    fun withStickiness(tag: Int): Event {
        return withStickiness(tag.toString())
    }

    /**
     * 关联一个粘性事件消息，可以收到关联之前发送的消息
     */
    fun withStickiness(tag: String): Event {
        return StickinessEvent(
            liveDataMap,
            tag
        )
    }

    /**
     * 发送一个普通事件消息
     * @param tag 事件标识
     * @param t 事件内容
     */
    fun send(tag: Int, t: Any? = null) {
        send(tag.toString(), t)
    }

    /**
     * 发送一个普通事件消息
     * @param tag 事件标识
     * @param t 事件内容
     */
    fun send(tag: String, t: Any? = null) {
        if (!liveDataMap.containsKey(tag) || liveDataMap[tag] == null) return
        liveDataMap[tag]?.postValue(t)
    }


    /**
     * 发送多个普通事件消息
     * @param tags 事件标识集合
     * @param datas 事件内容
     */
    fun send1(tags: List<Int>, datas: List<Any>) {
        for (i in 0..tags.size) {
            send(tags[i], datas[i])
        }
    }

    /**
     * 发送一个粘性事件消息
     *  @param tag 事件标识
     *  @param t 事件内容
     */
    fun sendStickiness(tag: Int, t: Any? = null) {
        return sendStickiness(
            tag.toString(),
            t
        )
    }

    /**
     * 发送一个粘性事件消息
     */
    fun sendStickiness(tag: String, t: Any? = null) {
        if (!liveDataMap.containsKey(tag) || liveDataMap[tag] == null) {
            liveDataMap.put(tag, MutableLiveData())
        }
        liveDataMap[tag]?.postValue(t)
    }

}