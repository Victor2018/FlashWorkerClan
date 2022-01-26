package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnTimePickerListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_time_picker.*
import kotlinx.android.synthetic.main.dlg_time_picker.mTvCancel
import kotlinx.android.synthetic.main.dlg_time_picker.mTvConfirm
import kotlinx.android.synthetic.main.dlg_time_picker.mTvTitle


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TimePickerDialog
 * Author: Victor
 * Date: 2021/1/6 15:03
 * Description: 
 * -----------------------------------------------------------------
 */
class TimePickerDialog(context: Context) : AbsBottomDialog(context), View.OnClickListener {
    val TAG = "TimePickerDialog"

    var mOnTimePickerListener: OnTimePickerListener? = null

    var mTimePickerTitle: String? = null

    var mHour: String? = null
    var mMinute: String? = "00"

    var hourPotion: Int = 0
    var minutePotion: Int = 0

    override fun bindContentView() = R.layout.dlg_time_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initialize()
    }

    fun initialize () {
        mTvTitle.text = mTimePickerTitle

        initHourPicker()
        initMinPicker()

        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        mHour = DateUtil.getNowHour().toString()
    }

    fun initHourPicker() {
        var hourValue: MutableList<String> = ArrayList()

        for (i in 0 until 24) {
            if (i < 10) {
                hourValue.add("0" + i.toString())
            } else {
                hourValue.add(i.toString())
            }
            if (TextUtils.equals(mHour,i.toString())) {
                hourPotion = i
            }
        }

        mHourPicker.refreshByNewDisplayedValues(hourValue.toTypedArray())

        mHourPicker.value = hourPotion
    }

    fun initMinPicker() {
        var minuteValue: MutableList<String> = ArrayList()
        minuteValue.add("00")
        minuteValue.add("30")

        if (TextUtils.equals(mMinute,"00")) {
            minutePotion = 0
        } else {
            minutePotion = 1
        }

        mMinutePicker.refreshByNewDisplayedValues(minuteValue.toTypedArray())

        mMinutePicker.value = minutePotion
    }

    fun getCurrentHourPosition (): Int {
        var position = mHourPicker.getValue() - mHourPicker.getMinValue()
        return position
    }

    fun getCurrentHourContent(): String {
        val content: Array<String> = mHourPicker.getDisplayedValues()
        var value = content[getCurrentHourPosition()]
        return value
    }

    fun getCurrentMinutePosition (): Int {
        var position = mMinutePicker.getValue() - mMinutePicker.getMinValue()
        return position
    }

    fun getCurrentMinuteContent(): String {
        val content: Array<String> = mMinutePicker.getDisplayedValues()
        var value = content[getCurrentMinutePosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnTimePickerListener?.OnTimePicker(getCurrentHourContent() + ":" + getCurrentMinuteContent())
                dismiss()
            }
        }
    }

}