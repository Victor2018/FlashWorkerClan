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
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.PrepaidConfirmDetailData
import com.flash.worker.lib.coremodel.data.parm.PrepaidConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.PrepaidParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.SalaryTalentAdapter
import kotlinx.android.synthetic.main.activity_prepaid_salary.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PrepaidSalaryActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class PrepaidSalaryActivity: BaseActivity(),View.OnClickListener, OnTradePasswordListener,
        AdapterView.OnItemClickListener {
    var mLoadingDialog: LoadingDialog? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    var mPrepaidConfirmDetailParm: PrepaidConfirmDetailParm? = null
    var mPrepaidConfirmDetailData: PrepaidConfirmDetailData? = null

    var mSalaryTalentAdapter: SalaryTalentAdapter? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: PrepaidConfirmDetailParm?) {
            var intent = Intent(activity, PrepaidSalaryActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }
    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_prepaid_salary

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
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

        mSalaryTalentAdapter = SalaryTalentAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSalaryTalentAdapter,mRvSalaryTalent)

        mRvSalaryTalent.adapter = animatorAdapter

        mIvBack.setOnClickListener(this)
        mTvConfimPrepaid.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mPrepaidConfirmDetailParm = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as PrepaidConfirmDetailParm

        sendPrepaidConfirmDetailRequest()
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
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.prepaidConfirmDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showPrepaidConfirmDetail(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.prepaidData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    PrepaidSalarySuccessActivity.intentStart(this,mPrepaidConfirmDetailData)

                    LiveDataBus.send(BusinessActions.REFRESH_E_WAIT_PREPAID)
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
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
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendPrepaidConfirmDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employmentVM.fetchPrepaidConfirmDetail(token,mPrepaidConfirmDetailParm)
    }

    fun sendPrepaidRequest (tradePassword: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = PrepaidParm()
        body.employerReleaseId = mPrepaidConfirmDetailParm?.employerReleaseId
        body.settlementOrderIds = mPrepaidConfirmDetailParm?.settlementOrderIds
        body.tradePassword = tradePassword


        employmentVM.prepaid(token,body)
    }

    fun showPrepaidConfirmDetail (data: PrepaidConfirmDetailData?) {
        mPrepaidConfirmDetailData = data

        mTvPrepaidCount.text = data?.count.toString()
        tv_service_fee_rate.text = "平台服务费(${data?.serviceFeeRate}%)："

        mTvTotalPrepaidAmount.text = AmountUtil.addCommaDots(data?.totalPrepaidAmount)
        mTvAvailableBalanceAmount.text = AmountUtil.addCommaDots(data?.availableBalance)

        mSalaryTalentAdapter?.clear()
        mSalaryTalentAdapter?.add(data?.prepaidUsers)
        mSalaryTalentAdapter?.notifyDataSetChanged()

        var availableBalance = data?.availableBalance ?: 0.0
        var totalPrepaidAmount = data?.totalPrepaidAmount ?: 0.0
        if (availableBalance < totalPrepaidAmount) {
            mTvConfimPrepaid.text = "支付充值"
        } else {
            mTvConfimPrepaid.text = "确认预付"
        }
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@PrepaidSalaryActivity)
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
                NavigationUtils.goVerifyIdentidyActivity(this@PrepaidSalaryActivity)
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
                NavigationUtils.goRealNameActivity(this@PrepaidSalaryActivity)
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
            R.id.mTvConfimPrepaid -> {
                var availableBalance = mPrepaidConfirmDetailData?.availableBalance ?: 0.0
                var totalPrepaidAmount = mPrepaidConfirmDetailData?.totalPrepaidAmount ?: 0.0
                if (availableBalance < totalPrepaidAmount) {
                    NavigationUtils.goRechargeActivity(this)
                    return
                }
                sendAccountInfoRequest()
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendPrepaidRequest(tradePassword)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }


}