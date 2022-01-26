package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnCountDownTimerListener
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.util.PhoneUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.VerifyResetTradePwdParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.AuthVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_verify_identidy.*

@Route(path = ARouterPath.VerifyIdentidyAct)
class VerifyIdentidyActivity : BaseActivity(),View.OnClickListener, OnCountDownTimerListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, VerifyIdentidyActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    private val authVM: AuthVM by viewModels {
        InjectorUtils.provideAuthVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mSmsCountDownTimer: SmsCountDownTimer? = null

    override fun getLayoutResource() = R.layout.activity_verify_identidy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
        initData()
    }

    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mSmsCountDownTimer = SmsCountDownTimer(60 * 1000, 1000, this)

        mIvBack.setOnClickListener(this)
        mTvCode.setOnClickListener(this)
        mTvNext.setOnClickListener(this)
    }

    fun initData () {
        val userInfo = App.get().getUserInfo()
        mTvPhone.text = PhoneUtil.blurPhone(userInfo?.phone)
    }

    fun subscribeUi () {
        authVM.verifySmsData.observe(this, Observer {
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

        accountVM.verifyResetTradePwdData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var realName = mEtRealName.text.toString()
                    var idCard = mEtIdCard.text.toString()
                    var code = mEtCode.text.toString()
                    ReSetTransactionPasswordActivity.intentStart(this,realName,idCard,code)
                    onBackPressed()
                }
                is HttpResult.Error -> {
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

    fun sendVerifyResetTradePwdRequest (realName: String?,idCard: String?,code: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = VerifyResetTradePwdParm()
        body.realName = realName
        body.idcard = idCard
        body.code = code

        accountVM.verifyResetTradePwd(token,body)
    }

    override fun onTick(millisUntilFinished: Long) {
        //防止计时过程中重复点击
        if (isFinishing()) return
        mTvCode.text = "${millisUntilFinished/1000}s"
        mTvCode.setTextColor(ResUtils.getColorRes(R.color.color_999999))
    }

    override fun onFinish() {
        //重新给Button设置文字
        if (isFinishing()) return
        mTvCode.text = ""
        mTvCode.text = "获取验证码"
        mTvCode.setTextColor(ResUtils.getColorRes(R.color.color_F7A730))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvCode -> {
                sendVerifySmsRequest()
            }
            R.id.mTvNext -> {
                var realName = mEtRealName.text.toString()
                if (TextUtils.isEmpty(realName)) {
                    ToastUtils.show("请输入真实姓名")
                    return
                }
                var idCard = mEtIdCard.text.toString()
                if (TextUtils.isEmpty(idCard)) {
                    ToastUtils.show("请输入身份证号码")
                    return
                }
                var code = mEtCode.text.toString()
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.show("请输入验证码")
                    return
                }
                sendVerifyResetTradePwdRequest(realName,idCard,code)
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

}