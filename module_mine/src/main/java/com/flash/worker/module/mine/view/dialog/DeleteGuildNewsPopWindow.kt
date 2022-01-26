package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnGuildNewsDeleteListener

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TerminationEmployPopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class DeleteGuildNewsPopWindow(context: Context?): AbsPopWindow(context), View.OnClickListener {

    var mOnGuildNewsDeleteListener: OnGuildNewsDeleteListener? = null

    override fun bindContentView() = R.layout.pop_delete_guild_news

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        view?.findViewById<TextView>(R.id.mTvDeleteGuildNews)?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDeleteGuildNews -> {
                mOnGuildNewsDeleteListener?.OnGuildNewsDelete()
                dismiss()
            }
        }
    }


}