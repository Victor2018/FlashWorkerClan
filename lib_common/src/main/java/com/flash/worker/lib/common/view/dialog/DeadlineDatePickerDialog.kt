package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnDateTimePickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import kotlinx.android.synthetic.main.dlg_date_time_picker.*
import kotlinx.android.synthetic.main.dlg_date_time_picker.mHourPicker
import kotlinx.android.synthetic.main.dlg_date_time_picker.mMinutePicker
import kotlinx.android.synthetic.main.dlg_date_time_picker.mTvCancel
import kotlinx.android.synthetic.main.dlg_date_time_picker.mTvConfirm
import kotlinx.android.synthetic.main.dlg_date_time_picker.mTvTitle


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DeadlineDatePickerDialog
 * Author: Victor
 * Date: 2020/12/18 9:47
 * Description: 
 * -----------------------------------------------------------------
 */
class DeadlineDatePickerDialog(context: Context): AbsBottomDialog(context), View.OnClickListener,
        NumberPickerView.OnValueChangeListenerInScrolling {
    val TAG = "DeadlineDatePickerDialog"

    var mOnDateTimePickListener: OnDateTimePickListener? = null

    var mDatePickerTitle: String? = null

    var startDate: String? = null
    var endDate: String? = null

    var startYear: Int = 0
    var endYear: Int = 0

    var startMonth: Int = 0
    var endMonth: Int = 0

    var startDay: Int = 0
    var endDay: Int = 0

    var startHour: Int = 0
    var endHour: Int = 0

    var startMinute = "00分"

    override fun bindContentView() = R.layout.dlg_date_time_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvTitle.text = mDatePickerTitle

        initYearPicker()
        setPickerListener()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun setPickerListener () {
        mYearPicker.setOnValueChangeListenerInScrolling(this)
        mMonthPicker.setOnValueChangeListenerInScrolling(this)
        mDayPicker.setOnValueChangeListenerInScrolling(this)
        mHourPicker.setOnValueChangeListenerInScrolling(this)
        mMinutePicker.setOnValueChangeListenerInScrolling(this)
    }

    fun initYearPicker() {
        Loger.e(TAG,"initYearPicker-startDate = " + startDate)
        Loger.e(TAG,"initYearPicker-endDate = " + endDate)
        var yearValue: MutableList<String> = ArrayList()
        var monthValue: MutableList<String> = ArrayList()
        var dayValue: MutableList<String> = ArrayList()
        var hourValue: MutableList<String> = ArrayList()
        var minuteValue: MutableList<String> = ArrayList()

        var startDateList = getDateList(startDate)
        var endDateList = getDateList(endDate)

        if (startDateList == null) return
        if (startDateList.size == 0) return

        if (endDateList == null) return
        if (endDateList.size == 0) return

        Loger.e(TAG,"initYearPicker-startDateList.size = " + startDateList.size)
        Loger.e(TAG,"initYearPicker-endDateList.size = " + endDateList.size)

        startYear = startDateList.get(0)
        endYear = endDateList.get(0)

        startMonth = startDateList.get(1)
        endMonth = endDateList.get(1)

        startDay = startDateList.get(2)
        endDay = endDateList.get(2)

        startHour = startDateList.get(3)
        endHour = endDateList.get(3)

        Loger.e(TAG,"initYearPicker-startYear = " + startYear)
        Loger.e(TAG,"initYearPicker-endYear = " + endYear)

        Loger.e(TAG,"initYearPicker-startMonth = " + startMonth)
        Loger.e(TAG,"initYearPicker-endMonth = " + endMonth)

        Loger.e(TAG,"initYearPicker-startDay = " + startDay)
        Loger.e(TAG,"initYearPicker-endDay = " + endDay)

        Loger.e(TAG,"initYearPicker-startHour = " + startHour)
        Loger.e(TAG,"initYearPicker-endHour = " + endHour)

        Loger.e(TAG,"initYearPicker-startMinute = " + startMinute)

        for (i in startYear until endYear + 1) {
            yearValue.add(i.toString() + "年")
        }

        for (i in startMonth until endMonth + 1) {
            monthValue.add(i.toString() + "月")
        }
        for (i in startDay until endDay + 1) {
            dayValue.add(i.toString() + "日")
        }
        for (i in 0 until 24) {
            if (i >= startHour) {
                if (i < 10) {
                    hourValue.add("0" + i.toString() + "点")
                } else {
                    hourValue.add(i.toString() + "点")
                }
            }
        }

        if (TextUtils.equals("30",startMinute)) {
            minuteValue.add("30分")
        } else {
            minuteValue.add("00分")
            minuteValue.add("30分")
        }

        if (yearValue.size > 0) {
            mYearPicker.refreshByNewDisplayedValues(yearValue.toTypedArray())
        }
        if (monthValue.size > 0) {
            mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())
        }
        if (dayValue.size > 0) {
            mDayPicker.refreshByNewDisplayedValues(dayValue.toTypedArray())
        }
        if (hourValue.size > 0) {
            mHourPicker.refreshByNewDisplayedValues(hourValue.toTypedArray())
        }
        if (minuteValue.size > 0) {
            mMinutePicker.refreshByNewDisplayedValues(minuteValue.toTypedArray())
        }

    }

    fun initDayPicker () {
        var days = DateUtil.getDaysByYearMonth(getCurrentYear().toInt(),getCurrentMonth().toInt())
        var selectMonth = getCurrentMonth().toInt()
        var dayValue: MutableList<String> = ArrayList()

        for (i in 0 until days) {
            var dayItem = (i + 1).toString()

            if (startMonth == selectMonth) {
                if (i+1 >= startDay) {
                    dayValue.add(dayItem + "日")
                }
            } else {
                if (i <= endDay) {
                    dayValue.add(dayItem + "日")
                }
            }
        }

        mDayPicker.refreshByNewDisplayedValues(dayValue.toTypedArray())
    }

    fun initHourPicker () {
        Loger.e(TAG,"initHourPicker-startDay = " + startDay)
        Loger.e(TAG,"initHourPicker-startHour = " + startHour)
        Loger.e(TAG,"initHourPicker-endHour = " + endHour)
        var hours = 24
        var selectDay = getCurrentDay().toInt()
        var hourValue: MutableList<String> = ArrayList()

        for (i in 0 until hours) {
            var dayItem = i.toString()

            if (startDay == selectDay) {
                if (i >= startHour) {
                    if (i < 10) {
                        hourValue.add("0" + i.toString() + "点")
                    } else {
                        hourValue.add(dayItem + "点")
                    }
                }
            } else if (endDay == selectDay) {
                if (i <= endHour) {
                    if (i < 10) {
                        hourValue.add("0" + i.toString() + "点")
                    } else {
                        hourValue.add(dayItem + "点")
                    }
                }
            } else {
                if (i < 10) {
                    hourValue.add("0" + i.toString() + "点")
                } else {
                    hourValue.add(dayItem + "点")
                }
            }
        }

        mHourPicker.refreshByNewDisplayedValues(hourValue.toTypedArray())
    }

    fun initMinPicker () {
        var minuteValue: MutableList<String> = ArrayList()

        var selectHour = getCurrentHour().toInt()

        if (selectHour == startHour) {
            if (TextUtils.equals("30分",startMinute)) {
                minuteValue.add("30分")
            } else {
                minuteValue.add("00分")
                minuteValue.add("30分")
            }
        } else {
            minuteValue.add("00分")
            minuteValue.add("30分")
        }

        mMinutePicker.refreshByNewDisplayedValues(minuteValue.toTypedArray())
    }

    fun getDateList (date: String?):List<Int> {
        var formatDate = date?.replace(":",".")
        Loger.e(TAG,"getDateList-date = " + date)
        Loger.e(TAG,"getDateList-formatDate = " + formatDate)
        var datas = ArrayList<Int>()
        if (!TextUtils.isEmpty(formatDate)) {
            if (formatDate?.contains(".")!!) {
                var values = formatDate.split(".")
                if (values.size >= 5) {
                    datas.add(values?.get(0).toInt())
                    datas.add(values?.get(1).toInt())
                    datas.add(values?.get(2).toInt())
                    datas.add(values?.get(3).toInt())
                    datas.add(values?.get(4).toInt())
                }
            }
        }
        return datas
    }

    fun getCurrentYearPosition(): Int {
        var position = mYearPicker.getValue() - mYearPicker.getMinValue()
        return position
    }

    fun getCurrentYear() : String {
        var yearPosition = getCurrentYearPosition()
        val yearContent: Array<String> = mYearPicker.getDisplayedValues()
        return yearContent[yearPosition].replace("年","")
    }

    fun getCurrentMonthPosition(): Int {
        var position = mMonthPicker.getValue() - mMonthPicker.getMinValue()
        return position
    }

    fun getCurrentMonth() : String {
        var monthPosition = getCurrentMonthPosition()
        val monthContent: Array<String> = mMonthPicker.getDisplayedValues()
        return monthContent[monthPosition].replace("月","")
    }

    fun getCurrentDayPosition(): Int {
        var position = mDayPicker.getValue() - mDayPicker.getMinValue()
        return position
    }

    fun getCurrentDay() : String {
        var dayPosition = getCurrentDayPosition()
        val dayContent: Array<String> = mDayPicker.getDisplayedValues()
        return dayContent[dayPosition].replace("日","")
    }

    fun getCurrentHourPosition(): Int {
        var position = mHourPicker.getValue() - mHourPicker.getMinValue()
        return position
    }

    fun getCurrentHour() : String {
        var hourPosition = getCurrentHourPosition()
        val hourContent: Array<String> = mHourPicker.getDisplayedValues()
        return hourContent[hourPosition].replace("点","")
    }

    fun getCurrentMinPosition(): Int {
        var position = mMinutePicker.getValue() - mMinutePicker.getMinValue()
        return position
    }

    fun getCurrentMin() : String {
        var minPosition = getCurrentMinPosition()
        val minContent: Array<String> = mMinutePicker.getDisplayedValues()
        return minContent[minPosition].replace("分","")
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                var year = getCurrentYear()
                var month = getCurrentMonth()
                var day = getCurrentDay()
                var hour = getCurrentHour()
                var min = getCurrentMin()

                if (month.length < 2) {
                    month = "0" + month
                }

                if (day.length < 2) {
                    day = "0" + day
                }

                var date = "$year.$month.$day $hour:$min"
                mOnDateTimePickListener?.OnDateTimePick(date)
                dismiss()
            }
        }
    }

    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        when (picker) {
            mYearPicker -> {
                initDayPicker()
            }
            mMonthPicker -> {
                initDayPicker()
            }
            mDayPicker -> {
                initHourPicker()
            }
            mHourPicker -> {
                initMinPicker()
            }
        }
    }
}