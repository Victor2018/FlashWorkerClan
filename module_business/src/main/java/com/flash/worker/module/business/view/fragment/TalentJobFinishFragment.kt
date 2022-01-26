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
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.ComplexAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TalentJobFinishInfo
import com.flash.worker.lib.coremodel.data.parm.TalentJobDeleteParm
import com.flash.worker.lib.coremodel.data.parm.TalentJobFinishParm
import com.flash.worker.lib.coremodel.data.req.TalentJobFinishReq
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.TalentJobVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.TalentOrderDetailActivity
import com.flash.worker.module.business.view.adapter.TalentJobFinishAdapter
import kotlinx.android.synthetic.main.fragment_talent_job_finish.*
import kotlinx.android.synthetic.main.fragment_talent_job_finish_content.*
import kotlinx.android.synthetic.main.talent_job_finish_menu.view.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentCancelledFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才 已结束
 * -----------------------------------------------------------------
 */
class TalentJobFinishFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener,
        OnDropDownMenuClickListener {

    companion object {
        fun newInstance(): TalentJobFinishFragment {
            return TalentJobFinishFragment()
        }
    }

    private lateinit var talentJobVM: TalentJobVM

    var headers: Array<String>? = null
    var popupViews: MutableList<View> = ArrayList()
    var balanceDetailView: View? = null
    var balanceContentView: View? = null
    var mComplexAdapter: ComplexAdapter? = null
    var mJobType: Int? = null//筛选条件

    var currentPage = 1
    var mLoadingDialog: LoadingDialog? = null
    var mTalentJobFinishAdapter: TalentJobFinishAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_talent_job_finish

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        talentJobVM = ViewModelProvider(this, TalentJobVMFactory(this))
                .get(TalentJobVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        headers = ResUtils.getStringArrayRes(R.array.business_talent_job_finish_menu)

        balanceDetailView = layoutInflater.inflate(R.layout.talent_job_finish_menu, null)

        mComplexAdapter = ComplexAdapter(activity!!,this)
        balanceDetailView?.mRvBalanceDetailFilter?.adapter = mComplexAdapter

        popupViews.clear()
        popupViews.add(balanceDetailView!!)
        balanceContentView = layoutInflater.inflate(R.layout.fragment_talent_job_finish_content, null)
        //init dropdownview
        mTalentFinishDropDownMenu.tabMenuView?.removeAllViews()
        mTalentFinishDropDownMenu.popupMenuViews?.removeAllViews()
        mTalentFinishDropDownMenu.isFillWidth = false
        mTalentFinishDropDownMenu.setDropDownMenu(headers?.toList()!!, popupViews, balanceContentView)
        mTalentFinishDropDownMenu.mOnDropDownMenuClickListener = this

        mTalentJobFinishAdapter = TalentJobFinishAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentJobFinishAdapter,mRvJobFinish)

        mRvJobFinish.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvJobFinish.setLoadMoreListener(this)

        mTvDeleteAll.setOnClickListener(this)
    }

    fun initData () {
        currentPage = 1
        var complexData = ResUtils.getStringArrayRes(R.array.business_talent_job_finish_titles)
        mComplexAdapter?.clear()
        mComplexAdapter?.add(complexData?.toList())
        mComplexAdapter?.notifyDataSetChanged()

        sendTalentJobFinishRequest()
    }

    fun subscribeUi() {
        talentJobVM.talentJobFinishData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentJobFinishData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentJobVM.talentJobDeleteData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mTvCheckCount.text = "(0/5)"
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))

                    mTalentJobFinishAdapter?.checkMap?.clear()
                    currentPage = 1
                    mTalentJobFinishAdapter?.clear()
                    mTalentJobFinishAdapter?.setFooterVisible(false)
                    mTalentJobFinishAdapter?.notifyDataSetChanged()

                    mRvJobFinish.setHasMore(false)

                    sendTalentJobFinishRequest()

                    UMengEventModule.report(activity, BussinessTalentEvent.talent_delete_order)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendTalentJobFinishRequest () {
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

        var body = TalentJobFinishParm()
        body.pageNum = currentPage
        body.type = mJobType

        talentJobVM.fetchTalentJobFinish(token,body)
    }

    fun sendTalentJobDeleteRequest (body: TalentJobDeleteParm?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentJobVM.talentJobDelete(token,body)
    }

    fun getTalentJobDeleteParm (
            data: TalentJobFinishInfo?,
            isSingleCheck: Boolean): TalentJobDeleteParm {

        var body = TalentJobDeleteParm()
        var ids = ArrayList<String>()

        if (isSingleCheck) {
            ids.add(data?.id ?: "")
        } else {
            var datas = mTalentJobFinishAdapter?.checkMap
            datas?.forEach {
                ids.add(it.key)
            }
        }
        body.ids = ids
        return body
    }

    fun showTalentJobFinishData (datas: TalentJobFinishReq) {
        mTalentJobFinishAdapter?.showData(datas.data,mTvNoData,mRvJobFinish,currentPage)
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

    fun showDeleteTipDlg (isSingleCheck: Boolean) {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "是否删除已选择的工作？"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                if (isSingleCheck) {
                    sendTalentJobDeleteRequest(getTalentJobDeleteParm(mTalentJobFinishAdapter?.getItem(currentPosition),isSingleCheck))
                } else {
                    sendTalentJobDeleteRequest(getTalentJobDeleteParm(null,isSingleCheck))
                }
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
        mTalentJobFinishAdapter?.clear()
        mTalentJobFinishAdapter?.setFooterVisible(false)
        mTalentJobFinishAdapter?.notifyDataSetChanged()

        mRvJobFinish.setHasMore(false)

        sendTalentJobFinishRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentJobFinishRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mTalentJobFinishAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mClComplexCell -> {
                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()

                mJobType = getJobType(position)
                mTalentFinishDropDownMenu.setTabText(mComplexAdapter?.getItem(position))
                mTalentFinishDropDownMenu.closeMenu()

                currentPage = 1
                mTalentJobFinishAdapter?.clear()
                mTalentJobFinishAdapter?.checkMap?.clear()
                mTalentJobFinishAdapter?.notifyDataSetChanged()

                mTvCheckCount.text = "(0/5)"

                sendTalentJobFinishRequest()
            }
            R.id.mChkCheck -> {
                var isChecked = mTalentJobFinishAdapter?.isItemChecked(data!!)
                if (isChecked!!) {
                    mTalentJobFinishAdapter?.checkMap?.remove(data?.id)
                } else {
                    mTalentJobFinishAdapter?.checkMap?.put(data?.id!!,data!!)
                }
                mTalentJobFinishAdapter?.notifyDataSetChanged()

                var count = mTalentJobFinishAdapter?.checkMap?.size ?: 0
                mTvCheckCount.text = "($count/5)"

                if (count > 0) {
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_FFD424))
                } else {
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mTvDelete -> {
                showDeleteTipDlg(true)
            }
            else -> {
                if (taskType == 1) {
                    TalentOrderDetailActivity.intentStart(activity as AppCompatActivity, data?.id)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskOrderDetailActivity(activity as AppCompatActivity, data?.id)
                }
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDeleteAll -> {
                if (mTalentJobFinishAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有可删除的工作")
                    return
                }
                if (mTalentJobFinishAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择需要删除的工作")
                    return
                }
                showDeleteTipDlg(false)
            }
        }
    }

    override fun OnDropDownMenuClick(position: Int) {
    }

    fun getJobType (position: Int): Int? {
        var jobType:Int? = null
        when (position) {
            0 -> {//全部
                jobType = null
            }
            1 -> {//完成工作
                jobType = 10
            }
            2 -> {//解约工作
                jobType = 20
            }
            3 -> {//取消报名
                jobType = 30
            }
        }
        return jobType
    }
}