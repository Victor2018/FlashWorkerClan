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
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.Constant
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
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerReleaseVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerReleaseVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.HireUpdateReleaseActivity
import com.flash.worker.module.business.view.adapter.HireReleaseAdapter
import kotlinx.android.synthetic.main.fragment_hire_release.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireReleaseFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 
 * -----------------------------------------------------------------
 */
class HireReleaseFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(status: Int): HireReleaseFragment {
            var fragment = HireReleaseFragment()
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA_KEY,status)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    private lateinit var employerReleaseVM: EmployerReleaseVM

    /**
     * 1,编辑中;2,发布中;3,已下架；4，已驳回
     */
    var mStatus: Int = 0
    var currentPage = 1

    var mHireReleaseAdapter: HireReleaseAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    var  currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_hire_release

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        employerReleaseVM = ViewModelProvider(this, EmployerReleaseVMFactory(this))
                .get(EmployerReleaseVM::class.java)


        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mHireReleaseAdapter = HireReleaseAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mHireReleaseAdapter,mRvHireRelease)

        mRvHireRelease.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvHireRelease.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        mStatus = arguments?.getInt(Constant.INTENT_DATA_KEY,0) ?: 0
        mHireReleaseAdapter?.status = mStatus

        sendEmployerReleaseRequest()
    }

    fun subscribeUi() {
        employerReleaseVM.employerReleaseData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showHireReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.employerReleaseRefreshData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("刷新成功")
                    currentPage = 1
                    mHireReleaseAdapter?.clear()
                    mHireReleaseAdapter?.setFooterVisible(false)
                    mHireReleaseAdapter?.notifyDataSetChanged()
                    mRvHireRelease.setHasMore(false)
                    sendEmployerReleaseRequest()
                    UMengEventModule.report(activity, BussinessEmployerEvent.refresh_employment_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.employerReleaseOffShelfData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("下架成功")

                    mHireReleaseAdapter?.removeItem(currentPosition)
                    mHireReleaseAdapter?.notifyItemRemoved(currentPosition)

                    if (mHireReleaseAdapter?.getContentItemCount() == 0) {
                        mHireReleaseAdapter?.setFooterVisible(false)
                        mHireReleaseAdapter?.notifyDataSetChanged()
                        mRvHireRelease.setHasMore(false)

                        mTvNoData.visibility = View.VISIBLE
                        mRvHireRelease.visibility = View.GONE
                    }

                    UMengEventModule.report(activity, BussinessEmployerEvent.off_shelf_employment_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.employerReleaseNewReleaseData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("发布成功")
                    mHireReleaseAdapter?.removeItem(currentPosition)
                    mHireReleaseAdapter?.notifyItemRemoved(currentPosition)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.employerReleaseDeleteData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mHireReleaseAdapter?.removeItem(currentPosition)
                    mHireReleaseAdapter?.notifyItemRemoved(currentPosition)

                    UMengEventModule.report(activity, BussinessEmployerEvent.employer_delete_employment)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmployerReleaseRequest () {
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

        var body = EmployerReleaseParm()
        body.pageNum = currentPage
        body.status = mStatus

        employerReleaseVM.fetchEmployerRelease(token,body)
    }

    fun sendTalentReleaseRefreshRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmployerReleaseRefreshParm()
        body.releaseId = id

        employerReleaseVM.employerReleaseRefresh(token,body)
    }

    fun sendTalentReleaseOffShelfRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmployerReleaseOffShelfParm()
        body.releaseId = id

        employerReleaseVM.employerReleaseOffShelf(token,body)
    }

    fun sendTalentReleaseNewReleaseRequest (id: String?) {
        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmployerReleaseNewReleaseParm()
        body.releaseId = id

        employerReleaseVM.employerReleaseNewRelease(token,body)
    }

    fun sendTalentReleaseDeleteRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmployerReleaseDeleteParm()
        body.releaseId = id

        employerReleaseVM.employerReleaseDelete(token,body)
    }

    fun showHireReleaseData (datas: EmployerReleaseReq) {
        mHireReleaseAdapter?.showData(datas.data,mTvNoData,mRvHireRelease,currentPage)
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

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mHireReleaseAdapter?.clear()
        mHireReleaseAdapter?.setFooterVisible(false)
        mHireReleaseAdapter?.notifyDataSetChanged()

        mRvHireRelease.setHasMore(false)

        sendEmployerReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mHireReleaseAdapter?.getItem(position)
        when (view?.id) {
            R.id.mTvRefresh -> {
                sendTalentReleaseRefreshRequest(data?.id)
            }
            R.id.mTvOffShelf -> {
                sendTalentReleaseOffShelfRequest(data?.id)
            }
            R.id.mTvRelease -> {
                var taskType = data?.taskType ?: 0
                if (mStatus == 3) {//已下架进入编辑页
                    if (taskType == 1) {
                        HireUpdateReleaseActivity.intentStart(activity as AppCompatActivity,
                            data,mStatus)
                        UMengEventModule.report(activity, BussinessEmployerEvent.employment_release_repost)
                    } else  if (taskType == 2) {
                        NavigationUtils.goTaskUpdateReleaseAct(activity!!, data,mStatus)
                    }
                } else {
                    var taskType = data?.taskType ?: 0
                    if (taskType == 1) {
                        sendTalentReleaseNewReleaseRequest(data?.id)
                    } else  if (taskType == 2) {
                        NavigationUtils.goTaskUpdateReleaseAct(activity!!,data,mStatus)
                    }
                }
            }
            R.id.mTvDelete -> {
                sendTalentReleaseDeleteRequest(data?.id)
            }
            else -> {
                var taskType = data?.taskType
                if (mStatus == 2 || mStatus == 3) {//发布中、已下架进入详情页
                    if (taskType == 1) {
                        NavigationUtils.goJobDetailActivity(activity!!,data?.id,
                            null,null, JobDetailAction.PREVIEW)
                    } else if (taskType == 2) {
                        NavigationUtils.goTaskDetailActivity(activity!!,data?.id,
                            null,null, TaskDetailAction.PREVIEW)
                    }
                } else {
                    if (taskType == 1) {
                        HireUpdateReleaseActivity.intentStart(activity as AppCompatActivity,
                            data,mStatus)
                    } else if (taskType == 2) {
                        NavigationUtils.goTaskUpdateReleaseAct(activity as AppCompatActivity,
                            data,mStatus)
                    }
                }
            }
        }
    }

}