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
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CheckTerminationEmploymentParm
import com.flash.worker.lib.coremodel.data.parm.EmployerWaitEmployParm
import com.flash.worker.lib.coremodel.data.parm.TerminationEmploymentParm
import com.flash.worker.lib.coremodel.data.req.EmployerWaitEmployReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmploymentVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnTerminationEmployListener
import com.flash.worker.module.business.view.activity.EmployerWaitEmployActivity
import com.flash.worker.module.business.view.adapter.EmployerWaitEmployAdapter
import com.flash.worker.module.business.view.dialog.TerminationEmployPopWindow
import kotlinx.android.synthetic.main.fragment_employer_wait_employ.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitEmployFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主待录取
 * -----------------------------------------------------------------
 */
class EmployerWaitEmployFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnTerminationEmployListener {

    companion object {
        fun newInstance(): EmployerWaitEmployFragment {
            return EmployerWaitEmployFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var employmentVM: EmploymentVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerWaitEmployAdapter: EmployerWaitEmployAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_wait_employ

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

        employmentVM = ViewModelProvider(
                this, EmploymentVMFactory(this))
                .get(EmploymentVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mEmployerWaitEmployAdapter = EmployerWaitEmployAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerWaitEmployAdapter,mRvWaitEmploy)
        
        mRvWaitEmploy.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitEmploy.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendEmployerWaitEmployRequest()
    }

    fun subscribeUi() {
        employerJobVM.employerWaitEmployData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerWaitEmployData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.checkTerminationEmploymentData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var breachContractStatus = it.value.data?.breachContractStatus ?: false
                    var terminationEmploymentCount = it.value.data?.terminationEmploymentCount ?: 0
                    if (breachContractStatus) {
                        showTerminationEmployCountTipDlg(terminationEmploymentCount)
                        return@Observer
                    }
                    sendTerminationEmployRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.terminationEmploymentData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("成功终止岗位雇用")
                    mEmployerWaitEmployAdapter?.removeItem(currentPosition)
                    mEmployerWaitEmployAdapter?.notifyItemRemoved(currentPosition)

                    var count =mEmployerWaitEmployAdapter?.getContentItemCount() ?: 0
                    if (count == 0) {
                        mTvNoData.visibility = View.VISIBLE
                        mRvWaitEmploy.visibility = View.GONE
                    }

                    UMengEventModule.report(activity, BussinessEmployerEvent.employer_termination_employment)
                }
                is HttpResult.Error -> {
                    ToastUtils.show("终止失败，请重试")
                }
            }
        })

    }

    fun sendEmployerWaitEmployRequest () {
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

        var body = EmployerWaitEmployParm()
        body.pageNum = currentPage

        employerJobVM.fetchWaitEmploy(token,body)
    }

    fun sendCheckTerminationEmployRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = CheckTerminationEmploymentParm()
        body.employerReleaseId = mEmployerWaitEmployAdapter?.getItem(currentPosition)?.employerReleaseId

        employmentVM.checkTerminationEmployment(token,body)
    }

    fun sendTerminationEmployRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TerminationEmploymentParm()
        body.employerReleaseId = mEmployerWaitEmployAdapter?.getItem(currentPosition)?.employerReleaseId

        employmentVM.terminationEmployment(token,body)
    }

    fun showEmployerWaitEmployData (datas: EmployerWaitEmployReq) {
        mEmployerWaitEmployAdapter?.showData(datas.data,mTvNoData,mRvWaitEmploy,currentPage)
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

    fun showTerminationEmployTipDlg () {
        var tip = "您还没有雇用人才哟，确定要终止雇用吗？"
        var signupNum = mEmployerWaitEmployAdapter?.getItem(currentPosition)?.signupNum ?: 0
        var employmentNum = mEmployerWaitEmployAdapter?.getItem(currentPosition)?.employmentNum ?: 0

        if (signupNum > employmentNum) {
            tip = "\t\t\t\t当前报名人数已超过雇用人数，若您终止雇用，视为您爽约哟。\n\t\t\t\t若您30天内三次爽约，您的账号将被暂停使用30天"
        }

        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = tip
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendCheckTerminationEmployRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showTerminationEmployCountTipDlg (terminationEmploymentCount: Int) {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "再爽约${terminationEmploymentCount}次，您的账号将被暂停使用，是否继续终止雇用？"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "终止雇用"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendTerminationEmployRequest()
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
        mEmployerWaitEmployAdapter?.clear()
        mEmployerWaitEmployAdapter?.setFooterVisible(false)
        mEmployerWaitEmployAdapter?.notifyDataSetChanged()

        mRvWaitEmploy.setHasMore(false)

        sendEmployerWaitEmployRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerWaitEmployRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_8).toFloat())
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var terminationEmployPopWindow = TerminationEmployPopWindow(activity)
                terminationEmployPopWindow.mOnTerminationEmployListener = this
                terminationEmployPopWindow.show(view!!, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            R.id.mTvDetail -> {
                var releaseId = mEmployerWaitEmployAdapter?.getItem(position)?.employerReleaseId
                NavigationUtils.goJobDetailActivity(activity as AppCompatActivity,
                    releaseId,null,null, JobDetailAction.PREVIEW)
            }
            else -> {
                var data = mEmployerWaitEmployAdapter?.getItem(position)
                EmployerWaitEmployActivity.intentStart(activity as AppCompatActivity,data)
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
    }

    override fun OnTerminationEmploy() {
        showTerminationEmployTipDlg()
    }
}