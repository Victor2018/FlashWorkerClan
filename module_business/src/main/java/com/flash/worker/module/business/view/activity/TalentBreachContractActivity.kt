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
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.CancelJobConfirmDetailData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CancelJobConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.CancelJobParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_talent_breach_contract.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentBreachContractActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentBreachContractActivity: BaseActivity(),View.OnClickListener, OnTradePasswordListener {

    var mLoadingDialog: LoadingDialog? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    var jobOrderId: String? = null
    var taskType: Int = 0//1,工单；2，任务

    var mCancelJobConfirmDetailData: CancelJobConfirmDetailData? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,jobOrderId: String?,taskType: Int) {
            var intent = Intent(activity, TalentBreachContractActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,jobOrderId)
            intent.putExtra(Constant.TASK_TYPE_KEY,taskType)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }
    private val talentJobVM: TalentJobVM by viewModels {
        InjectorUtils.provideTalentJobVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_talent_breach_contract

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
        taskType = intent?.getIntExtra(Constant.TASK_TYPE_KEY,0) ?: 0

        if (taskType == 1) {
            tv_confirm_tip.text = "\t\t\t\t违约取消工单后，若雇主已预付报酬，报酬将退回给雇主，您还须向雇主赔付："
        } else if (taskType == 2) {
            tv_confirm_tip.text = "\t\t\t\t违约取消任务后，雇主已预付的报酬将退回给雇主，您还须向雇主赔付："
        }
        sendCancelJobConfirmDetailRequest()
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

        talentJobVM.cancelJobConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showCancelJobConfirmDetail(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentJobVM.cancelJobData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("已成功取消工作")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
                    UMengEventModule.report(this, BussinessTalentEvent.talent_breach_cancel)
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

    fun sendCancelJobConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        val body = CancelJobConfirmDetailParm()
        body.jobOrderId = jobOrderId

        talentJobVM.fetchCancelJobConfirmDetail(token,body)
    }

    fun sendCancelJobRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CancelJobParm()
        body.jobOrderId = jobOrderId
        body.tradePassword = tradePassword

        talentJobVM.cancelJob(token,body)
    }

    fun showCancelJobConfirmDetail (data: CancelJobConfirmDetailData?) {
        mCancelJobConfirmDetailData = data

        mTvCreditBreachAmount.text = AmountUtil.addCommaDots(data?.signupFrozenAmount)

       /* if (data?.totalAmount!! > 0) {
            mTvWorkBreachAmount.text = AmountUtil.addCommaDots(data?.totalAmount)
            mTvWorkBreach.visibility = View.VISIBLE
            tv_refund_prepaid_salary.visibility = View.VISIBLE
            mTvWorkBreachAmount.visibility = View.VISIBLE
            tv_work_breach.visibility = View.VISIBLE
        } else {
            mTvWorkBreach.visibility = View.GONE
            tv_refund_prepaid_salary.visibility = View.GONE
            mTvWorkBreachAmount.visibility = View.GONE
            tv_work_breach.visibility = View.GONE
        }*/
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@TalentBreachContractActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
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
                NavigationUtils.goVerifyIdentidyActivity(this@TalentBreachContractActivity)
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
                NavigationUtils.goRealNameActivity(this@TalentBreachContractActivity)
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
        sendCancelJobRequest(tradePassword)
    }


}