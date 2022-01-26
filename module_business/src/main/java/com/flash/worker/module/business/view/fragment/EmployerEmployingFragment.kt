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
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CheckCloseTaskParm
import com.flash.worker.lib.coremodel.data.parm.CloseTaskParm
import com.flash.worker.lib.coremodel.data.parm.EmployerEmployingParm
import com.flash.worker.lib.coremodel.data.req.EmployerEmployingReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmploymentVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnBreachContractFiredListener
import com.flash.worker.module.business.view.activity.EmployerEmployingActivity
import com.flash.worker.module.business.view.activity.EmployerTaskEmployingActivity
import com.flash.worker.module.business.view.adapter.EmployerEmployingAdapter
import kotlinx.android.synthetic.main.fragment_employer_employing.*
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

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerEmployingAdapter: EmployerEmployingAdapter? = null
    var currentPosition: Int = -1

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
        commonTipDialog.cancelBtnVisible = View.GONE
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
    }

    override fun OnBreachContractFired() {
        ToastUtils.show("违约解雇，develop...")
    }
}