package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WithdrawConfirmDetailData
import com.flash.worker.lib.coremodel.data.parm.WithdrawConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.WithdrawParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.OpenUserVM
import com.flash.worker.lib.pay.data.PayChannel
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.dialog.WithdrawDialog
import com.flash.worker.module.mine.view.interfaces.OnTransactionPasswordInputListener
import kotlinx.android.synthetic.main.activity_withdraw.*

class WithdrawActivity : BaseActivity(), View.OnClickListener, OnTransactionPasswordInputListener,
    TextView.OnEditorActionListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity,withdrawAmt: Double?) {
            var intent = Intent(activity, WithdrawActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,withdrawAmt)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    private val openUserVM: OpenUserVM by viewModels {
        InjectorUtils.provideOpenUserVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mWithdrawAmt: Double = 0.0
    var mWithdrawConfirmDetailData: WithdrawConfirmDetailData? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.activity_withdraw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    override fun onResume() {
        super.onResume()
        sendOpenUserInfoRequest()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvWithdrawHistory.setOnClickListener(this)
        mTvExtractAll.setOnClickListener(this)
        mTvWithdraw.setOnClickListener(this)

        mEtWithdrawAmount.setOnEditorActionListener(this)
    }

    fun initData (intent: Intent?) {
        mWithdrawAmt = intent?.getDoubleExtra(Constant.INTENT_DATA_KEY,0.0) ?: 0.0
        mTvAvailableCashAmount.text = "可提现金额${AmountUtil.addCommaDots(mWithdrawAmt)}"

        var filter = MoneyInputFilter()
        filter.setMaxValue(mWithdrawAmt)
        mEtWithdrawAmount.filters = arrayOf(filter)
    }

    fun subscribeUi () {
        accountVM.accountInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var hasTradePassword = it.value?.data?.hasTradePassword ?: false
                    if (!hasTradePassword) {
                        showSetTransactionPwdDlg()
                        return@Observer
                    }
                    sendWithdrawConfirmDetailRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        openUserVM.openUserInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var isBindAlipay = it.value.data?.isBindAlipay ?: false
                    if (!isBindAlipay) {
                        showBindAliAccountTipDlg()
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        accountVM.withdrawConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mWithdrawConfirmDetailData = it.value.data
                    showWithdrawDialog()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        accountVM.withdrawData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("提现成功")
                    UMengEventModule.report(this, MineEvent.withdraw_to_zfb)
//                    WithdrawHistoryActivity.intentStart(this)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("151135",it.code)) {
                        if (passwordErrorCount >= 4) {
                            showErrorPasswordTipDlg()
                            passwordErrorCount = 0
                        }
                        passwordErrorCount++
                        ToastUtils.show(it.message.toString())
                        return@Observer
                    }
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendOpenUserInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        openUserVM.fetchOpenUserInfo(token)
    }

    fun sendWithdrawConfirmDetailRequest () {
        var withdrawAmt = mEtWithdrawAmount.text.toString()
        if (TextUtils.isEmpty(withdrawAmt)) {
            ToastUtils.show("请输入提现金额")
            return
        }

        if (withdrawAmt?.replace(",","").toDouble() == 0.0) {
            ToastUtils.show("提现金额输入错误，请重新输入")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = WithdrawConfirmDetailParm()
        body.withdrawAmount = withdrawAmt?.replace(",","").toDouble()
        body.channel = PayChannel.ALI_PAY

        accountVM.fetchWithdrawConfirmDetail(token,body)
    }

    fun sendWithdrawRequest (data: WithdrawConfirmDetailData?, tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = WithdrawParm()
        body.withdrawAmount = data?.withdrawAmount ?: 0.00
        body.channel = PayChannel.ALI_PAY
        body.tradePassword = tradePassword

        accountVM.withdraw(token,body)
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码，暂时不能体现哦~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@WithdrawActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showBindAliAccountTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未绑定支付宝账号，是否现在绑定？"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即绑定"
        commonTipDialog.setCanceledOnTouchOutside(false)
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                WithdrawAccountBindingActivity.intentStart(this@WithdrawActivity)
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    fun showWithdrawDialog () {
        var mWithdrawDialog = WithdrawDialog(this)
        mWithdrawDialog.mWithdrawConfirmDetailData = mWithdrawConfirmDetailData

        mWithdrawDialog.mOnTransactionPasswordInputListener = this
        mWithdrawDialog.show()
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
                showWithdrawDialog()
            }

            override fun OnDialogCancelClick() {
                var userInfo = App.get().getUserInfo()
                if (userInfo?.realNameStatus == 0) {
                    showAuthTipDlg()
                    return
                }
                VerifyIdentidyActivity.intentStart(this@WithdrawActivity)
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
                RealNameActivity.intentStart(this@WithdrawActivity)
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
            R.id.mTvWithdrawHistory -> {
                WithdrawHistoryActivity.intentStart(this)
            }
            R.id.mTvExtractAll -> {
                mEtWithdrawAmount.setText(AmountUtil.addCommaDots(mWithdrawAmt))
                mEtWithdrawAmount.setSelection(mEtWithdrawAmount.length())
            }
            R.id.mTvWithdraw -> {
                withdrawAction()
            }
        }
    }

    fun withdrawAction () {
        sendAccountInfoRequest()
    }

    override fun OnTransactionPasswordInput(data: WithdrawConfirmDetailData?, tradePassword: String?) {
        sendWithdrawRequest(data,tradePassword)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            withdrawAction()
            return true
        }
        return false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}