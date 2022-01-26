package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TaskSettledParm
import com.flash.worker.lib.coremodel.data.parm.TaskSubmitDetailParm
import com.flash.worker.lib.coremodel.data.req.TaskSettledReq
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnComplaintCancelListener
import com.flash.worker.module.business.view.activity.ComplaintActivity
import com.flash.worker.module.business.view.activity.EmployerBreachContractActivity
import com.flash.worker.module.business.view.activity.EmployerTaskEmployingActivity
import com.flash.worker.module.business.view.adapter.EmployerTaskSettledAdapter
import kotlinx.android.synthetic.main.fragment_employer_task_settled.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskReceivedFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主任务-已结算
 * -----------------------------------------------------------------
 */
class EmployerTaskSettledFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnComplaintCancelListener {

    companion object {
        fun newInstance(): EmployerTaskSettledFragment {
            return EmployerTaskSettledFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM
    private lateinit var talentJobVM: TalentJobVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerTaskSettledAdapter: EmployerTaskSettledAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_task_settled

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
                this, EmployerJobVMFactory(this)).get(EmployerJobVM::class.java)

        userVM = ViewModelProvider(
            this, UserVMFactory(this)).get(UserVM::class.java)

        talentJobVM = ViewModelProvider(
            this, TalentJobVMFactory(this)).get(TalentJobVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mEmployerTaskSettledAdapter = EmployerTaskSettledAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerTaskSettledAdapter,mRvSettled)
        
        mRvSettled.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvSettled.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendTaskSettlementRequest()

        var parentAct = activity as EmployerTaskEmployingActivity
        parentAct.sendEmploymentNumRequest()
    }

    fun subscribeUi() {
        employerJobVM.taskSettlementData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTaskSettlementData(it.value)
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

        talentJobVM.taskSubmitDetailData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    NavigationUtils.goTaskSubmitDetailActivity(activity as AppCompatActivity,
                        it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendTaskSettlementRequest () {
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

        var body = TaskSettledParm()
        body.pageNum = currentPage
        var parentAct = activity as EmployerTaskEmployingActivity
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId
        body.type = 30

        employerJobVM.fetchTaskSettlement(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun sendTaskSubmitDetailRequest (settlementOrderId: String?) {
        if (!App.get().hasLogin()) return

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TaskSubmitDetailParm()
        body.settlementOrderId = settlementOrderId

        talentJobVM.fetchTaskSubmitDetail(token,body)
    }

    fun showTaskSettlementData (datas: TaskSettledReq) {
        mEmployerTaskSettledAdapter?.showData(datas.data,mTvNoData,mRvSettled,currentPage)
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

    fun showComplaintTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t请确认该人才有违约行为，" +
                "投诉后与该人才的雇用将立即终止，任务进入争议处理流程。"
        commonTipDialog.mCancelText = "我再想想"
        commonTipDialog.mOkText = "确定投诉"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                var userName = mEmployerTaskSettledAdapter?.getItem(currentPosition)?.username
                var jobOrderId = mEmployerTaskSettledAdapter?.getItem(currentPosition)?.jobOrderId
                ComplaintActivity.intentStart(activity as AppCompatActivity,userName,jobOrderId,2)
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
        mEmployerTaskSettledAdapter?.clear()
        mEmployerTaskSettledAdapter?.setFooterVisible(false)
        mEmployerTaskSettledAdapter?.notifyDataSetChanged()

        mRvSettled.setHasMore(false)

        sendTaskSettlementRequest()

        var parentAct = activity as EmployerTaskEmployingActivity
        parentAct.sendEmploymentNumRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTaskSettlementRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mEmployerTaskSettledAdapter?.getItem(position)
        when (view?.id) {
            R.id.mTvSubmitDetail -> {
                sendTaskSubmitDetailRequest(data?.settlementOrderId)
            }
            else -> {
                var resumeId =  data?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }

    override fun OnComplaint() {
        showComplaintTipDlg()
    }

    override fun OnCancel() {
        var jobOrderId = mEmployerTaskSettledAdapter?.getItem(currentPosition)?.jobOrderId
        EmployerBreachContractActivity.intentStart(activity as AppCompatActivity,jobOrderId)
    }

}