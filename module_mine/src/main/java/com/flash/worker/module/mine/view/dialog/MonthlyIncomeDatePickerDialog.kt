package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.dlg_monthly_income_date_picker.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MonthlyIncomeDatePickerDialog
 * Author: Victor
 * Date: 2021/5/21 14:14
 * Description: 
 * -----------------------------------------------------------------
 */
class MonthlyIncomeDatePickerDialog(context: Context): AbsBottomDialog(context),
        View.OnClickListener, NumberPickerView.OnValueChangeListenerInScrolling {

    val TAG = "DatePickerDialog"

    var mOnDatePickListener: OnDatePickListener? = null

    var mDatePickerTitle: String? = null

    var startYear = 2021

    var mYear: String? = null
    var mMonth: String? = null

    var yearPosition: Int = 0
    var monthPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_monthly_income_date_picker

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

        initYearPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mYearPicker.setOnValueChangeListenerInScrolling(this)
    }

    fun initYearPicker() {
        var yearValue: MutableList<String> = ArrayList()
        var monthValue: MutableList<String> = ArrayList()

        var nowYear = DateUtil.getNowYear()

        var count = nowYear - startYear + 1

        for (i in 0 until count) {
            Loger.e(TAG,"initYearPicker-i = $i")
            var yearItem = (startYear + i).toString()
            yearValue.add(yearItem)

            if (TextUtils.equals(mYear,yearItem)) {
                yearPosition = i
            }
        }

        var nowMonth = DateUtil.getNowMonth()

        var startMonth = 1
        if (startYear == nowYear) {
            startMonth = 5
        }

        Loger.e(TAG,"startYear = $startYear")
        Loger.e(TAG,"nowYear = $nowYear")
        Loger.e(TAG,"startMonth = $startMonth")
        Loger.e(TAG,"nowMonth = $nowMonth")

        for (i in startMonth until nowMonth + 1) {
            var monthItem = i.toString()
            monthValue.add(monthItem)

            if (TextUtils.equals(mMonth,monthItem)) {
                monthPosition = i - startMonth
            }
        }

        mYearPicker.refreshByNewDisplayedValues(yearValue.toTypedArray())
        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())

        mYearPicker.value = yearPosition
        mMonthPicker.value = monthPosition
    }


    fun initMonthPicker () {
        var monthValue: MutableList<String> = ArrayList()

        var nowYear = DateUtil.getNowYear()//2021
        var selectYear = getCurrentYear().toInt()

        var startMonth = 1
        var endMonth = 12

        if (selectYear == nowYear) {
            var nowMonth = DateUtil.getNowMonth()
            endMonth = nowMonth
            startMonth = 5
        }

        for (i in startMonth until endMonth + 1) {
            var monthItem = i.toString()
            monthValue.add(monthItem)

            if (TextUtils.equals(mMonth,monthItem)) {
                monthPosition = i - startMonth
            }
        }

        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())

        mMonthPicker.value = monthPosition

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

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                var month = getCurrentMonth()
                if (month.length < 2) {
                    month = "0$month"
                }
                mOnDatePickListener?.OnDatePick(getCurrentYear() + "." + month)
                dismiss()
            }
        }
    }


    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        when (picker) {
            mYearPicker -> {
                initMonthPicker()
            }
        }
    }
}