package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.view.View
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.pop_platform_accessing.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PlatformAccessingPopWindow
 * Author: Victor
 * Date: 2021/1/5 16:53
 * Description: 
 * -----------------------------------------------------------------
 */
class PlatformAccessingPopWindow(
        context: Context?,
        var isEmployer: Boolean,
        var disputeType: Int)
    : AbsPopWindow(context) {

    init {
        handleWindow()
        initialzie()
    }

    override fun bindContentView() = R.layout.pop_platform_accessing

    override fun getWeightPercentage(): Double {
        return 0.0
    }

    override fun getHeightPercentage(): Double {
        return 0.0
    }

    override fun initView(view: View?) {
        var tip = ""
        if (disputeType == 1) {//人才举报
            if (isEmployer) {
                tip = "尊敬的雇主：\n\t\t\t\t平台介入后，若核实人才举报属实，您除了需要向人才进行赔付外，您的信用积分还将被扣掉10分。(同意诉求不扣分。)"
            } else {
                tip = "尊敬的人才：\n\t\t\t\t平台介入后，若核实您的举报不属实，您除了需要向雇主进行赔付外，您的信用积分还将被扣掉10分。(取消举报不扣分。)"
            }
        } else if (disputeType == 2) {//雇主投诉
            if (isEmployer) {
                tip = "尊敬的雇主：\n\t\t\t\t平台介入后，若核实您的投诉不属实，您除了需要向人才进行赔付外，您的信用积分还将被扣掉10分。(取消投诉不扣分。)"
            } else {
                tip = "尊敬的人才：\n\t\t\t\t平台介入后，若核实雇主投诉属实，您除了需要向雇主进行赔付外，您的信用积分还将被扣掉10分。(同意诉求不扣分。)"
            }
        }
        view?.mTvTip?.text = tip
    }


}