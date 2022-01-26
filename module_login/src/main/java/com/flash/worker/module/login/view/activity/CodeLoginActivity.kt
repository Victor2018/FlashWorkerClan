package com.flash.worker.module.login.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.LoginEvent
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.parm.OneKeyLoginParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.SmsLoginParm
import com.flash.worker.lib.coremodel.data.parm.SmsParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.login.R
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener
import com.flash.worker.module.login.interfaces.OnOneKeyLoginListener
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.SharedPreferencesUtils
import com.flash.worker.lib.coremodel.viewmodel.AuthVM
import com.flash.worker.lib.livedatabus.action.*
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_code_login.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LoginActivity
 * Author: Victor
 * Date: 2020/12/1 12:11
 * Description: 
 * -----------------------------------------------------------------
 */

@Route(path = ARouterPath.CodeLoginAct)
class CodeLoginActivity: BaseActivity(), View.OnClickListener, OnCountDownTimerListener, TextWatcher,
    OnOneKeyLoginListener {

    var mSmsCountDownTimer: SmsCountDownTimer? = null
    var mLoadingDialog: LoadingDialog? = null

    private val authVM: AuthVM by viewModels {
        InjectorUtils.provideAuthVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, CodeLoginActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_code_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        subscribeEvent()
        subscribeUi()
        mIvBack.setOnClickListener(this)
        mTvSendSms.setOnClickListener(this)
        mBtnLogin.setOnClickListener(this)
        mTvOneKeyLogin.setOnClickListener(this)
        mTvWechatLogin.setOnClickListener(this)
        mEtCode.addTextChangedListener(this)

        mSmsCountDownTimer = SmsCountDownTimer(60 * 1000, 1000, this)
        mLoadingDialog = LoadingDialog(this)

        mTvPrivacyAgreement.movementMethod = LinkMovementMethod.getInstance()

        SpannableUtil.stripUnderlines(mTvPrivacyAgreement)

    }

    fun subscribeEvent() {
        LiveDataBus.withStickiness(Constant.Msg.UPGRADE_APP)
            .observe(this, Observer {
            })
    }

    fun subscribeUi() {
        authVM.smsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mSmsCountDownTimer?.start()
                    mEtCode.requestFocus()
                    ToastUtils.show(R.string.login_get_ver_code_successful)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        authVM.smsLoginData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setLoginReq(it.value)
                    ToastUtils.show("登录成功")

                    SharedPreferencesUtils.setJPushAlias = false

                    App.get().resetLocation()

                    UMengEventModule.report(this, LoginEvent.phone_code_login)

                    sendUserInfoRequest(true)
                }
                is HttpResult.Error -> {
                    if(TextUtils.equals("001065",it.code)) {
                        AccountFrozenActivity.intentStart(this,it.message)
                        return@Observer
                    }
                    if(TextUtils.equals("001080",it.code)) {
                        AccountCancelledActivity.intentStart(this,it.message)
                        return@Observer
                    }

                    ToastUtils.show(it.message)
                }
            }
        })

        authVM.oneKeyLoginData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setLoginReq(it.value)
                    SharedPreferencesUtils.setJPushAlias = false
                    App.get().resetLocation()

                    sendUserInfoRequest(true)
                }
                is HttpResult.Error -> {
                    if(TextUtils.equals("001065",it.code)) {
                        AccountFrozenActivity.intentStart(this,it.message)
                        return@Observer
                    }
                    if(TextUtils.equals("001080",it.code)) {
                        AccountCancelledActivity.intentStart(this,it.message)
                        return@Observer
                    }

                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.userInfoData.observeForever {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(it.value.data)
                    LiveDataBus.send(LoginActions.LOGIN_SUCCESS)
                    LiveDataBus.send(TalentActions.REFRESH_TALENT_DETAIL)
                    LiveDataBus.send(JobActions.REFRESH_JOB_DETAIL)
                    LiveDataBus.send(JobActions.REFRESH_GUILD_DETAIL)
                    LiveDataBus.send(TaskActions.REFRESH_TASK_LIST)
                    LiveDataBus.send(TalentActions.CHECK_GUILD_RED_ENVELOPE)
                    LiveDataBus.send(MineActions.REFRESH_LOGIN_STATUS,true)

                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        }

    }

    fun sendSmsRequest () {
        var isRunning = mSmsCountDownTimer?.isRunning ?: false
        if (isRunning) return

        var phone = mEtPhone.text.toString()
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(R.string.login_enter_phone_tip)
            return
        }
        var body = SmsParm()
        body.phone = phone

        mLoadingDialog?.show()
        authVM.fetchSmsData(body)
    }

    fun sendSmsLoginRequest () {
        var phone = mEtPhone.text.toString()
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(R.string.login_enter_phone_tip)
            return
        }
        var code = mEtCode.text.toString()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(R.string.login_enter_verification_code)
            return
        }
        if (!mChkCheck.isChecked) {
            ToastUtils.show("请先阅读服务协议")
            return
        }
        var body = SmsLoginParm()
        body.code = code
        body.phone = phone

        mLoadingDialog?.show()
        authVM.smsLogin(body)
    }

    fun sendOneKeyLoginRequest (accessToken: String?) {
        if (TextUtils.isEmpty(accessToken)) {
            return
        }
        mLoadingDialog?.show()

        var body = OneKeyLoginParm()
        body.accessToken = accessToken

        authVM.oneKeyLogin(body)
    }

    fun sendUserInfoRequest (showLoading: Boolean) {
        if (!App.get().hasLogin()) return

        if (showLoading) {
            mLoadingDialog?.show()
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.fetchUserInfo(token)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvSendSms -> {
                sendSmsRequest()
            }
            R.id.mBtnLogin -> {
                sendSmsLoginRequest()
            }
            R.id.mTvOneKeyLogin -> {
                LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
                onBackPressed()
            }
            R.id.mTvWechatLogin -> {
                if (!mChkCheck.isChecked) {
                    ToastUtils.show("请先阅读服务协议")
                    return
                }
                LiveDataBus.send(LoginActions.GO_WECHAT_LOGIN)
                onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mSmsCountDownTimer != null) {
            mSmsCountDownTimer?.onFinish()
            mSmsCountDownTimer = null
        }
    }

    override fun onTick(millisUntilFinished: Long) {
        //防止计时过程中重复点击
        if (isFinishing()) return
        mTvTime.text = "${millisUntilFinished/1000}s"
        mTvSendSms.setText(R.string.login_after_get)
        mTvSendSms.setTextColor(ResUtils.getColorRes(R.color.color_999999))
    }

    override fun onFinish() {
        //重新给Button设置文字
        if (isFinishing()) return
        mTvTime.text = ""
        mTvSendSms.setText(R.string.login_get_verification_code)
        mTvSendSms.setTextColor(ResUtils.getColorRes(R.color.color_060D11))

    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val isEmpty = TextUtils.isEmpty(s)
        if (isEmpty) {
            mBtnLogin.setBackgroundResource(R.drawable.bg_f5eeeeee_shape_radius_10)
        } else {
            mBtnLogin.setBackgroundResource(R.drawable.bg_f4cc28_shape_radius_10)
        }
    }

    override fun OnOneKeyLogin(success: Boolean, message: String?) {
        if (!success) {
            ToastUtils.show(message)
            return
        }
        sendOneKeyLoginRequest(message)
    }

}