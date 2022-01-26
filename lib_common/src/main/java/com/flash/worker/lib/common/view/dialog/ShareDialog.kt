package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnShareListener
import kotlinx.android.synthetic.main.dlg_settlement_method.mTvCancel
import kotlinx.android.synthetic.main.dlg_share.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ShareDialog
 * Author: Victor
 * Date: 2021/1/6 11:56
 * Description: 
 * -----------------------------------------------------------------
 */
class ShareDialog(context: Context) : AbsBottomDialog(context), View.OnClickListener {
    val TAG = "ShareDialog"

    var mOnShareListener: OnShareListener? = null

    override fun bindContentView() = R.layout.dlg_share

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
//        wlp?.height = ((DeviceUtils.getDisplayMetrics().heightPixels * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        mTvCancel.setOnClickListener(this)
        mTvShareFriend.setOnClickListener(this)
        mTvShareFriendCircle.setOnClickListener(this)
        mTvShareQQ.setOnClickListener(this)
        mTvShareQZone.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                dismiss()
            }
            R.id.mTvShareFriend -> {
                mOnShareListener?.OnShareFriend()
                dismiss()
            }
            R.id.mTvShareFriendCircle -> {
                mOnShareListener?.OnShareFriendCircle()
                dismiss()
            }
            R.id.mTvShareQQ -> {
                mOnShareListener?.OnShareQQ()
                dismiss()
            }
            R.id.mTvShareQZone -> {
                mOnShareListener?.OnShareQZone()
                dismiss()
            }
        }
    }

}