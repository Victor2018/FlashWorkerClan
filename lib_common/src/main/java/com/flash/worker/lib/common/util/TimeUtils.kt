package com.flash.worker.lib.common.util

import android.widget.TextView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import java.text.DateFormat
import java.text.SimpleDateFormat


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TimeUtils
 * Author: Victor
 * Date: 2020/12/30 10:42
 * Description: 
 * -----------------------------------------------------------------
 */
object TimeUtils {
    private const val TIME_ONEDAY = 24 * 60 * 60 * 1000.toLong()

    private val mDataTimeFormat: DateFormat = SimpleDateFormat("昨天 HH:mm")

    private val mLiveDataFormat: DateFormat = SimpleDateFormat("MM-dd HH:mm")

    var mMessageDataFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    private val mLiveTimeFormat: DateFormat = SimpleDateFormat("HH:mm")

    private val nYearMonthDay: DateFormat = SimpleDateFormat("yyyyMMdd")

    private val mMMddHHmmFormat: DateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm")

    fun getYesterDayTime(time: Long): String? {
        return mDataTimeFormat.format(time)
    }

    fun getLiveData(times: Long): String? {
        var times = times
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return mLiveDataFormat.format(times)
    }

    fun getShowData(times: Long?, sdf: DateFormat?): String? {
        var times = times ?: 0L
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return sdf?.format(times)
    }

    fun getLiveTime(times: Long?): String? {
        var times = times ?: 0L
        if (times == 0L) {
            times = System.currentTimeMillis()
        }
        return mLiveTimeFormat.format(times)
    }

    fun bindShowTime(tvTime: TextView, time: Long?) {
        bindShowTime(tvTime, time, mLiveDataFormat)
    }

    fun bindShowTime(
        tvTime: TextView,
        time: Long?,
        sdf: DateFormat?
    ) {
        var text = formatImTime(time)
        tvTime.text = text
    }

    fun formatImTime(time: Long?): String? {
        var text: String? = null
        val nowTime = System.currentTimeMillis()
        val dayTime = java.lang.Long.valueOf(nYearMonthDay.format(time))
        val nowdayTime = java.lang.Long.valueOf(nYearMonthDay.format(nowTime))
        val dateTimeInterval = nowdayTime - dayTime
        //判定日期间隔
        if (dateTimeInterval == 0L) {
            //今天
            text = getLiveTime(time)
        } else if (dateTimeInterval == 1L) {
            //昨天
            text = App.get().getString(R.string.yesterday) + getLiveTime(time)
        } else {
            text = getShowData(time, mLiveDataFormat)
        }
        return text
    }

    /**
     * 显示直播时长
     *
     * @param duration
     * @return
     */
    fun getHMS(duration: Long): String? {
        val h = (duration / 60 / 60).toInt()
        val m = (duration / 60).toInt()
        val s = (duration % 60).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    fun getMMddHHmm(time: Long): String? {
        return mMMddHHmmFormat.format(time)
    }
}