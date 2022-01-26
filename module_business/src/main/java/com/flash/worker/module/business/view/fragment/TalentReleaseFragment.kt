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
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.data.TalentDetailAction
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
import com.flash.worker.module.business.view.activity.TalentUpdateReleaseActivity
import com.flash.worker.module.business.view.adapter.TalentReleaseAdapter
import kotlinx.android.synthetic.main.fragment_talent_release.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentReleaseFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentReleaseFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    companion  object {
        fun newInstance(status: Int): TalentReleaseFragment {
            var fragment = TalentReleaseFragment()
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA_KEY,status)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    private lateinit var talentReleaseVM: TalentReleaseVM

    var mStatus: Int = 0
    var currentPage = 1

    var mTalentReleaseAdapter: TalentReleaseAdapter? = null
    var mLoadingDialog: LoadingDialog? = null

    var  currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_talent_release

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

        mTalentReleaseAdapter = TalentReleaseAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentReleaseAdapter,mRvTalentRelease)

        mRvTalentRelease.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvTalentRelease.setLoadMoreListener(this)
    }

    fun initData () {
        currentPage = 1
        mStatus = arguments?.getInt(Constant.INTENT_DATA_KEY,0) ?: 0
        mTalentReleaseAdapter?.status = mStatus

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
        talentReleaseVM.talentReleaseRefreshData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("刷新成功")
                    currentPage = 1
                    mTalentReleaseAdapter?.clear()
                    mTalentReleaseAdapter?.setFooterVisible(false)
                    mTalentReleaseAdapter?.notifyDataSetChanged()
                    mRvTalentRelease.setHasMore(false)
                    sendTalentReleaseRequest()
                    UMengEventModule.report(activity, BussinessTalentEvent.refresh_talent_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
        talentReleaseVM.talentReleaseOffShelfData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("下架成功")

                    mTalentReleaseAdapter?.removeItem(currentPosition)
                    mTalentReleaseAdapter?.notifyItemRemoved(currentPosition)

                    if (mTalentReleaseAdapter?.getContentItemCount() == 0) {
                        mTalentReleaseAdapter?.setFooterVisible(false)
                        mTalentReleaseAdapter?.notifyDataSetChanged()
                        mRvTalentRelease.setHasMore(false)

                        mTvNoData.visibility = View.VISIBLE
                        mRvTalentRelease.visibility = View.GONE
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.talentReleaseNewReleaseData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("发布成功")
                    mTalentReleaseAdapter?.removeItem(currentPosition)
                    mTalentReleaseAdapter?.notifyItemRemoved(currentPosition)

                    if (mTalentReleaseAdapter?.getContentItemCount() == 0) {
                        mTalentReleaseAdapter?.setFooterVisible(false)
                        mTalentReleaseAdapter?.notifyDataSetChanged()
                        mRvTalentRelease.setHasMore(false)

                        mTvNoData.visibility = View.VISIBLE
                        mRvTalentRelease.visibility = View.GONE
                    }
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
                    mTalentReleaseAdapter?.removeItem(currentPosition)
                    mTalentReleaseAdapter?.notifyItemRemoved(currentPosition)

                    if (mTalentReleaseAdapter?.getContentItemCount() == 0) {
                        mTalentReleaseAdapter?.setFooterVisible(false)
                        mTalentReleaseAdapter?.notifyDataSetChanged()
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
        body.status = mStatus

        talentReleaseVM.fetchTalentJobRelease(token,body)
    }

    fun sendTalentReleaseRefreshRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentReleaseRefreshParm()
        body.releaseId = id

        talentReleaseVM.talentJobReleaseRefresh(token,body)
    }

    fun sendTalentReleaseOffShelfRequest (id: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentReleaseOffShelfParm()
        body.releaseId = id

        talentReleaseVM.talentJobReleaseOffShelf(token,body)
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

        var body = TalentReleaseNewReleaseParm()
        body.releaseId = id

        talentReleaseVM.talentJobReleaseNewRelease(token,body)
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
        mTalentReleaseAdapter?.showData(datas.data,mTvNoData,mRvTalentRelease,currentPage)
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
        mTalentReleaseAdapter?.clear()
        mTalentReleaseAdapter?.setFooterVisible(false)
        mTalentReleaseAdapter?.notifyDataSetChanged()

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
            R.id.mTvRefresh -> {
                sendTalentReleaseRefreshRequest(mTalentReleaseAdapter?.getItem(position)?.id)
            }
            R.id.mTvOffShelf -> {
                sendTalentReleaseOffShelfRequest(mTalentReleaseAdapter?.getItem(position)?.id)
            }
            R.id.mTvRelease -> {
                var data = mTalentReleaseAdapter?.getItem(position)
                TalentUpdateReleaseActivity.intentStart(activity as AppCompatActivity,
                        mTalentReleaseAdapter?.getItem(position),data?.status ?: 0)
//                sendTalentReleaseNewReleaseRequest(mTalentReleaseAdapter?.getItem(position)?.id)

                UMengEventModule.report(activity, BussinessTalentEvent.talent_release_repost)
            }
            R.id.mTvDelete -> {
                sendTalentReleaseDeleteRequest(mTalentReleaseAdapter?.getItem(position)?.id)
            }
            else -> {
                var data = mTalentReleaseAdapter?.getItem(position)

                if (data?.status == 1) {
                    TalentUpdateReleaseActivity.intentStart(activity as AppCompatActivity,
                        mTalentReleaseAdapter?.getItem(position),data?.status ?: 0)
                } else if (data?.status == 2) {
                    NavigationUtils.goTalentDetailActivity(activity!!,data.id, TalentDetailAction.RELEASE_PREVIEW)
                }
            }
        }
    }
}