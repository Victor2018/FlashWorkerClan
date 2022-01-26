package com.flash.worker.module.business.view.fragment

import android.os.Bundle
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
import com.flash.worker.lib.coremodel.data.parm.EmployConfirmDetailParm
import com.flash.worker.lib.coremodel.data.parm.EmployerRefuseParm
import com.flash.worker.lib.coremodel.data.parm.TalentUserParm
import com.flash.worker.lib.coremodel.data.req.TalentUserReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.EmploymentVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.EmploymentVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnCreditFreezeListener
import com.flash.worker.module.business.interfaces.OnRefuseEmployListener
import com.flash.worker.module.business.view.activity.EmployerWaitEmployActivity
import com.flash.worker.module.business.view.activity.EmploymentBondActivity
import com.flash.worker.module.business.view.adapter.SignedUpUserAdapter
import com.flash.worker.module.business.view.dialog.CreditFreezePopWindow
import kotlinx.android.synthetic.main.fragment_signed_up.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentUserFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description:
 * -----------------------------------------------------------------
 */
class SignedUpFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnRefuseEmployListener, OnCreditFreezeListener {

    companion object {
        fun newInstance(data: EmployerWaitEmployInfo?): SignedUpFragment {
            var fragment = SignedUpFragment()
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA_KEY,data)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var employmentVM: EmploymentVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mSignedUpUserAdapter: SignedUpUserAdapter? = null
    var currentPosition: Int = -1

    var mEmployerWaitEmployInfo: EmployerWaitEmployInfo? = null

    override fun getLayoutResource() = R.layout.fragment_signed_up

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

        userVM = ViewModelProvider(
                this, UserVMFactory(this))
                .get(UserVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()
        subscribeEvent()

        mSignedUpUserAdapter = SignedUpUserAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSignedUpUserAdapter,mRvSignedUp)

        mRvSignedUp.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvSignedUp.setLoadMoreListener(this)

        mTvEmployAll.setOnClickListener(this)
    }

    fun initData () {
        currentPage = 1
        mEmployerWaitEmployInfo = arguments?.getSerializable(Constant.INTENT_DATA_KEY)
                as EmployerWaitEmployInfo

        sendTalentUserRequest()

        var parentAct = activity as EmployerWaitEmployActivity
        parentAct.sendEmploymentNumRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.REFRESH_E_SIGNED_UP_USER)
                .observe(this, Observer {
                    currentPage = 1
                    mSignedUpUserAdapter?.checkMap?.clear()
                    mTvCheckCount.text = "(0/5)"
                    mTvEmployAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))

                    mSignedUpUserAdapter?.clear()
                    mSignedUpUserAdapter?.setFooterVisible(false)
                    mSignedUpUserAdapter?.notifyDataSetChanged()

                    mRvSignedUp.setHasMore(false)

