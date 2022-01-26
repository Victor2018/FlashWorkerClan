package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnHeightSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_height_picker.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HeightPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 身高dialog
 * -----------------------------------------------------------------
 */
class HeightPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "HeightPickerDialog"

    var mHeight: Int? = 0
    var checkPosition: Int = 0

    var mOnHeightSelectListener: OnHeightSelectListener? = null

    override fun bindContentView() = R.layout.dlg_height_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initHeightPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun initHeightPicker() {
        var heightValue: MutableList<String> = ArrayList()

        var j: Int = 0
        for (i in 140 until 191) {
            heightValue.add(i.toString())
            if (mHeight == i) {
                checkPosition = j
            }
            j++
        }

        mHeightPicker.refreshByNewDisplayedValues(heightValue.toTypedArray())

        mHeightPicker.value = checkPosition
    }

    fun getCurrentPosition (): Int {
        var position = mHeightPicker.getValue() - mHeightPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): String {
        val content: Array<String> = mHeightPicker.getDisplayedValues()
        var value = content[getCurrentPosition()]
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnHeightSelectListener?.OnHeightSelect(getCurrentPosition(),getCurrentContent())
                dismiss()
            }
        }
    }

}