package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnWorkingYearsSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import kotlinx.android.synthetic.main.dlg_working_years_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WorkingYearsPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 工作年限dialog
 * -----------------------------------------------------------------
 */
class WorkingYearsPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "WorkingYearsPickerDialog"

    var mOnWorkingYearsSelectListener: OnWorkingYearsSelectListener? = null

    var mWorkYears: String? = null
    var checkPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_working_years_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initWorkingYearsPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun initWorkingYearsPicker() {
        var workingYearsValue = ResUtils.getStringArrayRes(R.array.business_working_years)

        for (i in 0 until workingYearsValue?.size!!) {
            if (TextUtils.equals(mWorkYears,workingYearsValue?.get(i))) {
                checkPosition = i
            }
        }
        mWorkingYearsPicker.refreshByNewDisplayedValues(workingYearsValue)

        mWorkingYearsPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mWorkingYearsPicker.getValue() - mWorkingYearsPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mWorkingYearsPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnWorkingYearsSelectListener?.OnWorkingYearsSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }

}