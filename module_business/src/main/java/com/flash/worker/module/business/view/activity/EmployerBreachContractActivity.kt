package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.parm.FireTalentConfirmDetailParm
import com.flash.worker.lib.coremodel.data.req.FireTalentConfirmDetailReq
import com.flash.worker.lib.coremodel.data.parm.FireTalentParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_employer_breach_contract.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerBreachContractActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerBreachContractActivity: BaseActivity(),View.OnClickListener, OnTradePasswordListener {

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }
    private val employerJobVM: EmployerJobVM by viewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    var jobOrderId: String? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,jobOrderId: String?) {
            var intent = Intent(activity, EmployerBreachContractActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,jobOrderId)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_breach_contract

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {

        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvThinkAgain.setOnClickListener(this)
        mTvConfimBreach.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        jobOrderId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendFireTalentConfirmDetailRequest()
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

        employerJobVM.fireTalentConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showDismissalTalentConfirmDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.fireTalentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("已成功取消雇用")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    UMengEventModule.report(this, BussinessEmployerEvent.employer_breach_cancel)
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

    fun sendFireTalentConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = FireTalentConfirmDetailParm()
        body.jobOrderId = jobOrderId

        employerJobVM.fetchFireTalentConfirmDetail(token,body)
    }

    fun sendFireTalentRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = FireTalentParm()
        body.jobOrderId = jobOrderId
        body.tradePassword = tradePassword

        employerJobVM.fireTalent(token,body)
    }

    fun showDismissalTalentConfirmDetail (data: FireTalentConfirmDetailReq) {
        tv_confirm_tip.text = String.format("\t\t\t\t您确定要违约解雇%s吗？",data.data?.username)
        mTvCreditBreachAmount.text = AmountUtil.addCommaDots(data.data?.employmentFrozenAmount)
        mTvWorkBreachAmount.text = AmountUtil.addCommaDots(data.data?.totalSettledAmount)
        mTvServiceAmount.text = AmountUtil.addCommaDots(data.data?.totalServiceFeeAmount)
        mTvTotalAmount.text = AmountUtil.addCommaDots(data.data?.totalAmount)

        var totalSettledAmount: Double = data.data?.totalSettledAmount ?: 0.0
        if (totalSettledAmount > 0) {
            mTvWorkBreach.visibility = View.VISIBLE
            tv_work_breach_label.visibility = View.VISIBLE
            mTvWorkBreachAmount.visibility = View.VISIBLE
            tv_work_breach.visibility = View.VISIBLE
            tv_service_amount.visibility = View.VISIBLE
            mTvServiceAmount.visibility = View.VISIBLE
            tv_service_amount_unit.visibility = View.VISIBLE
        } else {
            mTvWorkBreach.visibility = View.GONE
            tv_work_breach_label.visibility = View.GONE
            mTvWorkBreachAmount.visibility = View.GONE
            tv_work_breach.visibility = View.GONE
            tv_service_amount.visibility = View.GONE
            mTvServiceAmount.visibility = View.GONE
            tv_service_amount_unit.visibility = View.GONE
        }
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
                NavigationUtils.goVerifyIdentidyActivity(this@EmployerBreachContractActivity)
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
                NavigationUtils.goRealNameActivity(this@EmployerBreachContractActivity)
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
                NavigationUtils.goSetTransactionPasswordActivity(this@EmployerBreachContractActivity)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvThinkAgain -> {
                onBackPressed()
            }
            R.id.mTvConfimBreach -> {
                sendAccountInfoRequest()
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendFireTalentRequest(tradePassword)
    }

}