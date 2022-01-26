package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.FinishJobParm
import com.flash.worker.lib.coremodel.data.parm.RemindSettlementParm
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementDetailParm
import com.flash.worker.lib.coremodel.data.req.TaskSettlementDetailReq
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.AccountVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmploymentVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnReportCancelListener
import com.flash.worker.module.business.view.activity.*
import com.flash.worker.module.business.view.adapter.TalentTaskWaitSettlementAdapter
import com.flash.worker.module.business.view.dialog.ReportCancelPopWindow
import kotlinx.android.synthetic.main.fragment_talent_wait_prepaid.*
import kotlinx.android.synthetic.main.fragment_talent_wait_settlement.*
import kotlinx.android.synthetic.main.fragment_talent_wait_settlement.mSrlRefresh
import kotlinx.android.synthetic.main.fragment_talent_wait_settlement.mTvNoData
import kotlinx.android.synthetic.main.rv_talent_wait_prepaid_cell.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitSettlementFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才任务-待结算
 * -----------------------------------------------------------------
 */
class TalentTaskWaitSettlementFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnReportCancelListener {

    companion object {
        fun newInstance(): TalentTaskWaitSettlementFragment {
            return TalentTaskWaitSettlementFragment()
        }
    }

    private lateinit var talentJobVM: TalentJobVM
    private lateinit var employmentVM: EmploymentVM
    private lateinit var accountVM: AccountVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mTalentTaskWaitSettlementAdapter: TalentTaskWaitSettlementAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数


    override fun getLayoutResource() = R.layout.fragment_talent_wait_settlement

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        talentJobVM = ViewModelProvider(
                this, TalentJobVMFactory(this))
                .get(TalentJobVM::class.java)

        employmentVM = ViewModelProvider(
                this, EmploymentVMFactory(this))
                .get(EmploymentVM::class.java)

        accountVM = ViewModelProvider(
                this, AccountVMFactory(this))
                .get(AccountVM::class.java)

        userVM = ViewModelProvider(
                this, UserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mTalentTaskWaitSettlementAdapter = TalentTaskWaitSettlementAdapter(activity!!,this)
        mTalentTaskWaitSettlementAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentTaskWaitSettlementAdapter,mRvWaitSettlement)

        mRvWaitSettlement.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitSettlement.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendTaskSettlementDetailRequest()
    }

    fun subscribeUi() {
        talentJobVM.taskSettlementDetailData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTaskSettlementDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })


        talentJobVM.remindSettlementData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showRemindSettlementSuccessTipDlg()
                    UMengEventModule.report(activity, BussinessTalentEvent.talent_remind_settlement)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.cancelSignUpData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("取消成功")
                    mTalentTaskWaitSettlementAdapter?.removeItem(currentPosition)
                    mTalentTaskWaitSettlementAdapter?.notifyItemRemoved(currentPosition)
                    LiveDataBus.send(JobActions.DISMISS_TRADE_PASSWORD_DLG)
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

        accountVM.accountInfoData.observe(viewLifecycleOwner, Observer {
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

        userVM.imLoginInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    NavigationUtils.goChatActivity(activity as AppCompatActivity,it.value.data?.imAccid)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTaskSettlementDetailRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }

        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token


        var parentAct = activity as TalentTaskEmployingActivity

        var body = TaskSettlementDetailParm()
        body.jobOrderId = parentAct.mTalentEmployingInfo?.id
        body.type = 20

        talentJobVM.fetchTaskSettlementDetail(token,body)
    }

    fun sendFinishJobRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = FinishJobParm()
        body.settlementOrderId = mTalentTaskWaitSettlementAdapter?.getItem(currentPosition)?.settlementOrderId

        talentJobVM.finishJob(token,body)
    }

    fun sendRemindSettlementRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = RemindSettlementParm()
        body.settlementOrderId = mTalentTaskWaitSettlementAdapter?.getItem(currentPosition)?.settlementOrderId

        talentJobVM.remindSettlement(token,body)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun showTaskSettlementDetailData (datas: TaskSettlementDetailReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mTalentTaskWaitSettlementAdapter?.setFooterVisible(false)
            mTalentTaskWaitSettlementAdapter?.clear()
            mTalentTaskWaitSettlementAdapter?.notifyDataSetChanged()
            mRvWaitSettlement.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mTalentTaskWaitSettlementAdapter?.setFooterVisible(false)
            mTalentTaskWaitSettlementAdapter?.clear()
            mTalentTaskWaitSettlementAdapter?.notifyDataSetChanged()
            mRvWaitSettlement.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        if (currentPage == 1) {
            mTalentTaskWaitSettlementAdapter?.clear()
        }
        mTalentTaskWaitSettlementAdapter?.setFooterVisible(true)
        mTalentTaskWaitSettlementAdapter?.add(datas?.data)

        mRvWaitSettlement.setHasMore(false)
        mTalentTaskWaitSettlementAdapter?.setLoadState(LOADING_END)
        mTalentTaskWaitSettlementAdapter?.notifyDataSetChanged()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(activity as AppCompatActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(activity!!)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    fun showRemindSettlementSuccessTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已成功向雇主发送提醒！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "确认"
        commonTipDialog.show()
    }

    fun showSetTransactionPwdDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未设置交易密码~"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "立即设置"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goSetTransactionPasswordActivity(activity!!)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showErrorPasswordTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
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
                NavigationUtils.goVerifyIdentidyActivity(activity!!)
            }

        }
        commonTipDialog.show()
    }

    fun showReportTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才：\n" +
                "\t\t\t\t请确认该雇主确有违约行为" +
                "需要举报，举报后本次工作将立" +
                "即终止，进入争议处理流程！"
        commonTipDialog.mCancelText = "我再想想"
        commonTipDialog.mOkText = "确定举报"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                var data = mTalentTaskWaitSettlementAdapter?.getItem(currentPosition)
                ReportActivity.intentStart(activity as AppCompatActivity,null,data)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mTalentTaskWaitSettlementAdapter?.clear()
        mTalentTaskWaitSettlementAdapter?.setFooterVisible(false)
        mTalentTaskWaitSettlementAdapter?.notifyDataSetChanged()

        mRvWaitSettlement.setHasMore(false)

        sendTaskSettlementDetailRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTaskSettlementDetailRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mTalentTaskWaitSettlementAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mTvParentTitle -> {

            }
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_15).toFloat())
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_5).toFloat())
                var reportCancelPopWindow = ReportCancelPopWindow(activity)
                reportCancelPopWindow.mOnReportCancelListener = this
                reportCancelPopWindow.show(view, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            R.id.mTvContactEmployer -> {
                var userId =  mTalentTaskWaitSettlementAdapter?.getItem(position)?.employerUserId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvRemindSettlement -> {
                sendRemindSettlementRequest()
            }
            else -> {
                var parentAct = activity as TalentTaskEmployingActivity
                var id = parentAct.mTalentEmployingInfo?.id
                if (taskType == 1) {
                    TalentOrderDetailActivity.intentStart(activity as AppCompatActivity, id)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskOrderDetailActivity(activity as AppCompatActivity, id)
                }
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
//        var releaseId = mTalentTaskWaitSettlementAdapter?.getItem(currentPosition)?.id
//        sendCancelSignUpRequest(releaseId,tradePassword)
    }

    override fun OnReport() {
        showReportTipDlg()
    }

    override fun OnCancel() {
        var jobOrderId = mTalentTaskWaitSettlementAdapter?.getItem(currentPosition)?.jobOrderId
        TalentBreachContractActivity.intentStart(activity as AppCompatActivity,jobOrderId,2)
    }
}