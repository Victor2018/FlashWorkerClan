package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnBreachContractFiredListener
import com.flash.worker.module.business.interfaces.OnRefuseEmployListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BreachContractFiredPopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class BreachContractFiredPopWindow(context: Context?): AbsPopWindow(context), View.OnClickListener {

    var mOnBreachContractFiredListener: OnBreachContractFiredListener? = null

    override fun bindContentView() = R.layout.pop_breach_contract_fired

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.mTvBreachContractFired)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvBreachContractFired -> {
                mOnBreachContractFiredListener?.OnBreachContractFired()
                dismiss()
            }
        }
    }
}