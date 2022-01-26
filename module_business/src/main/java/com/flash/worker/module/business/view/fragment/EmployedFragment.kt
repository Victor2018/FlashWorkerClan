package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitEmployInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentUserParm
import com.flash.worker.lib.coremodel.data.req.TalentUserReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnCreditFreezeListener
import com.flash.worker.module.business.interfaces.OnRefuseEmployListener
import com.flash.worker.module.business.view.activity.AuthenticationActivity
import com.flash.worker.module.business.view.activity.EmployerWaitEmployActivity
import com.flash.worker.module.business.view.adapter.EmployedUserAdapter
import com.flash.worker.module.business.view.dialog.CreditFreezePopWindow
import kotlinx.android.synthetic.main.fragment_employed.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployedFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description:
 * -----------------------------------------------------------------
 */
class EmployedFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnRefuseEmployListener, OnCreditFreezeListener {

    companion object {
        fun newInstance(data: EmployerWaitEmployInfo?): EmployedFragment {
            var fragment = EmployedFragment()
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA_KEY,data)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployedUserAdapter: EmployedUserAdapter? = null
    var currentPosition: Int = -1

    var mEmployerWaitEmployInfo: EmployerWaitEmployInfo? = null

    override fun getLayoutResource() = R.layout.fragment_employed

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

        subscribeUi()

        mEmployedUserAdapter = EmployedUserAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployedUserAdapter,mRvEmployed)
        
        mRvEmployed.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmployed.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        mEmployerWaitEmployInfo = arguments?.getSerializable(Constant.INTENT_DATA_KEY)
                as EmployerWaitEmployInfo

        sendTalentUserRequest()

        var parentAct = activity as EmployerWaitEmployActivity
        parentAct.sendEmploymentNumRequest()
    }

    fun subscribeUi() {
        employerJobVM.talentUserData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployedData(it.value)
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

    fun sendTalentUserRequest () {
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

        var body = TalentUserParm()
        body.pageNum = currentPage
        body.status = 3
        body.employerReleaseId = mEmployerWaitEmployInfo?.employerReleaseId

        employerJobVM.fetchTalentUser(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun showEmployedData (datas: TalentUserReq) {
        mEmployedUserAdapter?.showData(datas.data,mTvNoData,mRvEmployed,currentPage)
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

    fun showCloseJobTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "关闭该岗位后，若有已发布\n" +
                "的雇用信息将同步下架。"
        commonTipDialog.mCancelText = "暂不关闭"
        commonTipDialog.mOkText = "确认关闭"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
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
        mEmployedUserAdapter?.clear()
        mEmployedUserAdapter?.setFooterVisible(false)
        mEmployedUserAdapter?.notifyDataSetChanged()

        mRvEmployed.setHasMore(false)

        sendTalentUserRequest()

        var parentAct = activity as EmployerWaitEmployActivity
        parentAct.sendEmploymentNumRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentUserRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mTvCreditFreeze-> {
                var frozenAmt = mEmployedUserAdapter?.getItem(position)?.signupFrozenAmount ?: 0.0
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_30) * -1.0f)
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())

                var creditFreezePopWindow = CreditFreezePopWindow(activity,frozenAmt)
                creditFreezePopWindow.mOnCreditFreezeListener = this
                creditFreezePopWindow.show(view?.findViewById(R.id.mTvCreditFreeze)!!, AbsPopWindow.LocationGravity.TOP_CENTER,xOffset, yOffset)
            }
            R.id.mTvContactEmployer -> {
                var userId =  mEmployedUserAdapter?.getItem(position)?.talentUserId
                sendImLoginInfoRequest(userId)
            }
            else -> {
                var resumeId =  mEmployedUserAdapter?.getItem(position)?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
    }

    override fun OnRefuseEmploy() {
        ToastUtils.show("拒绝雇用，develop...")
    }

    override fun OnCreditFreeze() {
    }
}