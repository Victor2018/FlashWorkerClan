package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.util.ToastUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LoadingDialog
 * Author: Victor
 * Date: 2020/12/10 20:50
 * Description: 
 * -----------------------------------------------------------------
 */
class LoadingDialog(context: Context): AbsDialog(context), DialogInterface.OnKeyListener {

    var mExitTime: Long = 0

    override fun bindContentView() = R.layout.dlg_loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(true)
        setOnKeyListener(this)
    }

    override fun handleWindow(window: Window) {
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - mExitTime < 2000) {
                    App.get().mCurrentActivity?.get()?.finish()
                } else {
                    mExitTime = System.currentTimeMillis()
                    ToastUtils.show("再按一次退出")
                }
                return true

            }
        }

        return false
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}