package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CancelComplaintParm
import com.flash.worker.lib.coremodel.data.parm.TalentDeleteDisputeParm
import com.flash.worker.lib.coremodel.data.parm.TalentDisputeParm
import com.flash.worker.lib.coremodel.data.req.TalentDisputeReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.DisputeVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.TalentDisputeHandlingAdapter
import kotlinx.android.synthetic.main.activity_talent_dispute_handling.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentDisputeHandlingActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentDisputeHandlingActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener {

    var mLoadingDialog: LoadingDialog? = null
    var mTalentDisputeHandlingAdapter: TalentDisputeHandlingAdapter? = null
    var currentPage = 1

    var currentPosition: Int = -1

    var passwordErrorCount: Int = 0//密码输入错误次数

    private val disputeVM: DisputeVM by viewModels {
        InjectorUtils.provideDisputeVMFactory(this)
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TalentDisputeHandlingActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_dispute_handling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mTalentDisputeHandlingAdapter = TalentDisputeHandlingAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentDisputeHandlingAdapter,mRvDisputeHandling)

        mRvDisputeHandling.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvDisputeHandling.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
        mTvHandleRules.setOnClickListener(this)
    }

    fun initData () {
        sendDisputeRequest()
    }

    fun subscribeUi() {
        disputeVM.talentDisputeData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showDisputeData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        disputeVM.cancelComplaintData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {

                is HttpResult.Success -> {
                    ToastUtils.show("取消举报成功")
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)

                    currentPage = 1
                    mTalentDisputeHandlingAdapter?.clear()
                    mTalentDisputeHandlingAdapter?.setFooterVisible(false)
                    mTalentDisputeHandlingAdapter?.notifyDataSetChanged()

                    mRvDisputeHandling.setHasMore(false)

                    sendDisputeRequest()

                    UMengEventModule.report(this, BussinessTalentEvent.talent_cancel_report)
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

        disputeVM.talentDeleteDisputeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除记录成功")

                    mTalentDisputeHandlingAdapter?.removeItem(currentPosition)
                    mTalentDisputeHandlingAdapter?.notifyItemRemoved(currentPosition)

                    if (mTalentDisputeHandlingAdapter?.getContentItemCount() == 0) {
                        mTvNoData.visibility = View.VISIBLE
                        mTalentDisputeHandlingAdapter?.setFooterVisible(false)
                        mTalentDisputeHandlingAdapter?.clear()
                        mTalentDisputeHandlingAdapter?.notifyDataSetChanged()
                        mRvDisputeHandling.setHasMore(false)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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

    fun sendDisputeRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentDisputeParm()
        body.pageNum = currentPage

        disputeVM.fetchTalentDispute(token,body)
    }

    fun sendCancelComplaintRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CancelComplaintParm()
        body.complaintNo = mTalentDisputeHandlingAdapter?.getItem(currentPosition)?.complaintNo
        body.tradePassword = tradePassword

        disputeVM.cancelComplaint(token,body)
    }

    fun sendAccountInfoRequest () {
        passwordErrorCount = 0
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendDeleteComplaintRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentDeleteDisputeParm()
        body.complaintNo = mTalentDisputeHandlingAdapter?.getItem(currentPosition)?.complaintNo

        disputeVM.talentDeleteDispute(token,body)
    }

    fun showDisputeData (datas: TalentDisputeReq) {
        mTalentDisputeHandlingAdapter?.showData(datas.data,mTvNoData,mRvDisputeHandling,currentPage)
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(this@TalentDisputeHandlingActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showDeleteComplaintTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "确定删除已处理记录吗?"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendDeleteComplaintRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCancelComplaintTipDlg () {
        var data = mTalentDisputeHandlingAdapter?.getItem(currentPosition)
        var talentCreditAmount = data?.talentCreditAmount
        var prepaidAmount = data?.prepaidAmount
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才：\n" +
                "\t\t\t\t感谢您的理解与支持！\n" +
                "\t\t\t\t取消举报后，因雇用已终止，您需赔付雇主信用保证金:" +
                "${talentCreditAmount}元；平台代为退回雇主已预付报酬:${prepaidAmount}元。"
        commonTipDialog.mCancelText = "返回，继续举报"
        commonTipDialog.mOkText = "确定取消"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAccountInfoRequest()
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
                NavigationUtils.goVerifyIdentidyActivity(this@TalentDisputeHandlingActivity)
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
                NavigationUtils.goRealNameActivity(this@TalentDisputeHandlingActivity)
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
            R.id.mTvHandleRules -> {
                DisputeHandleRulesActivity.intentStart(this)
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mTalentDisputeHandlingAdapter?.clear()
        mTalentDisputeHandlingAdapter?.setFooterVisible(false)
        mTalentDisputeHandlingAdapter?.notifyDataSetChanged()

        mRvDisputeHandling.setHasMore(false)

        sendDisputeRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendDisputeRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mTvDelete -> {
                showDeleteComplaintTipDlg()
            }
            R.id.mTvCancel -> {
                showCancelComplaintTipDlg()
            }
            R.id.mTvHandleDetail -> {
                var complaintNo = mTalentDisputeHandlingAdapter?.getItem(position)?.complaintNo
                TalentHandlingDetailActivity.intentStart(this,complaintNo)
            }
            else -> {
            }
        }

    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendCancelComplaintRequest(tradePassword)
    }

}