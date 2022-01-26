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
import com.flash.worker.lib.coremodel.data.parm.EmployerWaitCommentParm
import com.flash.worker.lib.coremodel.data.req.EmployerWaitCommentReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.AttendanceRecordsActivity
import com.flash.worker.module.business.view.activity.EmployerEvaluationActivity
import com.flash.worker.module.business.view.activity.EmployerWaitCommentActivity
import com.flash.worker.module.business.view.adapter.EmployerWaitCommentAdapter
import kotlinx.android.synthetic.main.fragment_employer_wait_comment.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerWaitCommentFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主-待评价
 * -----------------------------------------------------------------
 */
class EmployerWaitCommentFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(): EmployerWaitCommentFragment {
            return EmployerWaitCommentFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerWaitCommentAdapter: EmployerWaitCommentAdapter? = null
    var currentPosition: Int = -1
    var passwordErrorCount: Int = 0//密码输入错误次数

    override fun getLayoutResource() = R.layout.fragment_employer_wait_comment

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

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mEmployerWaitCommentAdapter = EmployerWaitCommentAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerWaitCommentAdapter,mRvWaitComment)

        mRvWaitComment.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvWaitComment.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendEmployerWaitCommentRequest()
    }

    fun subscribeUi() {
        employerJobVM.employerWaitCommentData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerWaitComment(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendEmployerWaitCommentRequest () {
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

        var body = EmployerWaitCommentParm()
        body.pageNum = currentPage

        employerJobVM.fetchEmployerWaitComment(token,body)
    }


    fun showEmployerWaitComment (datas: EmployerWaitCommentReq) {
        mEmployerWaitCommentAdapter?.showData(datas.data,mTvNoData,mRvWaitComment,currentPage)
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
        mEmployerWaitCommentAdapter?.clear()
        mEmployerWaitCommentAdapter?.setFooterVisible(false)
        mEmployerWaitCommentAdapter?.notifyDataSetChanged()

        mRvWaitComment.setHasMore(false)

        sendEmployerWaitCommentRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerWaitCommentRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mEmployerWaitCommentAdapter?.getItem(position)
        when (view?.id) {
            R.id.mTvCheckInRecord -> {
                AttendanceRecordsActivity.intentStart(activity as AppCompatActivity,data)
            }
            R.id.mTvEvaluationAll -> {
                EmployerEvaluationActivity.intentStart(activity as AppCompatActivity,
                    mEmployerWaitCommentAdapter?.getItem(position),null)
            }
            R.id.mTvDetail -> {
                var releaseId = data?.employerReleaseId
                var taskType = data?.taskType ?: 0
                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(activity as AppCompatActivity,
                        releaseId, null,null, JobDetailAction.PREVIEW)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(activity as AppCompatActivity,
                        releaseId, null,null, TaskDetailAction.PREVIEW)
                }
            }
            else -> {
                EmployerWaitCommentActivity.intentStart(activity as AppCompatActivity,
                    mEmployerWaitCommentAdapter?.getItem(position))
            }
        }
    }

}