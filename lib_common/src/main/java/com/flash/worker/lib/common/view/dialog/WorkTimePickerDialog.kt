package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnTimePickerListener
import com.flash.worker.lib.common.util.*
import kotlinx.android.synthetic.main.dlg_date_picker.*
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
class WorkTimePickerDialog(context: Context) : AbsBottomDialog(context), View.OnClickListener,
        NumberPickerView.OnValueChangeListenerInScrolling{
    val TAG = "TimePickerDialog"

    var mOnTimePickerListener: OnTimePickerListener? = null

    var mTimePickerTitle: String? = null

    var mStartDate = ""
    var mHour: String? = "08"
    var mMinute: String? = "00"

    var hourPotion: Int = 0
    var minutePotion: Int = 0

    var mStartHour: Int = 0
    var mEndHour: Int = 23

    var isEmergencyRelease: Boolean = false//是否是紧急发布
    var isSelectStartTime: Boolean = false//是否是选择开始时间
    var showFirst0 = true//分钟是否显示00
    var showLast30 = true//分钟是否显示30
    var currentTime: Long = 0

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

        mHourPicker.setOnValueChangeListenerInScrolling(this)
        initHourPicker()
        initMinPicker()

        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        var currentHour = DateUtil.stampToDate(currentTime,"HH")
        var currentMinute = DateUtil.stampToDate(currentTime,"mm")

        var isToday = DateUtil.isSameDay(mStartDate.replace(".","-"),currentTime)

        var startHour = currentHour?.toInt()

        var addHour = 3

        if (isEmergencyRelease && isToday) {//紧急发布
            if (currentMinute?.toInt()!! > 30) {
                mMinute = "00"
                addHour++
            } else {
                mMinute = "30"
            }
            startHour = startHour!! + addHour
            mHour = startHour.toString()
        } else {
            mMinute = "00"
        }

    }

    fun initHourPicker() {
        var hourValue: MutableList<String> = ArrayList()

        if (mStartHour <= mEndHour) {
            for (i in mStartHour until mEndHour + 1) {
                var hour: String? = null
                if (i < 10) {
                    hour = "0" + i.toString()
                } else {
                    hour = i.toString()
                }
                hourValue.add(hour)

                if (TextUtils.equals(mHour,hour)) {
                    hourPotion = i - mStartHour
                }
            }
        } else {
            for (i in mStartHour until 24) {
                var hour: String? = null
                if (i < 10) {
                    hour = "0" + i.toString()
                } else {
                    hour = i.toString()
                }
                hourValue.add(hour)

                if (TextUtils.equals(mHour,hour)) {
                    hourPotion = i - mStartHour
                }
            }
            for (i in 0 until mEndHour + 1) {
                var hour: String? = null
                if (i < 10) {
                    hour = "0" + i.toString()
                } else {
                    hour = i.toString()
                }
                hourValue.add(hour)
                if (TextUtils.equals(mHour,hour)) {
                    hourPotion = i
                }
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

        var currentHour = DateUtil.stampToDate(currentTime,"HH")?.toInt() ?: 0
        var currentMinute = DateUtil.stampToDate(currentTime,"mm")?.toInt() ?: 0

        var isToday = DateUtil.isSameDay(mStartDate.replace(".","-"),currentTime)

        Loger.e(TAG,"initMinPicker-mStartDate = $mStartDate")
        Loger.e(TAG,"initMinPicker-currentHour = $currentHour")
        Loger.e(TAG,"initMinPicker-currentMinute = $currentMinute")
        Loger.e(TAG,"initMinPicker-isEmergencyRelease = $isEmergencyRelease")
        Loger.e(TAG,"initMinPicker-isToday = $isToday")

        if (isSelectStartTime) {
            if (isEmergencyRelease) {
                if (isToday) {
                    if (currentHour == 20) {
                        if (currentMinute > 0 && currentMinute < 30) {
                            minuteValue.clear()
                            minuteValue.add("30")
                            minutePotion = 0
                        }
                    }
                } else {
                    if (currentHour > 20) {
                        if (getCurrentHourPosition() == 0) {
                            if (currentMinute > 0 && currentMinute < 30) {
                                minuteValue.clear()
                                minuteValue.add("30")
                                minutePotion = 0
                            }
                        }
                    } else {
                        if (currentHour == 20 && currentMinute > 30) {
                            if (getCurrentHourPosition() == 0) {
                                if (currentMinute > 0 && currentMinute < 30) {
                                    minuteValue.clear()
                                    minuteValue.add("30")
                                    minutePotion = 0
                                }
                            }
                        }
                    }
                }
            }
        } else {
            var currentHourPosition = getCurrentHourPosition()

            if (currentHourPosition == 0 && !showFirst0) {
                minuteValue.clear()
                minuteValue.add("30")
                minutePotion = 0
            }

            var count = mHourPicker.displayedValues.size
            Loger.e(TAG,"initMinPicker-currentHourPosition = $currentHourPosition")
            Loger.e(TAG,"initMinPicker-count = $count")
            if (currentHourPosition == count -1 && !showLast30) {
                minuteValue.clear()
                minuteValue.add("00")
                minutePotion = 0
            }
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

    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        initMinPicker()
    }

}