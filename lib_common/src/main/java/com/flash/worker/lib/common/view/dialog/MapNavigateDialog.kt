package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnMapNavigateListener
import kotlinx.android.synthetic.main.dlg_map_navigate.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MapNavigateDialog
 * Author: Victor
 * Date: 2020/12/16 19:26
 * Description: 导航dialog
 * -----------------------------------------------------------------
 */
class MapNavigateDialog(context: Context) : AbsBottomDialog(context),View.OnClickListener {
    val TAG = "MapNavigateDialog"

    var mOnMapNavigateListener: OnMapNavigateListener? = null

    override fun bindContentView() = R.layout.dlg_map_navigate

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
//        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvGaodeMap.setOnClickListener(this)
        mTvBaiduMap.setOnClickListener(this)
        mTvTentcentMap.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvGaodeMap -> {
                mOnMapNavigateListener?.onGaodeNavigate()
                dismiss()
            }
            R.id.mTvBaiduMap -> {
                mOnMapNavigateListener?.onBaiduNavigate()
                dismiss()
            }
            R.id.mTvTentcentMap -> {
                mOnMapNavigateListener?.onTentcentNavigate()
                dismiss()
            }
        }
    }

}