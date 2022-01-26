package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import cn.carbswang.android.numberpickerview.library.NumberPickerView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnAgeSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import kotlinx.android.synthetic.main.dlg_age_requirement.*
import kotlinx.android.synthetic.main.dlg_age_requirement.mTvCancel
import kotlinx.android.synthetic.main.dlg_age_requirement.mTvConfirm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AgeRequirementDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class AgeRequirementDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener,
    NumberPickerView.OnValueChangeListenerInScrolling {
    val TAG = "AgeRequirementDialog"

    var mOnAgeSelectListener: OnAgeSelectListener? = null

    var mStartAge: Int = 0
    var mEndAge: Int = 0
    var startAgeCheckPosition: Int = 0
    var endAgeCheckPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_age_requirement

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initStartAgePicker()
        initEndAgePicker(17)

        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mStartAgePicker.setOnValueChangeListenerInScrolling(this)
    }


    fun initStartAgePicker() {
        var startAgeValue: MutableList<String> = ArrayList()

        for (i in 16 until 71) {
            startAgeValue.add(i.toString())
            if (mStartAge == i) {
                startAgeCheckPosition = i - 16
            }
        }

        mStartAgePicker.refreshByNewDisplayedValues(startAgeValue.toTypedArray())
        mStartAgePicker.value = startAgeCheckPosition
    }

    fun initEndAgePicker(startAge: Int) {
        Loger.e(TAG,"initEndAgePicker-startAge = $startAge")
        var endAgeValue: MutableList<String> = ArrayList()

        var j = 0
        for (i in startAge until 71) {
            endAgeValue.add(i.toString())
            if (mEndAge == i) {
                endAgeCheckPosition = j
            }
            j++
        }

        mEndAgePicker.refreshByNewDisplayedValues(endAgeValue.toTypedArray())
        mEndAgePicker.value = endAgeCheckPosition
    }

    fun getCurrentStartAgePosition (): Int {
        var position = mStartAgePicker.getValue() - mStartAgePicker.getMinValue()
        return position
    }

    fun getCurrentStartAgeContent(): String {
        val content: Array<String> = mStartAgePicker.getDisplayedValues()
        var value = content[getCurrentStartAgePosition()]
        return value
    }

    fun getCurrentEndAgePosition (): Int {
        var position = mEndAgePicker.getValue() - mEndAgePicker.getMinValue()
        return position
    }

    fun getCurrentEndAgeContent(): String {
        val content: Array<String> = mEndAgePicker.getDisplayedValues()
        var value = content[getCurrentEndAgePosition()]
        return value
    }

    fun getSelectAge (): String {
        var selectAge = getCurrentStartAgeContent() + "-" + getCurrentEndAgeContent()
        Loger.e(TAG,"selectAge = $selectAge")
        return selectAge
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnAgeSelectListener?.OnAgeSelect(getSelectAge())
                dismiss()
            }
        }
    }

    override fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
        when (picker) {
            mStartAgePicker -> {
                val content: Array<String> = mStartAgePicker.getDisplayedValues()
                var value = content[newVal]
                var startAge = value.toInt() + 1

                if (startAge > 70) {
                    startAge = 70
                }

                initEndAgePicker(startAge)
            }
        }
    }
}