package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitEmployInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TalentUserInfo
import com.flash.worker.lib.coremodel.data.parm.EmployConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.EmployerEmploymentParm
import com.flash.worker.lib.coremodel.data.req.EmployConfirmDetailReq
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.EmployUserAdapter
import kotlinx.android.synthetic.main.activity_employment_bond.*

class EmploymentBondActivity : BaseActivity(),View.OnClickListener, OnTradePasswordListener,
    AdapterView.OnItemClickListener {

    companion object {
        fun intentStart (
            activity: AppCompatActivity,
            body: EmployConfirmDetailParm?,
            data: EmployerWaitEmployInfo?,
            talentUserInfo: TalentUserInfo?) {
            var intent = Intent(activity, EmploymentBondActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,body)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,data)
            intent.putExtra(Constant.TALENT_USER_INFO_KEY,talentUserInfo)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mEmployUserAdapter: EmployUserAdapter? = null

    var mEmployConfirmDetailParm: EmployConfirmDetailParm? = null
    var mEmployerWaitEmployInfo: EmployerWaitEmployInfo? = null
    var mTalentUserInfo: TalentUserInfo? = null
    var mEmployConfirmDetailReq: EmployConfirmDetailReq? = null

    var passwordErrorCount: Int = 0//密码输入错误次数


    override fun getLayoutResource() = R.layout.activity_employment_bond

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mEmployUserAdapter = EmployUserAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployUserAdapter,mRvEmployTalent)

        mRvEmployTalent.adapter = animatorAdapter

        mIvBack.setOnClickListener(this)
        mTvConfirmAdmission.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mEmployConfirmDetailParm = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployConfirmDetailParm?
        mEmployerWaitEmployInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as EmployerWaitEmployInfo?
        mTalentUserInfo = intent?.getSerializableExtra(Constant.TALENT_USER_INFO_KEY)
                as TalentUserInfo?

        sendEmployConfirmDetailRequest()
    }

    fun subscribeUi() {
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

        employmentVM.employConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showEmployConfimDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.employerEmploymentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(BusinessActions.REFRESH_E_SIGNED_UP_USER)
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    EmploySuccessActivity.intentStart(this)
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

    fun sendEmployConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchEmployConfirmDetail(token,mEmployConfirmDetailParm)
    }

    fun sendEmployerEmploymentRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerEmploymentParm()
        body.employerReleaseId = mEmployConfirmDetailParm?.employerReleaseId
        body.jobOrderIds = mEmployConfirmDetailParm?.jobOrderIds
        body.tradePassword = tradePassword

        employmentVM.employerEmployment(token,body)
    }

    fun showEmployConfimDetailData (data: EmployConfirmDetailReq) {
        mEmployConfirmDetailReq = data

        if (data.data?.settlementMethod == 1) {
            mEmployUserAdapter?.salaryTxt = "${data.data?.settlementAmount}元/日"
        } else if (data.data?.settlementMethod == 2) {
            mEmployUserAdapter?.salaryTxt = "${data.data?.settlementAmount}元/周"
        } else if (data.data?.settlementMethod == 3) {
            mEmployUserAdapter?.salaryTxt = "${data.data?.settlementAmount}元/单"
        }

        mTvEmployCount.text = "${data.data?.count}"
        tv_credit_freeze_rate.text = "信用冻结(${data.data?.frozenRate}%)"
        mTvCreditFreezeAmount.text = AmountUtil.addCommaDots(data.data?.totalCreditFrozenAmount)
        mTvAccountBalance.text = AmountUtil.addCommaDots(data.data?.availableBalance)

        mEmployUserAdapter?.clear()
        mEmployUserAdapter?.add(data?.data?.employmentUsers)
        mEmployUserAdapter?.notifyDataSetChanged()

        var availableBalance = data.data?.availableBalance ?: 0.0
        var employmentAmount = data.data?.employmentAmount ?: 0.0
        if (availableBalance < employmentAmount) {
            mTvConfirmAdmission.text = "余额充值"
        } else {
            mTvConfirmAdmission.text = "确认雇用"
        }

    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(this)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.tradeAmount = mEmployConfirmDetailReq?.data?.employmentAmount ?: 0.0
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@EmploymentBondActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "交易密码错误，请重试。"
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
                NavigationUtils.goVerifyIdentidyActivity(this@EmploymentBondActivity)
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
                NavigationUtils.goRealNameActivity(this@EmploymentBondActivity)
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
            R.id.mTvConfirmAdmission -> {
                var availableBalance = mEmployConfirmDetailReq?.data?.availableBalance ?: 0.0
                var employmentAmount = mEmployConfirmDetailReq?.data?.employmentAmount ?: 0.0
                if (availableBalance < employmentAmount) {
                    NavigationUtils.goRechargeActivity(this)
                    return
                }
                sendAccountInfoRequest()
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendEmployerEmploymentRequest(tradePassword)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}