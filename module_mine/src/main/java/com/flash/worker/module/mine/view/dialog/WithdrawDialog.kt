package com.flash.worker.module.mine.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.AbsDialog
import com.flash.worker.lib.common.view.widget.MNPasswordEditText
import com.flash.worker.lib.coremodel.data.bean.WithdrawConfirmDetailData
import com.flash.worker.lib.coremodel.util.CryptoUtils
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.interfaces.OnTransactionPasswordInputListener
import kotlinx.android.synthetic.main.dlg_withdraw.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: WithdrawDialog
 * Author: Victor
 * Date: 2021/1/15 15:26
 * Description: 提现dialog
 * -----------------------------------------------------------------
 */
class WithdrawDialog(context: Context): AbsDialog(context), View.OnClickListener,
        MNPasswordEditText.OnTextChangeListener {

    val TAG = "WithdrawDialog"

    var mWithdrawConfirmDetailData: WithdrawConfirmDetailData? = null

    var mOnTransactionPasswordInputListener: OnTransactionPasswordInputListener? = null

    override fun bindContentView() = R.layout.dlg_withdraw

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
        mIvClose.setOnClickListener(this)
        mPasswordView.setOnTextChangeListener(this)

        mTvAmount.text = AmountUtil.addCommaDots(mWithdrawConfirmDetailData?.actualAmount)
        mTvServiceAmt.text = AmountUtil.addCommaDots(mWithdrawConfirmDetailData?.feeAmount)
        tv_withdraw_rate.text = "第三方支付服务费(${mWithdrawConfirmDetailData?.withdrawRate}%)："

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
               mPasswordView.clearFocus()
               mPasswordView.setFocusable(false)
               mPasswordView.setFocusableInTouchMode(false)

               KeyBoardUtil.hideKeyBoard(context,mPasswordView)

               dismiss()
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
            var tradePassword = CryptoUtils.MD5(text ?: "")
            mOnTransactionPasswordInputListener?.OnTransactionPasswordInput(mWithdrawConfirmDetailData,tradePassword)
            dismiss()
        }
    }


}