                    sendTalentUserRequest()
                })
    }

    fun subscribeUi() {
        employerJobVM.talentUserData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentUserData(it.value)
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

        employmentVM.employerRefuseData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("成功拒绝人才")

                    mSignedUpUserAdapter?.removeItem(currentPosition)
                    mSignedUpUserAdapter?.notifyItemRemoved(currentPosition)

                    if (mSignedUpUserAdapter?.getContentItemCount() == 0) {
                        mTvNoData.visibility = View.VISIBLE
                        mRvSignedUp.visibility = View.GONE
                    }

                    var parentAct = activity as EmployerWaitEmployActivity
                    parentAct.sendEmploymentNumRequest()
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
        body.status = 1
        body.employerReleaseId = mEmployerWaitEmployInfo?.employerReleaseId

        employerJobVM.fetchTalentUser(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun sendEmployerRefuseRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerRefuseParm()
        body.id = mSignedUpUserAdapter?.getItem(currentPosition)?.id

        employmentVM.employerRefuse(token,body)
    }

    fun showTalentUserData (datas: TalentUserReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mSignedUpUserAdapter?.setFooterVisible(false)
            mSignedUpUserAdapter?.clear()
            mSignedUpUserAdapter?.notifyDataSetChanged()
            mRvSignedUp.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mSignedUpUserAdapter?.setFooterVisible(false)
            mSignedUpUserAdapter?.clear()
            mSignedUpUserAdapter?.notifyDataSetChanged()
            mRvSignedUp.setHasMore(false)
            return
        }
        if (datas.data?.list == null) {
            mTvNoData.visibility = View.VISIBLE
            mSignedUpUserAdapter?.setFooterVisible(false)
            mSignedUpUserAdapter?.clear()
            mSignedUpUserAdapter?.notifyDataSetChanged()
            mRvSignedUp.setHasMore(false)
            return
        }
        if (datas.data?.list?.size == 0) {
            if (currentPage == 1) {
                mTvNoData.visibility = View.VISIBLE
                mSignedUpUserAdapter?.setFooterVisible(false)
                mSignedUpUserAdapter?.clear()
                mSignedUpUserAdapter?.notifyDataSetChanged()
                mRvSignedUp.setHasMore(false)
                return
            }
        }
        mTvNoData.visibility = View.GONE
        if (currentPage == 1) {
            mSignedUpUserAdapter?.clear()
        }
        mSignedUpUserAdapter?.setFooterVisible(true)
        mSignedUpUserAdapter?.add(datas?.data?.list)

        var count = datas?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvSignedUp.setHasMore(false)
            mSignedUpUserAdapter?.setLoadState(LOADING_END)
        } else {
            mRvSignedUp.setHasMore(true)
            mSignedUpUserAdapter?.setLoadState(LOADING)
        }
        mSignedUpUserAdapter?.notifyDataSetChanged()
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

    fun showRefuseTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您确认要拒绝该名人才的报名吗？"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确认"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendEmployerRefuseRequest()
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
        mSignedUpUserAdapter?.clear()
        mSignedUpUserAdapter?.setFooterVisible(false)
        mSignedUpUserAdapter?.notifyDataSetChanged()

        mRvSignedUp.setHasMore(false)

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
        var data = mSignedUpUserAdapter?.getItem(position)
        when (view?.id) {
            R.id.mChkCheck -> {
                var isChecked = mSignedUpUserAdapter?.isItemChecked(data!!)
                if (isChecked!!) {
                    mSignedUpUserAdapter?.checkMap?.remove(data?.id)
                } else {
                    if (mSignedUpUserAdapter?.checkMap?.size == 5) {
                        ToastUtils.show("最多只能选择5个人才")
                        return
                    }
                    mSignedUpUserAdapter?.checkMap?.put(data?.id!!,data!!)
                }
                mSignedUpUserAdapter?.notifyDataSetChanged()

                var count = mSignedUpUserAdapter?.checkMap?.size ?: 0
                mTvCheckCount.text = "($count/5)"

                if (count > 0) {
                    mTvEmployAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
                } else {
                    mTvEmployAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mTvCreditFreeze -> {
                var frozenAmt = data?.signupFrozenAmount!!
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_30) * -1.0f)
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())

                var creditFreezePopWindow = CreditFreezePopWindow(activity,frozenAmt)
                creditFreezePopWindow.mOnCreditFreezeListener = this
                creditFreezePopWindow.show(view!!, AbsPopWindow.LocationGravity.TOP_CENTER,xOffset, yOffset)
            }
            /*R.id.mIvMore.toLong() -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_8).toFloat())
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var refuseEmployPopWindow = RefuseEmployPopWindow(activity)
                refuseEmployPopWindow.mOnRefuseEmployListener = this
                refuseEmployPopWindow.show(view!!, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }*/
            R.id.mTvContactEmployer -> {
                var userId =  data?.talentUserId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvEmploy -> {
                var id = data?.id
                EmploymentBondActivity.intentStart(
                        activity as AppCompatActivity,
                        getEmployConfirmRequestParm(id,true),mEmployerWaitEmployInfo,data)
            }
            else -> {
                var resumeId =  data?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
    }

    override fun OnRefuseEmploy() {
        showRefuseTipDlg()
    }

    override fun OnCreditFreeze() {
    }

    fun getEmployConfirmRequestParm (id: String?,isSingleCheck: Boolean): EmployConfirmDetailParm {
        var body = EmployConfirmDetailParm()
        var jobOrderIds = ArrayList<String>()
        body.employerReleaseId = mEmployerWaitEmployInfo?.employerReleaseId

        if (isSingleCheck) {
            id?.let { jobOrderIds.add(it) }
        } else {
            var datas = mSignedUpUserAdapter?.checkMap
            datas?.forEach {
                jobOrderIds.add(it.key)
            }
        }
        body.jobOrderIds = jobOrderIds
        return body
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvEmployAll -> {
                if (mSignedUpUserAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择雇用人才")
                    return
                }
                if (currentPosition == -1) {
                    ToastUtils.show("请选择雇用人才")
                    return
                }

                var data = mSignedUpUserAdapter?.getItem(currentPosition)
                EmploymentBondActivity.intentStart(
                        activity as AppCompatActivity,
                        getEmployConfirmRequestParm(null,false),mEmployerWaitEmployInfo,data)
            }
        }
    }
}