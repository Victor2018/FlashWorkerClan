package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.dlg_work_experience_date_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkExperienceDatePickerDialog
 * Author: Victor
 * Date: 2021/6/9 15:48
 * Description: 
 * -----------------------------------------------------------------
 */
class WorkExperienceDatePickerDialog(context: Context): AbsBottomDialog(context), View.OnClickListener {
    val TAG = "WorkExperienceDatePickerDialog"

    var mOnDatePickListener: OnDatePickListener? = null

    var mDatePickerTitle: String? = null

    var mYear: String? = null
    var mMonth: String? = null

    var yearPosition: Int = 0
    var monthPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_work_experience_date_picker

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

        mYear = "${DateUtil.getNowYear()}年"
        mMonth = "${DateUtil.getNowMonth()}月"

        initYearPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initYearPicker() {
        var yearValue: MutableList<String> = ArrayList()
        var monthValue: MutableList<String> = ArrayList()

        var nowYear = DateUtil.getNowYear()

        var startYear = nowYear - 30
        var count = nowYear -startYear + 1

        for (i in 0 until count) {
            Loger.e(TAG,"initYearPicker-i = $i")
            var yearItem = "${startYear + i}年"
            yearValue.add(yearItem)

            if (TextUtils.equals(mYear,yearItem)) {
                yearPosition = i
            }
        }

        for (i in 0 until 12) {
            var monthItem = "${i + 1}月"
            monthValue.add(monthItem)

            if (TextUtils.equals(mMonth,monthItem)) {
                monthPosition = i
            }
        }

        mYearPicker.refreshByNewDisplayedValues(yearValue.toTypedArray())
        mMonthPicker.refreshByNewDisplayedValues(monthValue.toTypedArray())

        mYearPicker.value = yearPosition
        mMonthPicker.value = monthPosition
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

}