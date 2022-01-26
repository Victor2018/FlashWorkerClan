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
import com.flash.worker.lib.coremodel.data.parm.EmployerSettlementOrderParm
import com.flash.worker.lib.coremodel.data.parm.SettlementNumParm
import com.flash.worker.lib.coremodel.data.req.EmployerSettlementOrderReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.EmployerEmployingActivity
import com.flash.worker.module.business.view.adapter.EmployerSettledAdapter
import kotlinx.android.synthetic.main.fragment_employer_settled.*
import kotlinx.android.synthetic.main.rv_employer_settled_cell.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerSettledFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇主-已结算
 * -----------------------------------------------------------------
 */
class EmployerSettledFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(): EmployerSettledFragment {
            return EmployerSettledFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM
    private lateinit var userVM: UserVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerSettledAdapter: EmployerSettledAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_settled

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

        mEmployerSettledAdapter = EmployerSettledAdapter(activity!!,this)
        mEmployerSettledAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerSettledAdapter,mRvSettled)

        mRvSettled.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvSettled.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendSettlementNumRequest()
        sendSettlementOrderRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.REFRESH_E_SETTLED)
                .observe(this, Observer {
                    currentPage = 1
                    mEmployerSettledAdapter?.clear()
                    mEmployerSettledAdapter?.setFooterVisible(false)
                    mEmployerSettledAdapter?.notifyDataSetChanged()

                    mRvSettled.setHasMore(false)

                    initData()
                })
    }

    fun subscribeUi() {
        employerJobVM.employerSettlementOrderData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSettlementOrderData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.settlementNumData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mEmployerSettledAdapter?.headerData = it.value.data
                    mEmployerSettledAdapter?.notifyDataSetChanged()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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
        body.type = 30
        body.pageNum = currentPage
        body.arrivedStatus = 0
        body.finishedStatus = 0

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

    fun showSettlementOrderData (datas: EmployerSettlementOrderReq) {
        mEmployerSettledAdapter?.showData(datas.data,mTvNoData,mRvSettled,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerSettledAdapter?.clear()
        mEmployerSettledAdapter?.setFooterVisible(false)
        mEmployerSettledAdapter?.notifyDataSetChanged()

        mRvSettled.setHasMore(false)

        initData()
    }

    override fun OnLoadMore() {
        currentPage++
        sendSettlementOrderRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position

        when (view?.id) {
            R.id.mClHeaderRoot -> {

            }
            R.id.mTvJobOrderId -> {
                var jobOrderId = mEmployerSettledAdapter?.getItem(position)?.jobOrderId
                ClipboardUtil.copy(activity, Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
            else -> {
                var resumeId =  mEmployerSettledAdapter?.getItem(position)?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(activity as AppCompatActivity,resumeId)
            }
        }
    }
}