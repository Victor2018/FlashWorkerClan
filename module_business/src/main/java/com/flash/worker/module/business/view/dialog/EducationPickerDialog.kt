package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnEducationSelectListener
import kotlinx.android.synthetic.main.dlg_education_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EducationPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 学历选择 dialog
 * -----------------------------------------------------------------
 */
class EducationPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "EducationPickerDialog"

    var mOnEducationSelectListener: OnEducationSelectListener? = null

    var mEducation: String? = null
    var checkPosition: Int = 0
    var title: String? = null

    override fun bindContentView() = R.layout.dlg_education_picker

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
        initWorkingYearsPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        mTvTitle.text = title
    }

    fun initWorkingYearsPicker() {
        var educationValue = ResUtils.getStringArrayRes(R.array.business_educations)
        if (!TextUtils.isEmpty(title)) {
            educationValue = ResUtils.getStringArrayRes(R.array.business_requirement_educations)
        }

        for (i in 0 until educationValue?.size!!) {
            if (TextUtils.equals(mEducation,educationValue?.get(i))) {
                checkPosition = i
            }
        }

        mEducationPicker.refreshByNewDisplayedValues(educationValue)

        mEducationPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mEducationPicker.getValue() - mEducationPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mEducationPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnEducationSelectListener?.OnEducationSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }

}