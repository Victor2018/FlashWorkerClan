package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.widget.MNPasswordEditText
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.coremodel.util.CryptoUtils
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.dlg_trade_password.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawDialog
 * Author: Victor
 * Date: 2021/1/15 15:26
 * Description: 交易密码输入 dialog
 * -----------------------------------------------------------------
 */
class TradePasswordDialog(context: Context): AbsDialog(context), View.OnClickListener,
        MNPasswordEditText.OnTextChangeListener {

    val TAG = "TradePasswordDialog"
    var tradeAmount: Double = 0.0//交易金额
    var mOnTradePasswordListener: OnTradePasswordListener? = null
    var showAmount: Boolean = true//是否显示交易金额

    override fun bindContentView() = R.layout.dlg_trade_password

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.9).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        subscribeEvent()

        mIvClose.setOnClickListener(this)
        mPasswordView.setOnTextChangeListener(this)

        if (showAmount) {
            mLlAmt.visibility = View.VISIBLE
        } else {
            mLlAmt.visibility = View.GONE
        }

        tradeAmount = AmountUtil.getRoundUpDouble(tradeAmount,2)

        mTvAmount.text = tradeAmount.toString()

        requestPasswordViewFocus()

    }

    fun subscribeEvent() {
        var activity = AppUtil.scanForActivity(context)
        if (activity == null) return

        LiveDataBus.with(JobActions.TRADE_PASSWORD_ERROR)
            .observe(activity!!, Observer {
                mTvPasswordError.visibility = View.VISIBLE
                mPasswordView.text.clear()
                requestPasswordViewFocus()
            })
        LiveDataBus.with(JobActions.DISMISS_TRADE_PASSWORD_DLG)
            .observe(activity!!, Observer {
                closeAction()
            })
    }

    fun requestPasswordViewFocus () {
        //设置可获得焦点
        mPasswordView.setFocusable(true)
        mPasswordView.setFocusableInTouchMode(true)
        //请求获得焦点
        mPasswordView.requestFocus()

        mPasswordView.postDelayed(Runnable {
            KeyBoardUtil.showKeyBoard(context,mPasswordView)
        },300)
    }

    override fun onClick(v: View?) {
       when (v?.id) {
           R.id.mIvClose -> {
               closeAction()
           }
       }
    }

    override fun onTextChange(text: String?, isComplete: Boolean) {
        Loger.e(TAG, "onTextChange-isComplete = $isComplete")
        Loger.e(TAG, "onTextChange-text = $text")

        if (isComplete) {
            mPasswordView.clearFocus()
            mPasswordView.setFocusable(false)
            mPasswordView.setFocusableInTouchMode(false)

            KeyBoardUtil.hideKeyBoard(context,mPasswordView)
            var tradePassword = CryptoUtils.MD5(text!!)
            mOnTradePasswordListener?.OnTradePassword(tradeAmount,tradePassword)

//            dismiss()
        }
    }

    fun closeAction () {
        mPasswordView.clearFocus()
        mPasswordView.setFocusable(false)
        mPasswordView.setFocusableInTouchMode(false)

        KeyBoardUtil.hideKeyBoard(context,mPasswordView)

        dismiss()
    }


}