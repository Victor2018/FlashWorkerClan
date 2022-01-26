package com.flash.worker.module.job.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerUserCommentParm
import com.flash.worker.lib.coremodel.data.req.EmployerUserCommentReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.lib.coremodel.viewmodel.factory.CommentVMFactory
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.activity.EmployerDetailActivity
import com.flash.worker.module.job.view.adapter.EmployerUserCommentAdapter
import kotlinx.android.synthetic.main.fragment_evaluation.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EvaluationFragment
 * Author: Victor
 * Date: 2021/4/19 15:14
 * Description: 
 * -----------------------------------------------------------------
 */
class EvaluationFragment: BaseFragment(),AdapterView.OnItemClickListener, 
    SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener {

    private lateinit var commentVM: CommentVM

    var mEmployerUserCommentAdapter: EmployerUserCommentAdapter? = null
    var currentPage = 1
    
    companion object {
        fun newInstance(): EvaluationFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): EvaluationFragment {
            val fragment = EvaluationFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun getLayoutResource() = R.layout.fragment_evaluation

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    fun initialize () {
        commentVM = ViewModelProvider(this, CommentVMFactory(this))
                .get(CommentVM::class.java)


        subscribeUi()

        mEmployerUserCommentAdapter = EmployerUserCommentAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerUserCommentAdapter,mRvEvaluation)

        mRvEvaluation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEvaluation.setLoadMoreListener(this)

    }
    
    fun initData () {
        sendEmployerUserCommentRequest()
    }

    fun subscribeUi() {
        commentVM.employerUserCommentData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCommentData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmployerUserCommentRequest () {
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

        val body = EmployerUserCommentParm()
        body.pageNum = currentPage
        
        var parentAct = activity as EmployerDetailActivity

        if (parentAct.mHomeEmployerDetailData != null) {
            body.userId = parentAct.mHomeEmployerDetailData?.userId
        }
        if (parentAct.mTaskDetailData != null) {
            body.userId = parentAct.mTaskDetailData?.userId
        }
        if (parentAct.mTalentOrderReleaseInfo != null) {
            body.userId = parentAct.mTalentOrderReleaseInfo?.userId
        }

        commentVM.fetchEmployerUserComment(token,body)
    }
    
    fun showCommentData (datas: EmployerUserCommentReq) {
        mEmployerUserCommentAdapter?.showData(datas.data,mTvNoData,mRvEvaluation,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
        initData()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerUserCommentAdapter?.clear()
        mEmployerUserCommentAdapter?.setFooterVisible(false)
        mEmployerUserCommentAdapter?.notifyDataSetChanged()

        mRvEvaluation.setHasMore(false)

        sendEmployerUserCommentRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerUserCommentRequest()
    }
}