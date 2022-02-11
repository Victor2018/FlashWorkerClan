package com.flash.worker.module.job.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.event.JobEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.SignUpConfirmDetailReq
import com.flash.worker.lib.coremodel.data.parm.SignUpParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.module.job.R
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(),View.OnClickListener,
    OnTradePasswordListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,releaseId: String?,
                          talentReleaseId: String?,resumeId: String?) {
            var intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            intent.putExtra(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            intent.putExtra(Constant.RESUME_ID_KEY,resumeId)
            activity.startActivity(intent)
        }
    }

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    var mSignUpConfirmDetailReq: SignUpConfirmDetailReq? = null

    var releaseId: String? = null//发布ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//简历ID
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.activity_sign_up

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    override fun onResume() {
        super.onResume()
        sendSignUpConfirmDetailRequest()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvSignUpNow.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        releaseId = intent?.getStringExtra(Constant.RELEASE_ID_KEY)
        talentReleaseId = intent?.getStringExtra(Constant.TALENT_RELEASE_ID_KEY)
        resumeId = intent?.getStringExtra(Constant.RESUME_ID_KEY)
    }

    fun subscribeUi() {
        employmentVM.signUpConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showSignUpConfimDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.signUpData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    SignUpSuccessActivity.intentStart(this)
                    onBackPressed()
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
                    ToastUtils.show(it.message)
                }
            }
        })

        accountVM.accountInfoData.observe(this, Observer {
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
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendSignUpConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchSignUpConfirmDetail(token,releaseId)
    }

    fun sendSignUpRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = SignUpParm()
        body.employerReleaseId = releaseId
        body.resumeId = resumeId

        if (TextUtils.isEmpty(talentReleaseId)) {
            body.source = 1
            UMengEventModule.report(this, JobEvent.talent_sign_up)
        } else {
            body.source = 2
            UMengEventModule.report(this, BussinessTalentEvent.talent_accepts_invitation)
            body.talentReleaseId = talentReleaseId
        }

        body.tradePassword = tradePassword

        employmentVM.signUp(token,body)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showSignUpConfimDetail (data: SignUpConfirmDetailReq) {
        mSignUpConfirmDetailReq = data

        var signUpAmt = AmountUtil.addCommaDots(data.data?.signupAmount)

        var settlementAmount = AmountUtil.addCommaDots(data.data?.settlementAmount)

        var settlementMethod = ""

        if (data.data?.settlementMethod == 1) {
            settlementMethod = "元/日结"
        } else if (data.data?.settlementMethod == 2) {
            settlementMethod = "元/周结"
        } else if (data.data?.settlementMethod == 3) {
            settlementMethod = "元/整单结"
        }

        mTvEmployerName.text = "${data.data?.employerName}-${data.data?.title}"
        mTvSettlementMethod.text = "$settlementAmount$settlementMethod"
        mTvSignUpAmt.text = "信用冻结：${signUpAmt}元"
        mTvSettlementAmount.text = "(${settlementAmount}X${data?.data?.frozenRate}%)"
        mTvAccountBalance.text = AmountUtil.addCommaDots(data.data?.availableBalance)

        var availableBalance = data.data?.availableBalance ?: 0.0
        var signupAmount = data.data?.signupAmount ?: 0.0

        if (availableBalance < signupAmount) {
            mTvSignUpNow.text = "余额充值"
        } else {
            mTvSignUpNow.text = "确认报名"
        }
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog =
            TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.tradeAmount = mSignUpConfirmDetailReq?.data?.signupAmount ?: 0.0
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
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
                NavigationUtils.goVerifyIdentidyActivity(this@SignUpActivity)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时不能修改提现密码哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@SignUpActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@SignUpActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvSignUpNow -> {
                var availableBalance = mSignUpConfirmDetailReq?.data?.availableBalance ?: 0.0
                var signupAmount = mSignUpConfirmDetailReq?.data?.signupAmount ?: 0.0
                if (availableBalance < signupAmount) {
                    NavigationUtils.goRechargeActivity(this)
                    return
                }
                sendAccountInfoRequest()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendSignUpRequest(tradePassword)
    }
}