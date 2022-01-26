package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import kotlinx.android.synthetic.main.dlg_date_picker.*
import kotlinx.android.synthetic.main.dlg_date_picker.mTvCancel


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DatePickerDialog
 * Author: Victor
 * Date: 2020/12/18 9:47
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkDatePickerDialog(context: Context): AbsBottomDialog(context), View.OnClickListener,
        NumberPickerView.OnValueChangeListenerInScrolling {
    val TAG = "DatePickerDialog"

    var mOnDatePickListener: OnDatePickListener? = null

    var mDatePickerTitle: String? = null

    var startDate: String? = null
    var endDate: String? = null

    var startYear: Int = 0
    var endYear: Int = 0

    var startMonth: Int = 0
    var endMonth: Int = 0

    var startDay: Int = 0
    var endDay: Int = 0

    override fun bindContentView() = R.layout.dlg_date_picker

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


    fun getDateList (date: String?):List<Int> {
        var datas = ArrayList<Int>()
        if (!TextUtils.isEmpty(date)) {
            if (date?.contains(".")!!) {
                var values = date?.split(".")
                if (values.size >= 3) {
                    datas.add(values?.get(0).toInt())
                    datas.add(values?.get(1).toInt())
                    datas.add(values?.get(2).toInt())
                }
            }
        }
        return datas
    }

    fun setPickerListener () {
        mYearPicker.setOnValueChangeListenerInScrolling(this)
        mMonthPicker.setOnValueChangeListenerInScrolling(this)
        mDayPicker.setOnValueChangeListenerInScrolling(this)
    }

    fun initYearPicker() {
        Loger.e(TAG,"initYearPicker-startDate = " + startDate)
        Loger.e(TAG,"initYearPicker-endDate = " + endDate)

        var yearValue: MutableList<String> = ArrayList()
        var monthValue: MutableList<String> = ArrayList()
        var dayValue: MutableList<String> = ArrayList()


        var startDateList = getDateList(startDate)
        var endDateList = getDateList(endDate)

        startYear = startDateList.get(0)
        endYear = endDateList.get(0)

        startMonth = startDateList.get(1)
        endMonth = endDateList.get(1)

        startDay = startDateList.get(2)
        endDay = endDateList.get(2)

        for (i in startYear until endYear + 1) {
            Loger.e(TAG,"initYearPicker-year = " + i)
            yearValue.add(i.toString())
        }

        mYearPicker.refreshByNewDisplayedValues(yearValue.toTypedArray())

        //如果是跨年结束月比开始月小 12 1
        if (endMonth < startMonth) {
            monthValue.add(startMonth.toString())
        } else {
            for (i in startMonth until endMonth + 1) {
                monthValue.add(i.toString())
            }
        }

        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())

        if (startMonth == endMonth) {
            var days = DateUtil.getDaysByYearMonth(getCurrentYear().toInt(),getCurrentMonth().toInt())
            //如果是跨天结束天比开始天小 31，1
            if (endDay < startDay) {
                for (i in startDay until days + 1) {
                    dayValue.add(i.toString())
                }
            } else {
                for (i in startDay until endDay + 1) {
                    dayValue.add(i.toString())
                }
            }
        } else {
            var startMonthDays = DateUtil.getDaysByYearMonth(getCurrentYear().toInt(),startMonth)
            Loger.e(TAG,"startMonth = " + startMonth)
            Loger.e(TAG,"startDay = " + startDay)
            Loger.e(TAG,"startMonthDays = " + startMonthDays)

            for (i in startDay until startMonthDays + 1) {
                dayValue.add(i.toString())
            }

        }
        mDayPicker.refreshByNewDisplayedValues(dayValue.toTypedArray())
    }

    fun initMonthPicker () {
        var monthValue: MutableList<String> = ArrayList()

        var currentYearPosition = getCurrentYearPosition()
        if (currentYearPosition == 0) {
            //如果是跨年结束月比开始月小 12 1
            monthValue.add(startMonth.toString())
            if (endMonth > startMonth) {
                monthValue.add(endMonth.toString())
            }
        } else {
            monthValue.add(endMonth.toString())
        }

        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())
    }

    fun initDayPicker () {
        var days = DateUtil.getDaysByYearMonth(getCurrentYear().toInt(),getCurrentMonth().toInt())

        Loger.e(TAG,"initDayPicker-getCurrentMonth() = " + getCurrentMonth())
        Loger.e(TAG,"initDayPicker-days = " + days)

        var selectMonth = getCurrentMonth().toInt()
        var dayValue: MutableList<String> = ArrayList()

        for (i in 1 until days + 1) {
            var dayItem = i.toString()

            if (startMonth == selectMonth) {
                if (i >= startDay) {
                    dayValue.add(dayItem)
                }
            } else if (endMonth == selectMonth) {
                if (i <= endDay) {
                   dayValue.add(dayItem)
               }
            } else {
                dayValue.add(dayItem)
            }
        }

        mDayPicker.refreshByNewDisplayedValues(dayValue.toTypedArray())
    }

    fun getCurrentYearPosition(): Int {
        var position = mYearPicker.getValue() - mYearPicker.getMinValue()
        return position
    }

    fun getCurrentYear() : String {
        var yearPosition = getCurrentYearPosition()
        val yearContent: Array<String> = mYearPicker.getDisplayedValues()
        return yearContent[yearPosition]
    }

    fun getCurrentMonthPosition(): Int {
        var position = mMonthPicker.getValue() - mMonthPicker.getMinValue()
        return position
    }

    fun getCurrentMonth() : String {
        var monthPosition = getCurrentMonthPosition()
        val monthContent: Array<String> = mMonthPicker.getDisplayedValues()
        return monthContent[monthPosition]
    }

    fun getCurrentDayPosition(): Int {
        var position = mDayPicker.getValue() - mDayPicker.getMinValue()
        return position
    }

    fun getCurrentDay() : String {
        var dayPosition = getCurrentDayPosition()
        val dayContent: Array<String> = mDayPicker.getDisplayedValues()
        return dayContent[dayPosition]
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                var month = getCurrentMonth()
                if (month.length < 2) {
                    month = "0" + month
                }
                var days = getCurrentDay()
                if (days.length < 2) {
                    days = "0" + days
                }
                mOnDatePickListener?.OnDatePick(getCurrentYear() + "." + month + "." + days)
                dismiss()
            }
        }
    }

    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        when (picker) {
            mYearPicker -> {
                initMonthPicker()
                initDayPicker()
            }
            mMonthPicker -> {
                initDayPicker()
            }
        }
    }
}