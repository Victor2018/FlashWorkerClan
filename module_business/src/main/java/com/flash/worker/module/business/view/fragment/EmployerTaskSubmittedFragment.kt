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
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.TaskSettledParm
import com.flash.worker.lib.coremodel.data.parm.TaskSubmitDetailParm
import com.flash.worker.lib.coremodel.data.req.TaskSettledReq
import com.flash.worker.lib.coremodel.util.WebConfig
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
import com.flash.worker.module.business.view.adapter.EmployerTaskSubmittedAdapter
import com.flash.worker.module.business.view.dialog.ComplaintCancelPopWindow
import kotlinx.android.synthetic.main.fragment_employer_task_submitted.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerTaskReceivedFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主任务-已提交
 * -----------------------------------------------------------------
 */
class EmployerTaskSubmittedFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnComplaintCancelListener,
        View.OnClickListener {

    companion object {
        fun newInstance(): EmployerTaskSubmittedFragment {
            return EmployerTaskSubmittedFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM
    private lateinit var talentJobVM: TalentJobVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerTaskSubmittedAdapter: EmployerTaskSubmittedAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_task_submitted

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

        mEmployerTaskSubmittedAdapter = EmployerTaskSubmittedAdapter(activity!!,this)
        mEmployerTaskSubmittedAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerTaskSubmittedAdapter,mRvEmployerEmploying)
        
        mRvEmployerEmploying.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmployerEmploying.setLoadMoreListener(this)

        mTvSettlementAll.setOnClickListener(this)
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
                    NavigationUtils.goTaskSubmitDetailActivity(activity as AppCompatActivity,it.value.data)
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
        body.type = 20

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
        mEmployerTaskSubmittedAdapter?.showData(datas.data,mTvNoData,mRvEmployerEmploying,currentPage)
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
                var userName = mEmployerTaskSubmittedAdapter?.getItem(currentPosition)?.username
                var jobOrderId = mEmployerTaskSubmittedAdapter?.getItem(currentPosition)?.jobOrderId
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
        mEmployerTaskSubmittedAdapter?.clear()
        mEmployerTaskSubmittedAdapter?.setFooterVisible(false)
        mEmployerTaskSubmittedAdapter?.notifyDataSetChanged()

        mRvEmployerEmploying.setHasMore(false)

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
        var data = mEmployerTaskSubmittedAdapter?.getItem(position)
        when (view?.id) {
            R.id.mClHeaderRoot -> {

            }
            R.id.mTvParentTitle -> {

            }
            R.id.mChkCheck -> {
                var isChecked = mEmployerTaskSubmittedAdapter?.isItemChecked(data) ?: false
                if (isChecked) {
                    mEmployerTaskSubmittedAdapter?.checkMap?.remove(data?.settlementOrderId)
                } else {
                    if (mEmployerTaskSubmittedAdapter?.checkMap?.size == 5) {
                        ToastUtils.show("最多只能选择5个人才")
                        return
                    }
                    mEmployerTaskSubmittedAdapter?.checkMap?.put(data?.settlementOrderId!!, data)
                }
                mEmployerTaskSubmittedAdapter?.notifyDataSetChanged()

                mTvCheckCount.text = String.format("(%d/5)",mEmployerTaskSubmittedAdapter?.checkMap?.size)

                if (mEmployerTaskSubmittedAdapter?.checkMap?.size!! > 0) {
                    mTvSettlementAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
                } else {
                    mTvSettlementAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_15).toFloat())
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_5).toFloat())
                var complaintCancelPopWindow= ComplaintCancelPopWindow(activity,2)
                complaintCancelPopWindow.mOnComplaintCancelListener = this
                complaintCancelPopWindow.show(view, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            R.id.mTvContactTalent -> {
                var userId =  data?.talentUserId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvSubmitDetail -> {
                sendTaskSubmitDetailRequest(data?.settlementOrderId)
            }
            R.id.mTvSettlement -> {
                var body = getTaskSettlementBillDetailParm(
                    mEmployerTaskSubmittedAdapter?.getItem(position),true)

                NavigationUtils.goTaskSettlementSalaryActivity(activity as AppCompatActivity,body)
            }
            else -> {
                var resumeId =  data?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }

    fun getTaskSettlementBillDetailParm (
        data: TaskSettledInfo?,
        isSingleCheck: Boolean): TaskSettlementConfirmDetailParm {

        var body = TaskSettlementConfirmDetailParm()

        var parentAct = activity as EmployerTaskEmployingActivity
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId

        var settlementOrderId = ArrayList<String>()

        if (isSingleCheck) {
            settlementOrderId.add(data?.settlementOrderId ?: "")
        } else {
            var datas = mEmployerTaskSubmittedAdapter?.checkMap
            datas?.forEach {
                settlementOrderId.add(it.key)
            }
        }
        body.settlementOrderIds = settlementOrderId
        return body
    }


    override fun OnComplaint() {
        showComplaintTipDlg()
    }

    override fun OnCancel() {
        var jobOrderId = mEmployerTaskSubmittedAdapter?.getItem(currentPosition)?.jobOrderId
        EmployerBreachContractActivity.intentStart(activity as AppCompatActivity,jobOrderId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvSettlementAll -> {
                if (mEmployerTaskSubmittedAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有待结算的人才")
                    return
                }
                if (mEmployerTaskSubmittedAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择需要结算人才")
                    return
                }
                var body = getTaskSettlementBillDetailParm(
                    null,false)

                NavigationUtils.goTaskSettlementSalaryActivity(activity as AppCompatActivity,body)
            }
        }
    }
}