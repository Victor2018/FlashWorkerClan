package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.EmployerEmployingReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.AccountVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmploymentVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnBreachContractFiredListener
import com.flash.worker.module.business.view.activity.EmployerEmployingActivity
import com.flash.worker.module.business.view.activity.EmployerTaskEmployingActivity
import com.flash.worker.module.business.view.adapter.EmployerEmployingAdapter
import kotlinx.android.synthetic.main.fragment_employer_employing.*
import kotlinx.android.synthetic.main.fragment_employer_employing.mSrlRefresh
import kotlinx.android.synthetic.main.fragment_employer_employing.mTvNoData
import kotlinx.android.synthetic.main.fragment_talent_wait_employ.*
import kotlinx.android.synthetic.main.rv_employer_employing_cell.view.*
import kotlinx.android.synthetic.main.rv_employer_task_employing_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEmployingFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主-进行中
 * -----------------------------------------------------------------
 */
class EmployerEmployingFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnBreachContractFiredListener {

    companion object {
        fun newInstance(): EmployerEmployingFragment {
            return EmployerEmployingFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var talentJobVM: TalentJobVM
    private lateinit var employmentVM: EmploymentVM
    private lateinit var accountVM: AccountVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerEmployingAdapter: EmployerEmployingAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数
    var mToggleAutoPrepaid: Switch? = null

    override fun getLayoutResource() = R.layout.fragment_employer_employing

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        employerJobVM = ViewModelProvider(
                this, EmployerJobVMFactory(this))
                .get(EmployerJobVM::class.java)

        talentJobVM = ViewModelProvider(
                this, TalentJobVMFactory(this))
                .get(TalentJobVM::class.java)

        employmentVM = ViewModelProvider(
                this, EmploymentVMFactory(this))
                .get(EmploymentVM::class.java)

        accountVM = ViewModelProvider(
            this, AccountVMFactory(this)
        ).get(AccountVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mEmployerEmployingAdapter = EmployerEmployingAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerEmployingAdapter,mRvEmployerEmploying)
        
        mRvEmployerEmploying.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmployerEmploying.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendEmployerEmployingRequest()
    }

    fun subscribeUi() {
        employerJobVM.employerEmployingData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerEmployingData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        talentJobVM.settlementDateData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var employingData = mEmployerEmployingAdapter?.getItem(currentPosition)
                    EmployerEmployingActivity.intentStart(activity
                            as AppCompatActivity,it.value.data,employingData)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.checkCloseTaskData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showCloseTaskTipDlg()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("156020",it.code)) {
                        showCloseTaskReceivedTipDlg(it.message)
                        return@Observer
                    }
                    ToastUtils.show(it.message)
                }
            }
        })

        employmentVM.closeTaskData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("任务关闭成功")

                    mEmployerEmployingAdapter?.removeItem(currentPosition)
                    mEmployerEmployingAdapter?.notifyItemRemoved(currentPosition)
                    if (mEmployerEmployingAdapter?.getContentItemCount() == 0) {
                        mTvNoData.visibility = View.VISIBLE
                        mRvEmployerEmploying.visibility = View.GONE
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
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
                    ToastUtils.show("取消失败，请重试")
                }
            }
        })

        employerJobVM.checkAutoPrepaidData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value?.data?.status ?: false
                    var isChecked = mToggleAutoPrepaid?.isChecked ?: false
                    if (status) {
                        if (isChecked) {
                            showCloseAutoPrepaidDlg()
                        } else {
                            showOpenAutoPrepaidDlg()
                        }
                    } else {
                        showToggleAutoPrepaidErrorTipDlg()
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employerJobVM.openAutoPrepaidData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mToggleAutoPrepaid?.isChecked = true
                    ToastUtils.show("自动预付开启成功")
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
                    ToastUtils.show(it.message)
                }
            }
        })

        employerJobVM.closeAutoPrepaidData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mToggleAutoPrepaid?.isChecked = false
                    ToastUtils.show("自动预付关闭成功")
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendEmployerEmployingRequest () {
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

        var body = EmployerEmployingParm()
        body.pageNum = currentPage

        employerJobVM.fetchEmployerEmploying(token,body)
    }

    fun sendSettlementDateRequest (releaseId: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentJobVM.fetchSettlementDate(token,releaseId)
    }

    fun sendCheckCloseTaskRequest (employerReleaseId: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CheckCloseTaskParm()
        body.employerReleaseId = employerReleaseId

        employmentVM.checkCloseTask(token,body)
    }

    fun sendCloseTaskRequest (employerReleaseId: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CloseTaskParm()
        body.employerReleaseId = employerReleaseId

        employmentVM.closeTask(token,body)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendCheckAutoPrepaidRequest (employerReleaseId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CheckAutoPrepaidParm()
        body.employerReleaseId = employerReleaseId

        employerJobVM.checkAutoPrepaid(token,body)
    }

    fun sendOpenAutoPrepaidRequest (tradePassword: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var employerReleaseId = mEmployerEmployingAdapter?.getItem(currentPosition)?.employerReleaseId
        val body = OpenAutoPrepaidParm()
        body.employerReleaseId = employerReleaseId
        body.tradePassword = tradePassword

        employerJobVM.openAutoPrepaid(token,body)
    }

    fun sendCloseAutoPrepaidRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var employerReleaseId = mEmployerEmployingAdapter?.getItem(currentPosition)?.employerReleaseId
        val body = CloseAutoPrepaidParm()
        body.employerReleaseId = employerReleaseId

        employerJobVM.closeAutoPrepaid(token,body)
    }

    fun showEmployerEmployingData (datas: EmployerEmployingReq) {
        mEmployerEmployingAdapter?.showData(datas.data,mTvNoData,mRvEmployerEmploying,currentPage)
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

    fun showCloseTaskReceivedTipDlg (error: String?) {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = error
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "好的"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {

            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCloseTaskTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "任务关闭后，\n" +
                "预付冻结报酬将立即解冻"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendCloseTaskRequest(mEmployerEmployingAdapter?.getItem(currentPosition)?.employerReleaseId)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showAutoPrepaidTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "自动预付规则"
        commonTipDialog.mContent = "1.打开自动预付开关后，当工单进 入开工时间后，" +
                "系统将自动从钱包-可用余额中扣划预付报酬和服务费进行冻结；\n\n" +
                "2. 当钱包-可用余额不足，扣划失败，可在开工时间1小时内进行手动预付，" +
                "超过时间未预付，将视为违约。\n\n3. 自动预付开关仅限在工单的非工作时间段使用。"
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showOpenAutoPrepaidDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "确定开启自动预付吗？开启后，到达开工时间，无论人才是否到岗，" +
                "系统将自动从钱包-可用余额中扣划报酬及服务费进行冻结，为避免扣划失败影响雇用，" +
                "请确保可用余额充足！"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "确定开启"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendAccountInfoRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCloseAutoPrepaidDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "确定要关闭自动预付吗？"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendCloseAutoPrepaidRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showToggleAutoPrepaidErrorTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "当前工单处于工作时段中，\n" +
                "暂不可开启或关闭自动预付哦~"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "好的"
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

    fun getTradePasswordDialog (): TradePasswordDialog {
        var mTradePasswordDialog = TradePasswordDialog(activity!!)
        mTradePasswordDialog.mOnTradePasswordListener = this
        mTradePasswordDialog.showAmount = false
        mTradePasswordDialog.setCanceledOnTouchOutside(false)

        return mTradePasswordDialog
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerEmployingAdapter?.clear()
        mEmployerEmployingAdapter?.setFooterVisible(false)
        mEmployerEmployingAdapter?.notifyDataSetChanged()

        mRvEmployerEmploying.setHasMore(false)

        sendEmployerEmployingRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerEmployingRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mEmployerEmployingAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mTvGroupMessage -> {
//                ToastUtils.show("群发消息，developing...")
            }
            R.id.mTvClose -> {
                sendCheckCloseTaskRequest(data?.employerReleaseId)
            }
            R.id.mIvAutoPrepaidTip -> {
                showAutoPrepaidTipDlg()
            }
            R.id.mFlAutoPrepaid -> {
                mToggleAutoPrepaid = view?.mToggleAutoPrepaid

                var employerReleaseId = mEmployerEmployingAdapter?.getItem(position)?.employerReleaseId
                sendCheckAutoPrepaidRequest(employerReleaseId)
            }
            R.id.mTvDetail -> {
                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(activity as AppCompatActivity,
                        data?.employerReleaseId, null,null,
                        JobDetailAction.PREVIEW)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(activity!!,
                        data?.employerReleaseId,null,null, TaskDetailAction.PREVIEW)
                }

            }
            else -> {
                if (taskType == 1) {
                    var releaseId = mEmployerEmployingAdapter?.getItem(position)?.employerReleaseId
                    sendSettlementDateRequest(releaseId)
                } else if (taskType == 2) {
                    EmployerTaskEmployingActivity.intentStart(activity as AppCompatActivity,data)
                }
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        sendOpenAutoPrepaidRequest(tradePassword)
    }

    override fun OnBreachContractFired() {
        ToastUtils.show("违约解雇，develop...")
    }
}