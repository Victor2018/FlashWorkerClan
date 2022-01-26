package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnWeightSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_weight_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WeightPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 体重dialog
 * -----------------------------------------------------------------
 */
class WeightPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "WeightPickerDialog"

    var mWeight: Int? = 0
    var checkPosition: Int = 0

    var mOnWeightSelectListener: OnWeightSelectListener? = null

    override fun bindContentView() = R.layout.dlg_weight_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initWeightPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun initWeightPicker() {
        var weightValue: MutableList<String> = ArrayList()

        var j: Int = 0
        for (i in 35 until 101) {
            weightValue.add(i.toString())
            if (mWeight == i) {
                checkPosition = j
            }
            j++
        }

        mWeightPicker.refreshByNewDisplayedValues(weightValue.toTypedArray())

        mWeightPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mWeightPicker.getValue() - mWeightPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mWeightPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnWeightSelectListener?.OnWeightSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }

}