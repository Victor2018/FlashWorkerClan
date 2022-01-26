package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TradePasswordDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentCancelSignUpParm
import com.flash.worker.lib.coremodel.data.parm.TalentWaitCommentParm
import com.flash.worker.lib.coremodel.data.req.TalentWaitCommentReq
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
import com.flash.worker.module.business.view.activity.TalentEvaluationActivity
import com.flash.worker.module.business.view.activity.TalentOrderDetailActivity
import com.flash.worker.module.business.view.adapter.TalentWaitCommentAdapter
import kotlinx.android.synthetic.main.fragment_talent_wait_comment.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentToBeEvaluationFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才 待评价
 * -----------------------------------------------------------------
 */
class TalentWaitCommentFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener {

    companion object {
        fun newInstance(): TalentWaitCommentFragment {
            return TalentWaitCommentFragment()
        }
    }

    private lateinit var talentJobVM: TalentJobVM
    private lateinit var employmentVM: EmploymentVM
    private lateinit var accountVM: AccountVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mTalentWaitCommentAdapter: TalentWaitCommentAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.fragment_talent_wait_comment

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

        mTalentWaitCommentAdapter = TalentWaitCommentAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentWaitCommentAdapter,mRvWaitComment)

        mRvWaitComment.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitComment.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendTalentWaitCommentRequest()
    }

    fun subscribeUi() {
        talentJobVM.talentWaitCommentData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentWaitCommentData(it.value)
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
                    mTalentWaitCommentAdapter?.removeItem(currentPosition)
                    mTalentWaitCommentAdapter?.notifyItemRemoved(currentPosition)
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
    }

    fun sendTalentWaitCommentRequest () {
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

        var body = TalentWaitCommentParm()
        body.pageNum = currentPage

        talentJobVM.fetchTalentWaitComment(token,body)
    }

    fun sendCancelSignUpRequest (relationId: String?,tradePassword: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentCancelSignUpParm()
        body.id = relationId
        body.tradePassword = tradePassword

        employmentVM.cancelSignUp(token,body)
    }

    fun sendAccountInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun showTalentWaitCommentData (datas: TalentWaitCommentReq) {
        mTalentWaitCommentAdapter?.showData(datas.data,mTvNoData,mRvWaitComment,currentPage)
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

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mTalentWaitCommentAdapter?.clear()
        mTalentWaitCommentAdapter?.setFooterVisible(false)
        mTalentWaitCommentAdapter?.notifyDataSetChanged()

        mRvWaitComment.setHasMore(false)

        sendTalentWaitCommentRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentWaitCommentRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mTalentWaitCommentAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mTvEvaluation -> {
                TalentEvaluationActivity.intentStart(activity as AppCompatActivity,data)
            }
            else -> {
                if (taskType == 1) {
                    TalentOrderDetailActivity.intentStart(activity as AppCompatActivity, data?.id)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskOrderDetailActivity(activity as AppCompatActivity, data?.id)
                }
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
        var releaseId = mTalentWaitCommentAdapter?.getItem(currentPosition)?.id
        sendCancelSignUpRequest(releaseId,tradePassword)
    }
}