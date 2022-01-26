package com.flash.worker.module.job.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerReleasingParm
import com.flash.worker.lib.coremodel.data.req.EmployerReleasingReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.activity.EmployerDetailActivity
import com.flash.worker.module.job.view.activity.JobDetailActivity
import com.flash.worker.module.job.view.adapter.EmployerReleasingAdapter
import kotlinx.android.synthetic.main.fragment_employing.*


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
class EmployingFragment: BaseFragment(), AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener {
    companion object {
        fun newInstance(): EmployingFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): EmployingFragment {
            val fragment = EmployingFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    private lateinit var employerJobVM: EmployerJobVM

    var mEmployerReleasingAdapter: EmployerReleasingAdapter? = null
    var currentPage = 1

    override fun getLayoutResource() = R.layout.fragment_employing

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    fun initialize () {
        employerJobVM = ViewModelProvider(this, EmployerJobVMFactory(this))
                .get(EmployerJobVM::class.java)

        subscribeUi()

        mEmployerReleasingAdapter = EmployerReleasingAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerReleasingAdapter,mRvEmploying)

        mRvEmploying.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEmploying.setLoadMoreListener(this)
    }

    fun initData () {
        sendEmployerReleasingRequest()
    }

    fun subscribeUi() {
        employerJobVM.employerReleasingData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerReleasingData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmployerReleasingRequest () {
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

        val body = EmployerReleasingParm()
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

        employerJobVM.fetchEmployerReleasing(token,body)
    }

    fun showEmployerReleasingData (datas: EmployerReleasingReq) {
        var parentAct = activity as EmployerDetailActivity
        parentAct.setPagerTabTitle(datas.data?.total ?: 0)
        mEmployerReleasingAdapter?.showData(datas.data,mTvNoData,mRvEmploying,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
        initData()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var taskType = mEmployerReleasingAdapter?.getItem(position)?.taskType ?: 0
        if (taskType == 1) {
            JobDetailActivity.intentStart(activity as AppCompatActivity,
                mEmployerReleasingAdapter?.getItem(position)?.releaseId,
                null,null, JobDetailAction.NORMAL)
        } else if (taskType == 2) {
            NavigationUtils.goTaskDetailActivity(activity as AppCompatActivity,
                mEmployerReleasingAdapter?.getItem(position)?.releaseId,
                null,null, TaskDetailAction.NORMAL)
        }

    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerReleasingAdapter?.clear()
        mEmployerReleasingAdapter?.setFooterVisible(false)
        mEmployerReleasingAdapter?.notifyDataSetChanged()

        mRvEmploying.setHasMore(false)

        sendEmployerReleasingRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerReleasingRequest()
    }
}