package com.flash.worker.module.login.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.login.R
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.util.SharedPreferencesUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.LoginData
import com.flash.worker.lib.coremodel.data.parm.BindPhoneParm
import com.flash.worker.lib.coremodel.data.parm.SmsParm
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.viewmodel.AuthVM
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.livedatabus.action.*
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.activity_bind_phone.mBtnLogin
import kotlinx.android.synthetic.main.activity_bind_phone.mEtCode
import kotlinx.android.synthetic.main.activity_bind_phone.mEtPhone
import kotlinx.android.synthetic.main.activity_bind_phone.mIvBack
import kotlinx.android.synthetic.main.activity_bind_phone.mTvSendSms
import kotlinx.android.synthetic.main.activity_bind_phone.mTvTime


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BindPhoneActivity
 * Author: Victor
 * Date: 2020/12/10 19:37
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.BindPhoneAct)
class BindPhoneActivity: BaseActivity(),View.OnClickListener, OnCountDownTimerListener,TextWatcher {

     companion object {
         fun intentStart(activity: AppCompatActivity, openId: String?) {
             var intent = Intent(activity, BindPhoneActivity::class.java)
             intent.putExtra(Constant.INTENT_DATA_KEY, openId)
             activity.startActivity(intent)
         }
     }

    var mSmsCountDownTimer: SmsCountDownTimer? = null
    var mLoadingDialog: LoadingDialog? = null

    var openId: String? = null

    private val authVM: AuthVM by viewModels {
        InjectorUtils.provideAuthVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_bind_phone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        mIvBack.setOnClickListener(this)
        mTvSendSms.setOnClickListener(this)
        mBtnLogin.setOnClickListener(this)
        mEtCode.addTextChangedListener(this)

        mSmsCountDownTimer = SmsCountDownTimer(60 * 1000, 1000, this)

        mLoadingDialog = LoadingDialog(this)

    }

    fun initData (intent: Intent?) {
        openId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
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

        authVM.bindPhoneData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var loginReq = LoginReq()
                    var loginData = LoginData()
                    loginData.token = it.value.data?.token
                    loginData.userId = it.value.data?.userId
                    loginReq.data = loginData

                    App.get().setLoginReq(loginReq)
                    SharedPreferencesUtils.setJPushAlias = false
                    App.get().resetLocation()

                    sendUserInfoRequest(true)

                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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

        userVM.imLoginInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var loginInfo = LoginInfo(it.value.data?.imAccid,it.value.data?.imToken)
                    NimMessageManager.instance.login(loginInfo)

                    ToastUtils.show("登录成功")
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
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

    fun sendBindPhoneRequest () {
        var phone = mEtPhone.text.toString()
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(R.string.login_enter_phone_tip)
            return
        }
        var code = mEtCode.text.toString()
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show("请输入验证码")
            return
        }

        mLoadingDialog?.show()

        var body = BindPhoneParm()
        body.phone = phone
        body.code = code
        body.openId = openId

        authVM.bindPhone(body)
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

    fun sendImLoginInfoRequest (userId: String?) {
        if (!App.get().hasLogin()) return
        mLoadingDialog?.dismiss()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun loginNim () {
        if (!NimMessageManager.instance.hasLogin()) {
            var loginData = App.get().getLoginReq()?.data
            sendImLoginInfoRequest(loginData?.userId)
        } else {
            ToastUtils.show("登录成功")
            onBackPressed()
        }
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
                sendBindPhoneRequest()
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}