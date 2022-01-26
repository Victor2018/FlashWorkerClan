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
import com.flash.worker.lib.common.util.ClipboardUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerCancelledParm
import com.flash.worker.lib.coremodel.data.req.EmployerCancelledReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.EmployerTaskEmployingActivity
import com.flash.worker.module.business.view.adapter.EmployerTaskCancelledAdapter
import kotlinx.android.synthetic.main.fragment_employer_cancelled.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettledFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主任务-已解约
 * -----------------------------------------------------------------
 */
class EmployerTaskCancelledFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(): EmployerTaskCancelledFragment {
            return EmployerTaskCancelledFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerTaskCancelledAdapter: EmployerTaskCancelledAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_cancelled

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
        subscribeEvent()

        mEmployerTaskCancelledAdapter = EmployerTaskCancelledAdapter(activity!!,this)
        mEmployerTaskCancelledAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerTaskCancelledAdapter,mRvEmployerCancelled)

        mRvEmployerCancelled.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmployerCancelled.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendEmploymentNumRequest()
        sendEmployerCancelledRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.REFRESH_E_TC)
                .observe(this, Observer {
                    currentPage = 1
                    mEmployerTaskCancelledAdapter?.clear()
                    mEmployerTaskCancelledAdapter?.setFooterVisible(false)
                    mEmployerTaskCancelledAdapter?.notifyDataSetChanged()

                    mRvEmployerCancelled.setHasMore(false)

                    initData()
                })
    }

    fun subscribeUi() {
        employerJobVM.employmentNumData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mEmployerTaskCancelledAdapter?.headerData = it.value.data
                    mEmployerTaskCancelledAdapter?.notifyDataSetChanged()

                    var parentAct = activity as EmployerTaskEmployingActivity
                    parentAct.setPagerTabTitle(3,it.value.data?.cancelContractNum ?: 0)

                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.employerCancelledData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCancelledData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendEmploymentNumRequest () {
        mSrlRefresh.isRefreshing = false
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var parentAct = activity as EmployerTaskEmployingActivity
        var employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId

        employerJobVM.fetchEmploymentNum(token,employerReleaseId)
    }

    fun sendEmployerCancelledRequest () {
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

        var parentAct = activity as EmployerTaskEmployingActivity

        var body = EmployerCancelledParm()
        body.pageNum = currentPage
        body.employerReleaseId = parentAct.mEmployerEmployingInfo?.employerReleaseId

        employerJobVM.fetchEmployerCancelled(token,body)
    }

    fun showCancelledData (datas: EmployerCancelledReq) {
        mEmployerTaskCancelledAdapter?.showData(datas.data,mTvNoData,mRvEmployerCancelled,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerTaskCancelledAdapter?.clear()
        mEmployerTaskCancelledAdapter?.setFooterVisible(false)
        mEmployerTaskCancelledAdapter?.notifyDataSetChanged()

        mRvEmployerCancelled.setHasMore(false)

        sendEmploymentNumRequest()
        sendEmployerCancelledRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerCancelledRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mClHeaderRoot -> {
            }
            R.id.mTvJobOrderId -> {
                var jobOrderId = mEmployerTaskCancelledAdapter?.getItem(position)?.jobOrderId
                ClipboardUtil.copy(activity, Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
            else -> {
                var resumeId =  mEmployerTaskCancelledAdapter?.getItem(position)?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }
}