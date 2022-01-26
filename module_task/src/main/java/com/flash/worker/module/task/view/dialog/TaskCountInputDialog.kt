package com.flash.worker.module.task.view.dialog

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.module.task.R
import com.flash.worker.module.task.interfaces.OnTaskCountInputListener
import kotlinx.android.synthetic.main.dlg_task_count_input.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TaskCountInputDialog
 * Author: Victor
 * Date: 2021/12/2 10:19
 * Description: 
 * -----------------------------------------------------------------
 */
class TaskCountInputDialog(context: Context): AbsDialog(context), View.OnClickListener {

    var maxCount: Int = 0
    var mTitleTxt: String? = null

    var mOnTaskCountInputListener: OnTaskCountInputListener? = null

    override fun bindContentView() = R.layout.dlg_task_count_input

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.8).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        tv_title.text = mTitleTxt
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvConfirm -> {
                var count = mEtCount.text.toString()
                if (TextUtils.isEmpty(count)) {
                    ToastUtils.show("输入领取件数")
                    return
                }
                if (count.toInt() > maxCount) {
                    ToastUtils.show("领取件数不能大于${maxCount}件")
                    return
                }
                if (count.toInt() <= 0) {
                    ToastUtils.show("领取件数至少1件")
                    return
                }
                mOnTaskCountInputListener?.OnTaskCountInput(count.toInt())
                dismiss()
            }
        }
    }
}