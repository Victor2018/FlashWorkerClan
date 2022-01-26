package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerCommentCenterParm
import com.flash.worker.lib.coremodel.data.parm.EmployerDeleteCommentParm
import com.flash.worker.lib.coremodel.data.req.EmployerCommentCenterReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnDeleteMyEvaluationListener
import com.flash.worker.module.business.view.adapter.EmployerEvaluationCenterAdapter
import com.flash.worker.module.business.view.dialog.DeleteMyEvaluationPopWindow
import kotlinx.android.synthetic.main.activity_employer_evaluation_center.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEvaluationCenterActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEvaluationCenterActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnDeleteMyEvaluationListener {

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerEvaluationCenterAdapter: EmployerEvaluationCenterAdapter? = null
    var currentPage = 1

    var currentPosition: Int = -1

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EmployerEvaluationCenterActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_evaluation_center

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mEmployerEvaluationCenterAdapter = EmployerEvaluationCenterAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerEvaluationCenterAdapter,mRvEvaluation)

        mRvEvaluation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEvaluation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendEmployerCommentCenterRequest()
    }

    fun subscribeUi() {
        commentVM.employerCommentCenterData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerCommentCenterData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commentVM.employerDeleteCommentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")

                    if (mEmployerEvaluationCenterAdapter?.getItem(currentPosition)?.talentComment == null) {
                        mEmployerEvaluationCenterAdapter?.removeItem(currentPosition)
                        mEmployerEvaluationCenterAdapter?.notifyItemRemoved(currentPosition)
                    } else {
                        mEmployerEvaluationCenterAdapter?.getItem(currentPosition)?.employerComment = null
                        mEmployerEvaluationCenterAdapter?.notifyItemChanged(currentPosition)
                    }

                    UMengEventModule.report(this, BussinessEmployerEvent.employer_delete_evaluation)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendEmployerCommentCenterRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerCommentCenterParm()
        body.pageNum = currentPage

        commentVM.fetchEmployerCommentCenter(token,body)
    }

    fun sendEmployerDeleteCommentRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerDeleteCommentParm()
        body.commentId = mEmployerEvaluationCenterAdapter?.getItem(currentPosition)
            ?.employerComment?.commentId

        commentVM.employerDeleteComment(token,body)
    }

    fun showEmployerCommentCenterData (datas: EmployerCommentCenterReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mEmployerEvaluationCenterAdapter?.setFooterVisible(false)
            mEmployerEvaluationCenterAdapter?.clear()
            mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mEmployerEvaluationCenterAdapter?.setFooterVisible(false)
            mEmployerEvaluationCenterAdapter?.clear()
            mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data?.list == null) {
            mTvNoData.visibility = View.VISIBLE
            mEmployerEvaluationCenterAdapter?.setFooterVisible(false)
            mEmployerEvaluationCenterAdapter?.clear()
            mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data?.list?.size == 0) {
            if (currentPage == 1) {
                mTvNoData.visibility = View.VISIBLE
                mEmployerEvaluationCenterAdapter?.setFooterVisible(false)
                mEmployerEvaluationCenterAdapter?.clear()
                mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()
                mRvEvaluation.setHasMore(false)
                return
            }
        }
        mTvNoData.visibility = View.GONE
        if (currentPage == 1) {
            mEmployerEvaluationCenterAdapter?.clear()
        }
        mEmployerEvaluationCenterAdapter?.setFooterVisible(true)
        mEmployerEvaluationCenterAdapter?.add(datas?.data?.list)

        var count = datas?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvEvaluation.setHasMore(false)
            mEmployerEvaluationCenterAdapter?.setLoadState(BaseRecycleAdapter.LOADING_END)
        } else {
            mRvEvaluation.setHasMore(true)
            mEmployerEvaluationCenterAdapter?.setLoadState(BaseRecycleAdapter.LOADING)
        }
        mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()
    }

    fun showDeleteEvaluationTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "确定删除我的评价吗？"
        commonTipDialog.mContent = "删除后将没有评价内容。"
        commonTipDialog.mCancelText = "暂不删除"
        commonTipDialog.mOkText = "确定删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendEmployerDeleteCommentRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerEvaluationCenterAdapter?.clear()
        mEmployerEvaluationCenterAdapter?.setFooterVisible(false)
        mEmployerEvaluationCenterAdapter?.notifyDataSetChanged()

        mRvEvaluation.setHasMore(false)

        sendEmployerCommentCenterRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerCommentCenterRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_60) * -1.0f)
                val yOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var deleteMyEvaluationPopWindow = DeleteMyEvaluationPopWindow(this)
                deleteMyEvaluationPopWindow.mOnDeleteMyEvaluationListener = this
                deleteMyEvaluationPopWindow.show(view!!, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            else -> {
            }
        }

    }

    override fun OnDeleteMyEvaluation() {
        showDeleteEvaluationTipDlg()
    }

}