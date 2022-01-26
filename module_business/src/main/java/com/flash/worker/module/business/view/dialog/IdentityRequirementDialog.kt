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
import com.flash.worker.module.business.interfaces.OnIdentitySelectListener
import kotlinx.android.synthetic.main.dlg_identity_requirement.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IdentityRequirementDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class IdentityRequirementDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "IdentityRequirementDialog"

    var mOnIdentitySelectListener: OnIdentitySelectListener? = null

    var mIdentity: String? = null
    var checkPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_identity_requirement

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
        var genderValue = ResUtils.getStringArrayRes(R.array.hire_identity_requirement)

        for (i in 0 until genderValue?.size!!) {
            if (TextUtils.equals(mIdentity,genderValue?.get(i))) {
                checkPosition = i
            }
        }

        mIdentityPicker.refreshByNewDisplayedValues(genderValue)

        mIdentityPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mIdentityPicker.getValue() - mIdentityPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mIdentityPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                var identityRequirement = 3
                var position = getCurrentPosition()
                if (position == 0) {//不限
                    identityRequirement = 3
                } else if (position == 1) {//学生
                    identityRequirement = 2
                }
                mOnIdentitySelectListener?.OnIdentitySelect(identityRequirement,getCurrentContent())
                dismiss()
            }
        }
    }
}