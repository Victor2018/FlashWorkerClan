package com.flash.worker.lib.common.util

import android.text.TextUtils
import android.text.format.Time
import com.flash.worker.lib.common.data.DeadlineData
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DateUtil
 * Author: Victor
 * Date: 2020/7/21 上午 11:54
 * Description: 
 * -----------------------------------------------------------------
 */
object DateUtil {
    private const val TAG = "DateUtils"

    /**
     * 获取当前日期
     * @return
     */
    fun getCurrentTime(): String? {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return df.format(Date())
    }

    fun transDate(
        inputDate: String?,
        inputFormat: String?,
        outFormat: String?
    ): String {
        var date = ""
        if (TextUtils.isEmpty(inputDate)) return date
        val format = SimpleDateFormat(inputFormat)
        val c = Calendar.getInstance()
        try {
            c.time = format.parse(inputDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
//        Loger.e(TAG, "inputDate = $inputDate")
        val formatter = SimpleDateFormat(outFormat)
        date = formatter.format(c.time)
//        Loger.e(TAG, "date = $date")
        return date
    }

    /**
     * 获取当前时间的年
     * @return
     */
    fun getNowYear(): Int {
        try {
            val date = Date()
            val formatter = SimpleDateFormat("yyyy")
            return formatter.format(date).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 2021
    }
    fun getNowMonth(): Int {
        try {
            val date = Date()
            val formatter = SimpleDateFormat("MM")
            return formatter.format(date).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 1
    }
    fun getNowDay(): Int {
        try {
            val date = Date()
            val formatter = SimpleDateFormat("dd")
            return formatter.format(date).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 1
    }

    /**
     * 获取当前时间的小时数
     * @return
     */
    fun getNowHour(): Int {
        val date = Date()
        val formatter = SimpleDateFormat("HH")
        val today = formatter.format(date)
        var hour = 0
        try {
            hour = today.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return hour
    }
    /**
     * 获取当前时间的分钟数
     * @return
     */
    fun getNowMinute(): Int {
        val date = Date()
        val formatter = SimpleDateFormat("mm")
        val today = formatter.format(date)
        var minute = 0
        try {
            minute = today.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return minute
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    fun getDaysByYearMonth(year: Int, month: Int): Int {
        val a = Calendar.getInstance()
        a[Calendar.YEAR] = year
        a[Calendar.MONTH] = month - 1
        a[Calendar.DATE] = 1
        a.roll(Calendar.DATE, -1)
        return a[Calendar.DATE]
    }

    /**
     * 获取今天日期
     * @return
     */
    fun getTodayDate(): String? {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy年MM月dd")
        val today = formatter.format(date)
        Loger.e(TAG, "getDateOfToday-today = $today")
        return today
    }

    /**
     * 获取今天日期
     * @return
     */
    fun getTodayDate(formater: String?): String {
        val date = Date()
        val formatter = SimpleDateFormat(formater)
        val today = formatter.format(date)
        Loger.e(TAG, "getDateOfToday-today = $today")
        return today
    }

    /**
     * @param m 分钟差值
     * @return
     */
    fun ms2CreateTime(m: Long): String? {
        var res = ""
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        val ms = m * 60 * 1000
        day = ms / (24 * 60 * 60 * 1000)
        hour = (ms - day * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        min = (ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60)
        if (day > 0) {
            res = day.toString() + "天"
        } else if (hour > 0) {
            res = hour.toString() + "小时"
        } else if (min > 0) {
            res = min.toString() + "分钟"
        }
        return res
    }

    /**
     * @param ms 时间戳差值
     * @return
     */
    fun ms2SysCreateTime(ms: Long): DeadlineData {
        var res = ""
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var second: Long = 0
        day = ms / (24 * 60 * 60 * 1000)
        hour = (ms - day * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        min = (ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60)
        second = (ms - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - (min * 1000 * 60)) / 1000

        var data = DeadlineData()
        data.day = day.toInt()
        data.hour = hour.toInt()
        data.minute = min.toInt()
        data.second = second.toInt()

        return data
    }

    /**
     * @param time 日期
     * @return
     */
    fun handeDynamicTime(time: String): String? {
        Loger.e(TAG, "handeDynamicTime-time = $time")
        var res = ""
        val createTime: Long = dateToStamp(time)
        val currentTime: Long = getTimestamp()
        val diff = currentTime - createTime
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        if (days > 0) {
            res = if (days > 3) {
                transDate(time, "yyyy-MM-dd HH:mm:ss", "MM-dd HH:mm")
            } else {
                days.toString() + "天前"
            }
        } else if (hours > 0) {
            res = hours.toString() + "小时前"
        } else if (minutes > 0) {
            res = if (minutes < 30) {
                "刚刚"
            } else {
                minutes.toString() + "分钟前"
            }
        }
        Loger.e(TAG, "ms2SysCreateTime-res = $res")
        return res
    }

    /**
     * @param time 日期
     * @return
     */
    fun handeSysMessageTime(time: String): String? {
        Loger.e(TAG, "handeSysMessageTime-time = $time")
        var res = ""
        val createTime: Long = dateToStamp(time)
        val currentTime: Long = getTimestamp()
        val diff = currentTime - createTime
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes =
            (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
        if (days > 0) {
            res = if (days > 3) {
                transDate(time, "yyyy-MM-dd HH:mm:ss", "MM-dd HH:mm")
            } else {
                days.toString() + "天前"
            }
        } else if (hours > 0) {
            res = hours.toString() + "小时前"
        } else if (minutes > 0) {
            res = if (minutes < 5) {
                "刚刚"
            } else {
                minutes.toString() + "分钟前"
            }
        }
        Loger.e(TAG, "ms2SysCreateTime-res = $res")
        return res
    }

    fun getDiffHour(startTime: String,endTime: String): String? {
        Loger.e(TAG, "getDiffHour-startTime = $startTime")
        Loger.e(TAG, "getDiffHour-endTime = $endTime")

        var res = ""
        val start: Long = dateToStamp(startTime,"HH:mm")
        val end: Long = dateToStamp(endTime,"HH:mm")
        val diff = end - start
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)

        if (hours > 0) {
            res = hours.toString() + "小时"
        }
        if (minutes > 0) {
             res += minutes.toString() + "分钟"
        }
        Loger.e(TAG, "getDiffHour-res = $res")
        return res
    }

    fun isAfter3Hours(startTime: String?,endTime: String?): Boolean {
        if (TextUtils.isEmpty(startTime)) return false
        if (TextUtils.isEmpty(endTime)) return false
        Loger.e(TAG, "isAfterDate3Hours-startTime = $startTime")
        Loger.e(TAG, "isAfterDate3Hours-endTime = $endTime")

        val start: Long = dateToStamp(startTime,"HH:mm")
        val end: Long = dateToStamp(endTime,"HH:mm")
        val diff = end - start
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)

        return hours >= 3
    }

    fun isAfter18Hours(startTime: String,endTime: String): Boolean {
        Loger.e(TAG, "isAfterDate3Hours-startTime = $startTime")
        Loger.e(TAG, "isAfterDate3Hours-endTime = $endTime")

        val start: Long = dateToStamp(startTime,"HH:mm")
        val end: Long = dateToStamp(endTime,"HH:mm")
        val diff = end - start
        val days = diff / (1000 * 60 * 60 * 24)
        val hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)

        return hours > 18
    }

    fun getDiffDay(startDate: String,endDate: String): Int {
        if (TextUtils.isEmpty(startDate)) return 0
        if (TextUtils.isEmpty(endDate)) return 0
        Loger.e(TAG, "getDiffDay-startDate = $startDate")
        Loger.e(TAG, "getDiffDay-endDate = $endDate")

        val start: Long = dateToStamp(startDate,"yyyy.MM.dd")
        val end: Long = dateToStamp(endDate,"yyyy.MM.dd")
        val diff = end - start
        val days = diff / (1000 * 60 * 60 * 24)

        Loger.e(TAG, "getDiffDay-days = $days")
        return days.toInt() + 1
    }

    /**
     * 获取当前时间戳
     * @return
     */
    fun getTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /*
     * 将时间转换为时间戳
     */
    fun dateToStamp(s: String): Long {
        Loger.e(TAG, "dateToStamp-s = $s")
        var timeStamp: Long = 0
        if (TextUtils.isEmpty(s)) return timeStamp
        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = simpleDateFormat.parse(s)
            timeStamp = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeStamp
    }

    fun dateToStamp(s: String?,format: String): Long {
        Loger.e(TAG, "dateToStamp-s = $s")
        if (TextUtils.isEmpty(s)) return 0
        var timeStamp: Long = 0
        if (TextUtils.isEmpty(s)) return timeStamp
        val simpleDateFormat = SimpleDateFormat(format)
        try {
            val date = simpleDateFormat.parse(s)
            timeStamp = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeStamp
    }


    /*
     * 将时间戳转换为时间
     */
    fun stampToDate(s: Long): String? {
        var res: String? = null
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(s)
            res = simpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }
    /*
     * 将时间戳转换为时间
     */
    fun stampToDate(s: Long,format: String?): String? {
        var res: String? = null
        try {
            val simpleDateFormat = SimpleDateFormat(format)
            val date = Date(s)
            res = simpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }

    /**
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return
     */
    fun isToday(day: String?): Boolean {
        try {
            val pre = Calendar.getInstance()
            val predate = Date(System.currentTimeMillis())
            pre.time = predate
            val cal = Calendar.getInstance()
            val date = getDateFormat()!!.parse(day)
            cal.time = date
            if (cal[Calendar.YEAR] == pre[Calendar.YEAR]) {
                val diffDay = (cal[Calendar.DAY_OF_YEAR]
                        - pre[Calendar.DAY_OF_YEAR])
                if (diffDay == 0) {
                    return true
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 是否是同一天
     */
    fun isSameDay(day: String?,diffTime: Long): Boolean {
        try {
            val pre = Calendar.getInstance()
            val predate = Date(diffTime)
            pre.time = predate
            val cal = Calendar.getInstance()
            val date = getDateFormat()!!.parse(day)
            cal.time = date
            if (cal[Calendar.YEAR] == pre[Calendar.YEAR]) {
                val diffDay = (cal[Calendar.DAY_OF_YEAR]
                        - pre[Calendar.DAY_OF_YEAR])
                if (diffDay == 0) {
                    return true
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun isBeforeTodayDate(date: String?, formater: String?): Boolean {
        val today = getTodayDate(formater)
        val df: DateFormat = SimpleDateFormat(formater)
        try {
            val date1 = df.parse(date)
            val date2 = df.parse(today)
            if (date1.time < date2.time) {
                return true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    fun isBeforeStartDate(startDate: String?,endDate: String?, formater: String?): Boolean {
        val df: DateFormat = SimpleDateFormat(formater)
        try {
            val date1 = df.parse(endDate)
            val date2 = df.parse(startDate)
            if (date1.time < date2.time) {
                return true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    fun isNextDayDate(startDate: String?,endDate: String?, formater: String?): Boolean {
        val df: DateFormat = SimpleDateFormat(formater)
        try {
            val date1 = df.parse(endDate)
            val date2 = df.parse(startDate)
            if (date1.time <= date2.time) {
                return true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return false
    }

    /**
     * 判断当前系统时间是否在特定时间的段内
     *
     * @param beginHour 开始的小时，例如5
     * @param beginMin 开始小时的分钟数，例如00
     * @param endHour 结束小时，例如 8
     * @param endMin 结束小时的分钟数，例如00
     * @return true表示在范围内，否则false
     */
    fun isCurrentInTimeScope(
        beginHour: Int,
        beginMin: Int,
        endHour: Int,
        endMin: Int
    ): Boolean {
        var result = false // 结果
        val aDayInMillis = 1000 * 60 * 60 * 24.toLong() // 一天的全部毫秒数
        val currentTimeMillis = System.currentTimeMillis() // 当前时间
        val now = Time() // 注意这里导入的时候选择android.text.format.Time类,而不是java.sql.Time类
        now.set(currentTimeMillis)
        val startTime = Time()
        startTime.set(currentTimeMillis)
        startTime.hour = beginHour
        startTime.minute = beginMin
        val endTime = Time()
        endTime.set(currentTimeMillis)
        endTime.hour = endHour
        endTime.minute = endMin
        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis)
            result = !now.before(startTime) && !now.after(endTime) // startTime <= now <= endTime
            val startTimeInThisDay = Time()
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis)
            if (!now.before(startTimeInThisDay)) {
                result = true
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime) // startTime <= now <= endTime
        }
        return result
    }

    /**
     * @param time 日期
     * @return
     */
    fun moreThanOneMon(time: String): Boolean {
        Loger.e(TAG, "moreThanOneMon-time = $time")
        val res = ""
        val createTime =
            dateToStamp(transDate(time, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"))
        val currentTime = getTimestamp()
        val diff = createTime - currentTime
        val days = diff / (1000 * 60 * 60 * 24)
        Loger.e(TAG, "moreThanOneMon-days = $days")
        return days > 31
    }

    /**
     * 获取前n天日期，
     * 如获取距离今日7天前那一天的具体日期则getOldDate（-7）即可，
     * 后7天日期则getOldDate（7）
     */
    fun getOldFetureDate(currentTime: Long,distanceDay: Int,formater: String): String? {
        val dft = SimpleDateFormat(formater)
        val calendar = Calendar.getInstance()
        calendar.time = Date(currentTime)
        calendar[Calendar.DATE] = calendar[Calendar.DATE] + distanceDay
        var endDate: Date? = null
        try {
            endDate = dft.parse(dft.format(calendar.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dft.format(endDate)
    }

    fun getOldFetureDate(date: String?,distanceDay: Int,formater: String): String? {
        if (TextUtils.isEmpty(date)) return null
        if (TextUtils.isEmpty(formater)) return null
        if (distanceDay == 0) return null
        try {
            val dft = SimpleDateFormat(formater)
            val calendar = Calendar.getInstance()
            calendar.time = dft.parse(date)
            calendar[Calendar.DATE] = calendar[Calendar.DATE] + distanceDay
            val endDate = dft.parse(dft.format(calendar.time))
            return dft.format(endDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据日期获取时间戳
     */
    fun getTimestampByDate(timeString: String?,formater: String): Long {
        var timeStamp: Long = 0
        val sdf = SimpleDateFormat(formater)
        val d: Date
        try {
            d = sdf.parse(timeString)
            timeStamp = d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeStamp
    }

    /**
     *
     * @Title:
     * @Description:获取提前或延后的日期，格式为yyyy-MM-dd HH:mm
     * @return String
     * @Version:1.1.0
     */
    fun getRollDate(dateStr: String?, hour: Int,formater: String?): String? {
        return try {
            val format = SimpleDateFormat(formater)
            val date = format.parse(dateStr)
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.HOUR_OF_DAY, hour)
            format.format(cal.time)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getRollDate(ms: Long, hour: Int,formater: String?): String? {
        return try {
            val format = SimpleDateFormat(formater)
            val date = format.parse(stampToDate(ms,formater))
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.HOUR_OF_DAY, hour)
            format.format(cal.time)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun getRollMinute(ms: Long, minutes: Int,formater: String?): String? {
        return try {
            val format = SimpleDateFormat(formater)
            val date = format.parse(stampToDate(ms,formater))
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.MINUTE, minutes)
            format.format(cal.time)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun isHalfHourMinute (ms: Long): Boolean {
        var minutes = stampToDate(ms,"mm")
        return minutes?.toInt()!! <= 30
    }

    fun getAddHourByMinute (ms: Long): Int {
        var addHour = 0
        var minutes = stampToDate(ms,"mm")
        if (minutes?.toInt()!! > 30) {
             //30min
            addHour = 2
        } else {
            if (minutes?.toInt()!! == 0) {
                addHour = 1
            } else {
                addHour = 1
            }
        }
        return addHour
    }

    fun getDateFormat(): SimpleDateFormat? {
        if (null == DateLocal.get()) {
            DateLocal.set(SimpleDateFormat("yyyy-MM-dd", Locale.CHINA))
        }
        return DateLocal.get()
    }

    private val DateLocal =
        ThreadLocal<SimpleDateFormat?>()
}