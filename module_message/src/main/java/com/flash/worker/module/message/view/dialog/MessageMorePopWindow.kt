package com.flash.worker.module.message.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.message.R
import com.flash.worker.module.message.interfaces.OnMessageMoreListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageMorePopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class MessageMorePopWindow(context: Context?): AbsPopWindow(context), View.OnClickListener {

    var mOnMessageMoreListener: OnMessageMoreListener? = null

    override fun bindContentView() = R.layout.pop_message_more

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.mTvAllRead)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvAllRead -> {
                mOnMessageMoreListener?.OnAllRead()
                dismiss()
            }
        }
    }


}