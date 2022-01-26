package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnGenderSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import kotlinx.android.synthetic.main.dlg_gender_requirement.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GenderRequirementDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class GenderRequirementDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "GenderRequirementDialog"

    var mOnGenderSelectListener: OnGenderSelectListener? = null

    var mGender: String? = null
    var checkPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_gender_requirement

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
        var genderValue = ResUtils.getStringArrayRes(R.array.hire_gender_requirement)

        for (i in 0 until genderValue?.size!!) {
            if (TextUtils.equals(mGender,genderValue?.get(i))) {
                checkPosition = i
            }
        }

        mGenderPicker.refreshByNewDisplayedValues(genderValue)

        mGenderPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mGenderPicker.getValue() - mGenderPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mGenderPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnGenderSelectListener?.OnGenderSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }
}