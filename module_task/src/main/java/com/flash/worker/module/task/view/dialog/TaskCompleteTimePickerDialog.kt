package com.flash.worker.module.task.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.task.R
import com.flash.worker.module.task.interfaces.OnTaskCompleteTimeSelectListener
import kotlinx.android.synthetic.main.dlg_task_complete_time_picker.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskCompleteTimePickerDialog
 * Author: Victor
 * Date: 2021/11/30 15:58
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskCompleteTimePickerDialog(context: Context) : AbsBottomDialog(context), View.OnClickListener {
    val TAG = "TaskCompleteTimePickerDialog"

    var mCompleteTime: Int? = 0
    var checkPosition: Int = 0
    var isHour: Boolean = false

    var mOnTaskCompleteTimeSelectListener: OnTaskCompleteTimeSelectListener? = null

    override fun bindContentView() = R.layout.dlg_task_complete_time_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        initPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        if (isHour) {
            mTvUnit.text = "小时"
        } else {
            mTvUnit.text = "天"
        }
    }

    fun initPicker() {
        var value: MutableList<String> = ArrayList()

        var endValue = 30
        if (isHour) {
            endValue = 24
        }
        for (i in 1 until endValue + 1) {
            value.add(i.toString())
            if (mCompleteTime == i) {
                checkPosition = i - 1
            }
        }

        mCompleteTimePicker.refreshByNewDisplayedValues(value.toTypedArray())

        mCompleteTimePicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mCompleteTimePicker.getValue() - mCompleteTimePicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mCompleteTimePicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnTaskCompleteTimeSelectListener?.OnTaskCompleteTimeSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }
}