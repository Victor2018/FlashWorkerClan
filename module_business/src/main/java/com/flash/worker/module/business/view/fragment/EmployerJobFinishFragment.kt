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
import com.flash.worker.lib.common.interfaces.OnTradePasswordListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter.Companion.LOADING_END
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.EmployerJobFinishInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerJobFinishParm
import com.flash.worker.lib.coremodel.data.parm.EmployerOrderDeleteParm
import com.flash.worker.lib.coremodel.data.req.EmployerJobFinishReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.factory.EmployerJobVMFactory
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.AttendanceRecordsActivity
import com.flash.worker.module.business.view.activity.EmployerJobFinishActivity
import com.flash.worker.module.business.view.adapter.EmployerJobFinishAdapter
import kotlinx.android.synthetic.main.fragment_employer_job_finish.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobFinishFragment
 * Author: Victor
 * Date: 2020/12/25 12:29
 * Description: 人才 已结束
 * -----------------------------------------------------------------
 */
class EmployerJobFinishFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
        LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnTradePasswordListener {

    companion object {
        fun newInstance(): EmployerJobFinishFragment {
            return EmployerJobFinishFragment()
        }
    }

    private lateinit var employerJobVM: EmployerJobVM

    var currentPage = 1

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerJobFinishAdapter: EmployerJobFinishAdapter? = null
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.fragment_employer_job_finish

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

        mEmployerJobFinishAdapter = EmployerJobFinishAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerJobFinishAdapter,mRvJobFinish)

        mRvJobFinish.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvJobFinish.setLoadMoreListener(this)

        mTvDeleteAll.setOnClickListener(this)
    }

    fun initData () {
        currentPage = 1

        sendEmployerJobFinishRequest()
    }

    fun subscribeUi() {
        employerJobVM.employerJobFinishData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerJobFinishData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.employerOrderDeleteData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mTvCheckCount.text = "(0/5)"
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))

                    mEmployerJobFinishAdapter?.checkMap?.clear()

                    currentPage = 1
                    mEmployerJobFinishAdapter?.clear()
                    mEmployerJobFinishAdapter?.setFooterVisible(false)
                    mEmployerJobFinishAdapter?.notifyDataSetChanged()

                    mRvJobFinish.setHasMore(false)
                    sendEmployerJobFinishRequest()

                    UMengEventModule.report(activity, BussinessEmployerEvent.employer_delete_order)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendEmployerJobFinishRequest () {
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

        var body = EmployerJobFinishParm()
        body.pageNum = currentPage

        employerJobVM.fetchEmployerJobFinish(token,body)
    }

    fun sendEmployerOrderDeleteRequest (body: EmployerOrderDeleteParm?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employerJobVM.employerOrderDelete(token,body)
    }

    fun getEmployerOrderDeleteParm (
            data: EmployerJobFinishInfo?,
            isSingleCheck: Boolean): EmployerOrderDeleteParm {

        var body = EmployerOrderDeleteParm()
        var ids = ArrayList<String>()

        if (isSingleCheck) {
            ids.add(data?.id ?: "")
        } else {
            var datas = mEmployerJobFinishAdapter?.checkMap
            datas?.forEach {
                ids.add(it.key)
            }
        }
        body.ids = ids
        return body
    }

    fun showEmployerJobFinishData (datas: EmployerJobFinishReq) {
        mEmployerJobFinishAdapter?.showData(datas.data,mTvNoData,mRvJobFinish,currentPage)
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
                    var data = mEmployerJobFinishAdapter?.getItem(currentPosition)
                    sendEmployerOrderDeleteRequest(getEmployerOrderDeleteParm(data,isSingleCheck))
                } else {
                    sendEmployerOrderDeleteRequest(getEmployerOrderDeleteParm(null,isSingleCheck))
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
        mEmployerJobFinishAdapter?.clear()
        mEmployerJobFinishAdapter?.setFooterVisible(false)
        mEmployerJobFinishAdapter?.notifyDataSetChanged()

        mRvJobFinish.setHasMore(false)

        sendEmployerJobFinishRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerJobFinishRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        var data = mEmployerJobFinishAdapter?.getItem(position)
        var taskType = data?.taskType ?: 0
        when (view?.id) {
            R.id.mTvCheckInRecord -> {
                AttendanceRecordsActivity.intentStart(activity as AppCompatActivity,data)
            }
            R.id.mChkCheck -> {
                var isChecked = mEmployerJobFinishAdapter?.isItemChecked(data) ?: false
                if (isChecked) {
                    mEmployerJobFinishAdapter?.checkMap?.remove(data?.id)
                } else {
                    mEmployerJobFinishAdapter?.checkMap?.put(data?.id!!,data!!)
                }
                mEmployerJobFinishAdapter?.notifyDataSetChanged()

                var count = mEmployerJobFinishAdapter?.checkMap?.size ?: 0
                mTvCheckCount.text = "($count/5)"

                if (count > 0) {
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
                } else {
                    mTvDeleteAll.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                }
            }
            R.id.mTvDelete -> {
                showDeleteTipDlg(true)
            }
            R.id.mTvDetail -> {
                if (taskType == 1) {
                    NavigationUtils.goJobDetailActivity(activity as AppCompatActivity,
                        data?.employerReleaseId,null,null, JobDetailAction.PREVIEW)
                } else if (taskType == 2) {
                    NavigationUtils.goTaskDetailActivity(activity as AppCompatActivity,
                        data?.employerReleaseId,null,null, TaskDetailAction.PREVIEW)
                }
            }
            else -> {
                EmployerJobFinishActivity.intentStart(activity as AppCompatActivity,mEmployerJobFinishAdapter?.getItem(position))
            }
        }
    }

    override fun OnTradePassword(tradeAmount: Double, tradePassword: String?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDeleteAll -> {
                if (mEmployerJobFinishAdapter?.getContentItemCount() == 0) {
                    ToastUtils.show("没有可删除的工作")
                    return
                }
                if (mEmployerJobFinishAdapter?.checkMap?.size == 0) {
                    ToastUtils.show("请选择需要删除的工作")
                    return
                }
                showDeleteTipDlg(false)
            }
        }
    }

}