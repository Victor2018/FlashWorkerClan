package com.flash.worker.module.message.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.TextViewBoundsUtil
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.module.message.R
import com.flash.worker.module.message.interfaces.OnRecentContactMoreListener
import kotlinx.android.synthetic.main.dlg_recent_contact_more.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RecentContactMoreDialog
 * Author: Victor
 * Date: 2021/5/10 20:13
 * Description: 
 * -----------------------------------------------------------------
 */
class RecentContactMoreDialog(context: Context, var isStickyTagSet: Boolean = false)
    : AbsDialog(context), View.OnClickListener {

    var mOnRecentContactMoreListener: OnRecentContactMoreListener? = null

    override fun bindContentView() = R.layout.dlg_recent_contact_more

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.8).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        if (isStickyTagSet) {
            mTvStickyRecentContact.text = "取消置顶"
            mIvSticky.setImageResource(R.mipmap.ic_cancel_sticky_recent_contract)
        } else {
            mTvStickyRecentContact.text = "置顶该聊天"
            mIvSticky.setImageResource(R.mipmap.ic_sicky_recent_contract)
        }

        mLlStickyRecentContact.setOnClickListener(this)
        mLlDeleteRecentContact.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mLlStickyRecentContact -> {
                mOnRecentContactMoreListener?.OnRecentContactSticky()
                dismiss()
            }
            R.id.mLlDeleteRecentContact -> {
                mOnRecentContactMoreListener?.OnRecentContactDelete()
                dismiss()
            }
        }
    }
}