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
import com.flash.worker.lib.common.event.BussinessTalentEvent
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
import com.flash.worker.lib.coremodel.data.req.TalentReleaseReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.TalentReleaseVM
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentReleaseVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.TalentRejectedReleaseAdapter
import kotlinx.android.synthetic.main.fragment_talent_rejected_release.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentRejectedReleaseFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才已驳回接活
 * -----------------------------------------------------------------
 */
class TalentRejectedReleaseFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion  object {
        fun newInstance(): TalentRejectedReleaseFragment {
            var fragment = TalentRejectedReleaseFragment()
            return fragment
        }
    }

    private lateinit var talentReleaseVM: TalentReleaseVM

    var currentPage = 1

    var mTalentRejectedReleaseAdapter: TalentRejectedReleaseAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    var  currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_talent_rejected_release

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        talentReleaseVM = ViewModelProvider(
                this, TalentReleaseVMFactory(this))
                .get(TalentReleaseVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mTalentRejectedReleaseAdapter = TalentRejectedReleaseAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentRejectedReleaseAdapter,mRvTalentRelease)

        mRvTalentRelease.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvTalentRelease.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        sendTalentReleaseRequest()
    }

    fun subscribeUi() {
        talentReleaseVM.talentReleaseData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.talentReleaseDeleteData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mTalentRejectedReleaseAdapter?.removeItem(currentPosition)
                    mTalentRejectedReleaseAdapter?.notifyItemRemoved(currentPosition)

                    if (mTalentRejectedReleaseAdapter?.getContentItemCount() == 0) {
                        mTalentRejectedReleaseAdapter?.setFooterVisible(false)
                        mTalentRejectedReleaseAdapter?.notifyDataSetChanged()
                        mRvTalentRelease.setHasMore(false)

                        mTvNoData.visibility = View.VISIBLE
                        mRvTalentRelease.visibility = View.GONE
                    }

                    UMengEventModule.report(activity, BussinessTalentEvent.talent_release_delete)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTalentReleaseRequest () {
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

        var body = TalentReleaseParm()
        body.pageNum = currentPage
        body.status = 4

        talentReleaseVM.fetchTalentJobRelease(token,body)
    }

    fun sendTalentReleaseDeleteRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentReleaseDeleteParm()
        body.releaseId = id

        talentReleaseVM.talentJobReleaseDelete(token,body)
    }

    fun showTalentReleaseData (datas: TalentReleaseReq) {
        mTalentRejectedReleaseAdapter?.showData(datas.data,mTvNoData,mRvTalentRelease,currentPage)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        currentPage = 1
        mTalentRejectedReleaseAdapter?.clear()
        mTalentRejectedReleaseAdapter?.setFooterVisible(false)
        mTalentRejectedReleaseAdapter?.notifyDataSetChanged()

        mRvTalentRelease.setHasMore(false)

        sendTalentReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mTvDelete -> {
                sendTalentReleaseDeleteRequest(mTalentRejectedReleaseAdapter?.getItem(position)?.id)
            }
        }
    }
}