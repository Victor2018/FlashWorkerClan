package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnCouponMoreListener


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CouponMorePopWindow
 * Author: Victor
 * Date: 2021/9/28 16:08
 * Description: 
 * -----------------------------------------------------------------
 */
class CouponMorePopWindow(context: Context?): AbsPopWindow(context), View.OnClickListener {

    var mOnCouponMoreListener: OnCouponMoreListener? = null

    override fun bindContentView() = R.layout.pop_coupon_more

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.mTvHistory)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvHistory -> {
                mOnCouponMoreListener?.OnCouponMore()
                dismiss()
            }
        }
    }


}