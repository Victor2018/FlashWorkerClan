package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.data.SettlementInfo
import com.flash.worker.lib.common.interfaces.OnSettlementMethodSelectListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_settlement_method.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementMethodDialog
 * Author: Victor
 * Date: 2021/1/6 11:56
 * Description: 
 * -----------------------------------------------------------------
 */
class SettlementMethodDialog(context: Context) : AbsBottomDialog(context), View.OnClickListener {
    val TAG = "SettlementMethodDialog"

    var mOnSettlementMethodSelectListener: OnSettlementMethodSelectListener? = null

    var settlementMethods: ArrayList<SettlementInfo>? = null

    override fun bindContentView() = R.layout.dlg_settlement_method

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        initSettlementMethodPicker()
        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }


    fun initSettlementMethodPicker() {
//        var settlementMethodValue = ResUtils.getStringArrayRes(R.array.hire_settlement_method)
        var settlementMethodValue = ArrayList<String>()

        for (item in settlementMethods!!) {
            settlementMethodValue.add(item.settlementName!!)
        }

        mSettlementMethodPicker.refreshByNewDisplayedValues(settlementMethodValue.toTypedArray())
    }

    fun getCurrentPosition (): Int {
        var position = mSettlementMethodPicker.getValue() - mSettlementMethodPicker.getMinValue()
        return position
    }

    fun getCurrentContent(): SettlementInfo? {
        val content: Array<String> = mSettlementMethodPicker.getDisplayedValues()
        var value = settlementMethods?.get(getCurrentPosition())
        return value
    }

    override fun onClick(v: View?) {
        when (v) {
            mTvCancel -> {
                dismiss()
            }
            mTvConfirm -> {
                mOnSettlementMethodSelectListener?.OnSettlementMethodSelect(getCurrentContent())
                dismiss()
            }
        }
    }

}