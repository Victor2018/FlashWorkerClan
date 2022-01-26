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
import com.flash.worker.module.business.view.adapter.HireRejectedReleaseAdapter
import kotlinx.android.synthetic.main.fragment_hire_rejected_release.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireRejectedReleaseFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 雇用发布已驳回
 * -----------------------------------------------------------------
 */
class HireRejectedReleaseFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion object {
        fun newInstance(): HireRejectedReleaseFragment {
            var fragment = HireRejectedReleaseFragment()
            return fragment
        }
    }

    private lateinit var employerReleaseVM: EmployerReleaseVM

    var currentPage = 1

    var mHireRejectedReleaseAdapter: HireRejectedReleaseAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    var  currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_hire_rejected_release

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

        mHireRejectedReleaseAdapter = HireRejectedReleaseAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mHireRejectedReleaseAdapter,mRvHireRelease)

        mRvHireRelease.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvHireRelease.setLoadMoreListener(this)
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

        employerReleaseVM.employerReleaseDeleteData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mHireRejectedReleaseAdapter?.removeItem(currentPosition)
                    mHireRejectedReleaseAdapter?.notifyItemRemoved(currentPosition)

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
        body.status = 4

        employerReleaseVM.fetchEmployerRelease(token,body)
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
        mHireRejectedReleaseAdapter?.showData(datas.data,mTvNoData,mRvHireRelease,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mHireRejectedReleaseAdapter?.clear()
        mHireRejectedReleaseAdapter?.setFooterVisible(false)
        mHireRejectedReleaseAdapter?.notifyDataSetChanged()

        mRvHireRelease.setHasMore(false)

        sendEmployerReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mTvDelete -> {
                sendTalentReleaseDeleteRequest(mHireRejectedReleaseAdapter?.getItem(position)?.id)
            }
        }
    }

}