package com.flash.worker.module.mine.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.PhoneUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CancelAccountParm
import com.flash.worker.lib.coremodel.data.parm.CancelAccountVerifyParm
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.AuthVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.AccountVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.AuthVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.activity.CancelAccountSuccessActivity
import com.flash.worker.module.mine.view.activity.RealNameActivity
import kotlinx.android.synthetic.main.frag_cancel_account.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CancelAccountFragment
 * Author: Victor
 * Date: 2021/5/19 17:04
 * Description: 
 * -----------------------------------------------------------------
 */
class CancelAccountFragment: BaseFragment(),View.OnClickListener, OnCountDownTimerListener,
        TextWatcher, OnTradePasswordListener {
    
    companion object {

        fun newInstance(): CancelAccountFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): CancelAccountFragment {
            val fragment = CancelAccountFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var authVM: AuthVM
    private lateinit var accountVM: AccountVM
    private lateinit var userVM: UserVM

    var mLoadingDialog: LoadingDialog? = null
    var mSmsCountDownTimer: SmsCountDownTimer? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.frag_cancel_account

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        authVM = ViewModelProvider(this, AuthVMFactory(this))
                .get(AuthVM::class.java)

        accountVM = ViewModelProvider(this, AccountVMFactory(this))
                .get(AccountVM::class.java)

        userVM = ViewModelProvider(this, UserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)
        mSmsCountDownTimer = SmsCountDownTimer(60 * 1000, 1000, this)

        subscribeUi()

        mTvCode.setOnClickListener(this)
        mTvCancelAccount.setOnClickListener(this)

        mEtCode.addTextChangedListener(this)
    }

    fun initData () {
        val userInfo = App.get().getUserInfo()
        mTvPhone.text = PhoneUtil.blurPhone(userInfo?.phone)
    }

    fun subscribeUi () {
        authVM.verifySmsData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mSmsCountDownTimer?.start()
                    mEtCode.requestFocus()
                    ToastUtils.show("验证码发送成功")
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        accountVM.accountInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var hasTradePassword = it.value?.data?.hasTradePassword ?: false
                    if (!hasTradePassword) {
                        showSetTransactionPwdDlg()
                        return@Observer
                    }
                    getTradePasswordDialog().show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.cancelAccountVerifyData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    sendAccountInfoRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.cancelAccountData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    CancelAccountSuccessActivity.intentStart(activity as AppCompatActivity)
                    UMengEventModule.report(activity, MineEvent.cancel_account)
                    activity?.onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("151135",it.code)) {
                        if (passwordErrorCount >= 4) {
                            LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                            showErrorPasswordTipDlg()
                            passwordErrorCount = 0
                        }
                        LiveDataBus.send(JobActions.TRADE_PASSWORD_ERROR)
                        passwordErrorCount++
                        return@Observer
                    }
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendVerifySmsRequest () {
        var isRunning = mSmsCountDownTimer?.isRunning ?: false
        if (isRunning) return

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        authVM.sendVerifySms(token)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendCancelAccountVerifyRequest () {
        var code = mEtCode.text.toString()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show("请输入验证码")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CancelAccountVerifyParm()
        body.code = code

        userVM.cancelAccountVerify(token,body)
    }

    fun sendCancelAccountRequest (tradePassword: String?) {
        var code = mEtCode.text.toString()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show("请输入验证码")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CancelAccountParm()
        body.code = code
        body.tradePassword = tradePassword

        userVM.cancelAccount(token,body)
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(activity!!)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(activity!!)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已连续输错密码5次，\n" +
                "若连续输错密码10次 ，\n" +
                "您的帐户将被锁定2小时。"
        commonTipDialog.mCancelText = "忘记密码"
        commonTipDialog.mOkText = "重试"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                getTradePasswordDialog().show()
            }

            override fun OnDialogCancelClick() {
                var userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 0) {
                    showAuthTipDlg()
                    return
                }
                NavigationUtils.goVerifyIdentidyActivity(activity!!)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                RealNameActivity.intentStart(activity as AppCompatActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCode -> {
                sendVerifySmsRequest()
            }
            R.id.mTvCancelAccount -> {
                sendCancelAccountVerifyRequest()
            }
        }
    }

    override fun onTick(millisUntilFinished: Long) {
        //防止计时过程中重复点击
        if (!isAdded) return
        mTvCode.text = "${millisUntilFinished/1000}s"
        mTvCode.setTextColor(ResUtils.getColorRes(R.color.color_999999))
    }

    override fun onFinish() {
        //重新给Button设置文字
        if (!isAdded) return
        mTvCode.text = ""
        mTvCode.text = "获取验证码"
        mTvCode.setTextColor(ResUtils.getColorRes(R.color.color_F7A730))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mSmsCountDownTimer != null) {
            mSmsCountDownTimer?.onFinish()
            mSmsCountDownTimer = null
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val isEmpty = TextUtils.isEmpty(s)
        if (isEmpty) {
            mTvCancelAccount.setBackgroundResource(R.drawable.shape_eeeeee_radius_13)
        } else {
            mTvCancelAccount.setBackgroundResource(R.drawable.shape_ffd424_radius_13)
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendCancelAccountRequest(tradePassword)
    }


}