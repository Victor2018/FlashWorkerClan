package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnAreaPickerListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import kotlinx.android.synthetic.main.dlg_area_picker.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AreaPickerDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 地区选择dialog
 * -----------------------------------------------------------------
 */
class AreaPickerDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "AreaPickerDialog"
    var areaDatas: ArrayList<AreaInfo> = ArrayList()
    var mOnAreaPickerListener: OnAreaPickerListener? = null

    var mArea: String? = null
    var checkPosition: Int = 0

    override fun bindContentView() = R.layout.dlg_area_picker

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initAreaPicker()

        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun setAreaData (areas: ArrayList<AreaInfo>?) {
        areaDatas.clear()
        areas?.let { areaDatas.addAll(it) }
    }

    fun initAreaPicker () {
        var areaValue: MutableList<String> = ArrayList()

        var allCity = AreaInfo()
        allCity.name = "全市"
        areaDatas?.add(0,allCity)

        if (areaDatas != null && areaDatas?.size!! > 0) {
            for (i in 0 until areaDatas?.size!!) {
                areaValue.add(areaDatas?.get(i)?.name!!)
                if (TextUtils.equals(mArea,areaDatas?.get(i)?.name)) {
                    checkPosition = i
                }
            }
            mAreaPicker.refreshByNewDisplayedValues(areaValue.toTypedArray())

            Loger.e(TAG,"initAreaPicker-checkPosition = " + checkPosition)
            mAreaPicker.value = checkPosition
        }
    }


    fun getCurrentArea() : AreaInfo? {
        if (mAreaPicker.visibility == View.GONE) {
            return null
        }
        var areaPosition = mAreaPicker.getValue() - mAreaPicker.getMinValue()
        return areaDatas?.get(areaPosition)!!
    }


    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnAreaPickerListener?.OnAreaPicker(getCurrentArea())
                dismiss()
            }
        }
    }


}