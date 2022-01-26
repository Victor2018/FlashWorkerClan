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
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ArrivePostParm
import com.flash.worker.lib.coremodel.data.parm.RemindPrepaidParm
import com.flash.worker.lib.coremodel.data.parm.TaskSettlementDetailParm
import com.flash.worker.lib.coremodel.data.req.TaskSettlementDetailReq
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnReportCancelListener
import com.flash.worker.module.business.view.activity.*
import com.flash.worker.module.business.view.adapter.TalentTaskingAdapter
import com.flash.worker.module.business.view.dialog.ReportCancelPopWindow
import kotlinx.android.synthetic.main.fragment_talent_wait_prepaid.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentTaskingFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才-任务中
 * -----------------------------------------------------------------
 */
class TalentTaskingFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener,OnReportCancelListener {

    companion object {
        fun newInstance(): TalentTaskingFragment {
            return TalentTaskingFragment()
        }
    }

    private lateinit var talentJobVM: TalentJobVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mTalentTaskingAdapter: TalentTaskingAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.fragment_talent_wait_prepaid

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

        userVM = ViewModelProvider(
                this, UserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mTalentTaskingAdapter = TalentTaskingAdapter(activity!!,this)
        mTalentTaskingAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentTaskingAdapter,mRvWaitPrepaid)
        
        mRvWaitPrepaid.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitPrepaid.setLoadMoreListener(this)
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
        body.type = 10

        talentJobVM.fetchTaskSettlementDetail(token,body)
    }

    fun sendArrivePostRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = ArrivePostParm()
        body.settlementOrderId = mTalentTaskingAdapter?.getItem(currentPosition)?.settlementOrderId

        talentJobVM.arrivePost(token,body)
    }

    fun sendRemindPrepaidRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = RemindPrepaidParm()
        body.settlementOrderId = mTalentTaskingAdapter?.getItem(currentPosition)?.settlementOrderId

        talentJobVM.remindPrepaid(token,body)
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
            mTalentTaskingAdapter?.setFooterVisible(false)
            mTalentTaskingAdapter?.clear()
            mTalentTaskingAdapter?.notifyDataSetChanged()
            mRvWaitPrepaid.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mTalentTaskingAdapter?.setFooterVisible(false)
            mTalentTaskingAdapter?.clear()
            mTalentTaskingAdapter?.notifyDataSetChanged()
            mRvWaitPrepaid.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        if (currentPage == 1) {
            mTalentTaskingAdapter?.clear()
        }
        mTalentTaskingAdapter?.setFooterVisible(true)
        mTalentTaskingAdapter?.add(datas?.data)

        mRvWaitPrepaid.setHasMore(false)
        mTalentTaskingAdapter?.setLoadState(LOADING_END)
        mTalentTaskingAdapter?.notifyDataSetChanged()
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

    fun showCheckTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "请确认您已到岗，若你未到岗，将可能面临雇主投诉！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "确认打卡"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendArrivePostRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showPrepaymentTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已成功向雇主发送提醒！为保障您的权益，每次开始工作时，请准时打卡！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "确认"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showReportTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才：\n" +
                "\t\t\t\t请确认该雇主有违约行为，" +
                "举报后雇用将立即终止，任务进入争议处理流程。"
        commonTipDialog.mCancelText = "我再想想"
        commonTipDialog.mOkText = "确定举报"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                var data = mTalentTaskingAdapter?.getItem(currentPosition)
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
        mTalentTaskingAdapter?.clear()
        mTalentTaskingAdapter?.setFooterVisible(false)
        mTalentTaskingAdapter?.notifyDataSetChanged()

        mRvWaitPrepaid.setHasMore(false)

        sendTaskSettlementDetailRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTaskSettlementDetailRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mTalentTaskingAdapter?.getItem(position)
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
                var userId = data?.employerUserId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvSubmit -> {
                NavigationUtils.goSubmitTaskActivity(activity as AppCompatActivity,data)
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

    override fun OnReport() {
        showReportTipDlg()
    }

    override fun OnCancel() {
        var jobOrderId = mTalentTaskingAdapter?.getItem(currentPosition)?.jobOrderId
        TalentBreachContractActivity.intentStart(activity as AppCompatActivity,jobOrderId,2)
    }
}