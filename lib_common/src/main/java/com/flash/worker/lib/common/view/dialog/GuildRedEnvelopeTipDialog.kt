package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnJoinGuildListener
import com.flash.worker.lib.common.util.DensityUtil
import kotlinx.android.synthetic.main.dlg_guild_red_envelope_tip.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: GuildRedEnvelopeTipDialog
 * Author: Victor
 * Date: 2020/12/18 12:12
 * Description: 引导加入公会领取红包弹窗
 * -----------------------------------------------------------------
 */
class GuildRedEnvelopeTipDialog(context: Context): AbsDialog(context),View.OnClickListener {

    var mOnJoinGuildListener: OnJoinGuildListener? = null

    override fun bindContentView() = R.layout.dlg_guild_red_envelope_tip

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.8).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        setCanceledOnTouchOutside(false)
        mIvClose.setOnClickListener(this)
        mIvJoinGuild.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvClose -> {
                dismiss()
            }
            mIvJoinGuild -> {
                mOnJoinGuildListener?.OnJoinGuild()
                dismiss()
            }
        }
    }
}