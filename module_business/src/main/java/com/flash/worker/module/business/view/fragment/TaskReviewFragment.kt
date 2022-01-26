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
import com.flash.worker.module.business.view.adapter.TaskReviewAdapter
import kotlinx.android.synthetic.main.fragment_task_review.*


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
class TaskReviewFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(): TaskReviewFragment {
            var fragment = TaskReviewFragment()
            return fragment
        }
    }

    private lateinit var employerReleaseVM: EmployerReleaseVM

    /**
     * 1,编辑中;2,发布中;3,已下架；4，已驳回,5-已关闭；6-审核中
     */
    var currentPage = 1

    var mTaskReviewAdapter: TaskReviewAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.fragment_task_review

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

        mTaskReviewAdapter = TaskReviewAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTaskReviewAdapter,mRvReview)

        mRvReview.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvReview.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
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
        body.status = 6

        employerReleaseVM.fetchEmployerRelease(token,body)
    }

    fun showHireReleaseData (datas: EmployerReleaseReq) {
        mTaskReviewAdapter?.showData(datas.data,mTvNoData,mRvReview,currentPage)
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
        mTaskReviewAdapter?.clear()
        mTaskReviewAdapter?.setFooterVisible(false)
        mTaskReviewAdapter?.notifyDataSetChanged()

        mRvReview.setHasMore(false)

        sendEmployerReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

}