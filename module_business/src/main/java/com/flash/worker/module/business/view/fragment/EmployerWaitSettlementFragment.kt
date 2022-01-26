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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerSettlementOrderParm
import com.flash.worker.lib.coremodel.data.parm.SettlementConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.SettlementNumParm
import com.flash.worker.lib.coremodel.data.req.EmployerSettlementOrderReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnComplaintCancelListener
import com.flash.worker.module.business.view.activity.*
import com.flash.worker.module.business.view.adapter.EmployerWaitSettlementAdapter
import com.flash.worker.module.business.view.dialog.ComplaintCancelPopWindow
import kotlinx.android.synthetic.main.fragment_employer_wait_settlement.*
import kotlinx.android.synthetic.main.rv_employer_wait_settlement_cell.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitSettlementFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主-待结算
 * -----------------------------------------------------------------
 */
class EmployerWaitSettlementFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnComplaintCancelListener,
        View.OnClickListener {

    companion object {
        fun newInstance(): EmployerWaitSettlementFragment {
            return EmployerWaitSettlementFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerWaitSettlementAdapter: EmployerWaitSettlementAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数

    var finishedStatus: Int = 0//完工状态: 0-全部；1-只查询已完工

    override fun getLayoutResource() = R.layout.fragment_employer_wait_settlement

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

        userVM = ViewModelProvider(
                this, UserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeEvent()
        subscribeUi()

        mEmployerWaitSettlementAdapter = EmployerWaitSettlementAdapter(activity!!,this)
        mEmployerWaitSettlementAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerWaitSettlementAdapter,mRvWaitSettlement)

        mRvWaitSettlement.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitSettlement.setLoadMoreListener(this)

        mTvSettlementAll.setOnClickListener(this)
    }

    fun initData () {
        currentPage = 1
        sendSettlementNumRequest()
        sendSettlementOrderRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.REFRESH_E_WAIT_SETTLEMENT)
                .observe(this, Observer {
                    currentPage = 1
                    mEmployerWaitSettlementAdapter?.checkMap?.clear()
                    mTvCheckCount.text = "(0/5)"
                    mTvSettlementAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))

                    mEmployerWaitSettlementAdapter?.clear()
                    mEmployerWaitSettlementAdapter?.setFooterVisible(false)
                    mEmployerWaitSettlementAdapter?.notifyDataSetChanged()

                    mRvWaitSettlement.setHasMore(false)

                    initData()
                })
    }

    fun subscribeUi() {
        userVM.imLoginInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    NavigationUtils.goChatActivity(activity as AppCompatActivity,it.value.data?.imAccid)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employerJobVM.employerSettlementOrderData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSettlementOrderData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        employerJobVM.settlementNumData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mEmployerWaitSettlementAdapter?.headerData = it.value.data
                    mEmployerWaitSettlementAdapter?.notifyDataSetChanged()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendSettlementOrderRequest () {
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


        var parentAct = activity as EmployerEmployingActivity

        var body = EmployerSettlementOrderParm()
        body.settlementStartTime = parentAct.mSettlementDateInfo?.settlementStartTime
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId
        body.type = 20
        body.pageNum = currentPage
        body.arrivedStatus = 0
        body.finishedStatus = finishedStatus

        employerJobVM.fetchEmployerSettlementOrder(token,body)
    }

    fun sendSettlementNumRequest () {
        mSrlRefresh.isRefreshing = false
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var parentAct = activity as EmployerEmployingActivity

        val body = SettlementNumParm()
        body.settlementStartTime = parentAct.mSettlementDateInfo?.settlementStartTime
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId

        employerJobVM.fetchSettlementNum(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun showSettlementOrderData (datas: EmployerSettlementOrderReq) {
        mEmployerWaitSettlementAdapter?.showData(datas.data,mTvNoData,mRvWaitSettlement,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerWaitSettlementAdapter?.clear()
        mEmployerWaitSettlementAdapter?.setFooterVisible(false)
        mEmployerWaitSettlementAdapter?.notifyDataSetChanged()

        mRvWaitSettlement.setHasMore(false)

        initData()
    }

    override fun OnLoadMore() {
        currentPage++
        sendSettlementOrderRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mEmployerWaitSettlementAdapter?.getItem(position)
        when (view?.id) {
            R.id.mClHeaderRoot -> {
                
            }
            R.id.mTvParentTitle -> {

            }
            R.id.mChkOnlyFinish -> {
                var showOnlyFinish = mEmployerWaitSettlementAdapter?.showOnlyFinish ?: false
                mEmployerWaitSettlementAdapter?.showOnlyFinish = !showOnlyFinish
                mEmployerWaitSettlementAdapter?.notifyDataSetChanged()

                showOnlyFinish = mEmployerWaitSettlementAdapter?.showOnlyFinish ?: false
                if (showOnlyFinish) {
                    finishedStatus = 1
                } else {
                    finishedStatus = 0
                }
                sendSettlementOrderRequest()
            }
            R.id.mChkCheck -> {
                var isChecked = mEmployerWaitSettlementAdapter?.isItemChecked(data) ?: false
                if (isChecked!!) {
                    mEmployerWaitSettlementAdapter?.checkMap?.remove(data?.settlementOrderId)
                } else {
                    if (mEmployerWaitSettlementAdapter?.checkMap?.size == 5) {
                        ToastUtils.show("最多只能选择5个人才")
                        return
                    }
                    mEmployerWaitSettlementAdapter?.checkMap?.put(data?.settlementOrderId!!,data!!)
                }
                mEmployerWaitSettlementAdapter?.notifyDataSetChanged()

                var count = mEmployerWaitSettlementAdapter?.checkMap?.size ?: 0
                mTvCheckCount.text = "($count/5)"

                if (count > 0) {
                    mTvSettlementAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
                } else {
                    mTvSettlementAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mTvJobOrderId -> {
                var jobOrderId = data?.jobOrderId
                ClipboardUtil.copy(activity,Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_15).toFloat())
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_5).toFloat())
                var complaintCancelPopWindow= ComplaintCancelPopWindow(activity,1)
                complaintCancelPopWindow.mOnComplaintCancelListener = this
                complaintCancelPopWindow.show(view, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            R.id.mTvReward -> {
                RewardActivity.intentStart(
                    activity as AppCompatActivity,data)
            }
            R.id.mTvContactEmployer -> {
                var userId =  data?.talentUserId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvPayment -> {
                var body = getSettlementConfirmRequestParm(data,true)

                SettlementSalaryActivity.intentStart(activity as AppCompatActivity,body)
            }
            else -> {
                var resumeId =  data?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }

    fun getSettlementConfirmRequestParm (
            data: EmployerSettlementOrderInfo?,
            isSingleCheck: Boolean): SettlementConfirmDetailParm {

        var body = SettlementConfirmDetailParm()

        var parentAct = activity as EmployerEmployingActivity
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId

        var settlementOrderId = ArrayList<String>()

        if (isSingleCheck) {
            settlementOrderId.add(data?.settlementOrderId ?: "")
        } else {
            var datas = mEmployerWaitSettlementAdapter?.checkMap
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
        var jobOrderId = mEmployerWaitSettlementAdapter?.getItem(currentPosition)?.jobOrderId
        EmployerBreachContractActivity.intentStart(activity as AppCompatActivity,jobOrderId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvSettlementAll -> {
                if (mEmployerWaitSettlementAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有待结算的人才")
                    return
                }
                if (mEmployerWaitSettlementAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择需要结算人才")
                    return
                }
                var body = getSettlementConfirmRequestParm(
                        null,false)

                SettlementSalaryActivity.intentStart(activity as AppCompatActivity,body)
            }
        }
    }

    fun showComplaintTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的雇主：\n" +
                "\t\t\t\t请确认该人才确有违约行为" +
                "需要投诉，投诉后本次雇用将立即终止，进入争议处理流程！"
        commonTipDialog.mCancelText = "我再想想"
        commonTipDialog.mOkText = "确定投诉"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                var userName = mEmployerWaitSettlementAdapter?.getItem(currentPosition)?.username
                var jobOrderId = mEmployerWaitSettlementAdapter?.getItem(currentPosition)?.jobOrderId
                ComplaintActivity.intentStart(activity as AppCompatActivity,userName,jobOrderId,1)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }
}