package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnNameSettingListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ToastUtils
import kotlinx.android.synthetic.main.dlg_name_setting.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NameSettingDialog
 * Author: Victor
 * Date: 2021/10/25 10:16
 * Description: 
 * -----------------------------------------------------------------
 */
class NameSettingDialog (context: Context): AbsDialog(context), View.OnClickListener,
    DialogInterface.OnKeyListener {

    var mOnNameSettingListener: OnNameSettingListener? = null
    var mExitTime: Long = 0

    override fun bindContentView() = R.layout.dlg_name_setting

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
        mTvConfirm.setOnClickListener(this)
        setOnKeyListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvConfirm -> {
                val name = mEtName.text.toString()
                val inviteId = mEtInviteId.text.toString()
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.show("请输入闪工名")
                    return
                }
                mOnNameSettingListener?.OnNameSetting(name,inviteId)
                dismiss()
            }
        }
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - mExitTime < 2000) {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(0)
                } else {
                    mExitTime = System.currentTimeMillis()
                    ToastUtils.show("再按一次退出")
                }
                return true

            }
        }

        return false
    }

    fun clearInput() {
        mEtName?.setText("")
        mEtInviteId?.setText("")
    }

}