package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.BindAliPayParm
import com.flash.worker.lib.coremodel.data.parm.OpenUserUnBindParm
import com.flash.worker.lib.coremodel.data.req.OpenUserInfoReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.OpenUserVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.pay.PayHelper
import com.flash.worker.lib.pay.data.PayChannel
import com.flash.worker.lib.pay.interfaces.OnPayCompleteListener
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_withdraw_account_binding.*
import kotlinx.android.synthetic.main.activity_withdraw_account_binding.mIvBack

class WithdrawAccountBindingActivity : BaseActivity(),View.OnClickListener, OnPayCompleteListener,
        OnTradePasswordListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, WithdrawAccountBindingActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val openUserVM: OpenUserVM by viewModels {
        InjectorUtils.provideOpenUserVMFactory(this)
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mPayHelper: PayHelper? = null
    var mOpenUserInfoReq: OpenUserInfoReq? = null

    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.activity_withdraw_account_binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
        initData()
    }

    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mPayHelper = PayHelper(this,this)
        mIvBack.setOnClickListener(this)
        mClBindAccount.setOnClickListener(this)
    }

    fun initData () {
        sendOpenUserInfoRequest()
    }

    fun subscribeUi () {
        openUserVM.openUserInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showOpenUserInfo(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        openUserVM.aliAuthData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mLoadingDialog?.show()
                    mPayHelper?.sendPayRequestWithParms(PayHelper.BIND_ALI_PAY,it.value.data?.body!!)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        openUserVM.bindAliPayData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("绑定成功")
                    UMengEventModule.report(this, MineEvent.bind_zhb)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        openUserVM.openUserUnBindData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("解绑成功")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    UMengEventModule.report(this, MineEvent.unbind_zfb)
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
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendOpenUserInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        openUserVM.fetchOpenUserInfo(token)
    }

    fun sendAliAuthInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        openUserVM.fetchAliAuthInfo(token)
    }

    fun sendBindPayAccountRequest (payWay: Int,authCode: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = BindAliPayParm()
        if (payWay == PayHelper.ALI_PAY) {
            body.channel = PayChannel.ALI_PAY
        } else if (payWay == PayHelper.WX_PAY) {
            body.channel = PayChannel.WX_PAY
        }
        body.code = authCode
        openUserVM.bindAliPay(token,body)
    }

    fun sendUnBindPayAccountRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = OpenUserUnBindParm()
        body.channel = PayChannel.ALI_PAY
        body.tradePassword = tradePassword

        openUserVM.openUserUnBind(token,body)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showOpenUserInfo (data: OpenUserInfoReq) {
        mOpenUserInfoReq = data
        var isBindAlipay = data.data?.isBindAlipay ?: false
        if (isBindAlipay) {
            mTvBindStatus.text = "去解绑"
        } else {
            mTvBindStatus.text = "去绑定"
        }
    }

    fun showBindTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "是否绑定支付宝账号进行提现"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即绑定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAliAuthInfoRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showUnBindTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您是否确认解除绑定支付宝"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "解绑"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAccountInfoRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码，暂时不能体现哦~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                SetTransactionPasswordActivity.intentStart(this@WithdrawAccountBindingActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
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
                NavigationUtils.goVerifyIdentidyActivity(this@WithdrawAccountBindingActivity)
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                RealNameActivity.intentStart(this@WithdrawAccountBindingActivity)
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
            R.id.mClBindAccount -> {
                var isBindAlipay = mOpenUserInfoReq?.data?.isBindAlipay ?: false
                if (isBindAlipay) {
                    showUnBindTipDlg()
                    return
                }
                showBindTipDlg()
            }
        }
    }

    override fun OnPlayComplete(msg: String?, isPaySuccess: Boolean) {
    }

    override fun OnAuthComplete(msg: String?, isAuthSuccess: Boolean) {
        mLoadingDialog?.dismiss()
        if (!isAuthSuccess) {
            ToastUtils.show(msg)
            return
        }

        sendBindPayAccountRequest(PayHelper.ALI_PAY,msg)
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendUnBindPayAccountRequest(tradePassword)
    }

}