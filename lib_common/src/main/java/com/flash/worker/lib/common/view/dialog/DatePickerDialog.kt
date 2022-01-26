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
class DatePickerDialog(context: Context): AbsBottomDialog(context), View.OnClickListener,
        NumberPickerView.OnValueChangeListenerInScrolling {
    val TAG = "DatePickerDialog"

    var mOnDatePickListener: OnDatePickListener? = null

    var mDatePickerTitle: String? = null

    var mYear: String? = null
    var mMonth: String? = null
    var mDay: String? = null

    var yearPosition: Int = 0
    var monthPosition: Int = 0
    var dayPosition: Int = 0

    var showDayPicker: Boolean = true

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

        mYear = DateUtil.getNowYear().toString()
        mMonth = DateUtil.getNowMonth().toString()
        mDay = DateUtil.getNowDay().toString()

        initYearPicker()
        setPickerListener()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        if (showDayPicker) {
            mDayPicker.visibility = View.VISIBLE
        } else {
            mDayPicker.visibility = View.GONE
        }
    }

    fun setPickerListener () {
        mYearPicker.setOnValueChangeListenerInScrolling(this)
        mMonthPicker.setOnValueChangeListenerInScrolling(this)
        mDayPicker.setOnValueChangeListenerInScrolling(this)
    }

    fun initYearPicker() {
        var yearValue: MutableList<String> = ArrayList()
        var monthValue: MutableList<String> = ArrayList()
        var dayValue: MutableList<String> = ArrayList()

        var nowYear = DateUtil.getNowYear()

        var startYear = nowYear - 30
        var count = nowYear -startYear + 1

        for (i in 0 until count) {
            Loger.e(TAG,"initYearPicker-i = $i")
            var yearItem = (startYear + i).toString()
            yearValue.add(yearItem)

            if (TextUtils.equals(mYear,yearItem)) {
                yearPosition = i
            }
        }

        for (i in 0 until 12) {
            var monthItem = (i + 1).toString()
            monthValue.add(monthItem)

            if (TextUtils.equals(mMonth,monthItem)) {
                monthPosition = i
            }
        }

        var days = DateUtil.getDaysByYearMonth(nowYear,1)

        for (i in 0 until days) {
            var dayItem = (i + 1).toString()
            dayValue.add(dayItem)

            if (TextUtils.equals(mDay,dayItem)) {
                dayPosition = i
            }
        }

        mYearPicker.refreshByNewDisplayedValues(yearValue.toTypedArray())
        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())
        mDayPicker.refreshByNewDisplayedValues(dayValue.toTypedArray())

        mYearPicker.value = yearPosition
        mMonthPicker.value = monthPosition
        mDayPicker.value = dayPosition
    }

    fun initDayPicker () {
        var days = DateUtil.getDaysByYearMonth(getCurrentYear().toInt(),getCurrentMonth().toInt())

        var dayValue: MutableList<String> = ArrayList()
        for (i in 0 until days) {
            dayValue.add((i + 1).toString())
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
                if (showDayPicker) {
                    mOnDatePickListener?.OnDatePick(getCurrentYear() + "." + month + "." + days)
                } else {
                    mOnDatePickListener?.OnDatePick(getCurrentYear() + "." + month)
                }
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
        }
    }
}