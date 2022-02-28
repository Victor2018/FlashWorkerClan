package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnWorkingHoursSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_working_hours_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkingHoursPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 工作时长 dialog
 * -----------------------------------------------------------------
 */
class WorkingHoursPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "WorkingHoursPickerDialog"

    var mOnWorkingHoursSelectListener: OnWorkingHoursSelectListener? = null

    var mWorkHours: String? = "7.5小时"
    var checkPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_working_hours_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initWorkingHoursPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun initWorkingHoursPicker() {
        var workingHoursValue = ArrayList<String>()

        for (i in 2 until 37) {
            var item = ""
            if (i % 2 == 0) {
                item = (i / 2).toString() + "小时"
            } else {
                item = (0.5 * i).toString() + "小时"
            }

            if (TextUtils.equals(mWorkHours,item)) {
                checkPosition = i - 2
            }
            workingHoursValue.add(item)
        }
        mWorkingHoursPicker.refreshByNewDisplayedValues(workingHoursValue.toTypedArray())

        mWorkingHoursPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mWorkingHoursPicker.getValue() - mWorkingHoursPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mWorkingHoursPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnWorkingHoursSelectListener?.OnWorkingHoursSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }

}