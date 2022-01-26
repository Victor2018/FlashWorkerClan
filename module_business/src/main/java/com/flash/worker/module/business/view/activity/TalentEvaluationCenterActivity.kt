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
import com.flash.worker.lib.common.event.BussinessTalentEvent
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
import com.flash.worker.lib.coremodel.data.parm.TalentCommentCenterParm
import com.flash.worker.lib.coremodel.data.parm.TalentDeleteCommentParm
import com.flash.worker.lib.coremodel.data.req.TalentCommentCenterReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnDeleteMyEvaluationListener
import com.flash.worker.module.business.view.adapter.TalentEvaluationCenterAdapter
import com.flash.worker.module.business.view.dialog.DeleteMyEvaluationPopWindow
import kotlinx.android.synthetic.main.activity_talent_evaluation_center.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEvaluationCenterActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentEvaluationCenterActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnDeleteMyEvaluationListener {

    var mLoadingDialog: LoadingDialog? = null
    var mTalentEvaluationCenterAdapter: TalentEvaluationCenterAdapter? = null
    var currentPage = 1

    var currentPosition: Int = -1

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TalentEvaluationCenterActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_evaluation_center

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mTalentEvaluationCenterAdapter = TalentEvaluationCenterAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentEvaluationCenterAdapter,mRvEvaluation)

        mRvEvaluation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEvaluation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendTalentCommentCenterRequest()
    }

    fun subscribeUi() {
        commentVM.talentCommentCenterData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentCommentCenterData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.talentDeleteCommentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")

                    if (mTalentEvaluationCenterAdapter?.getItem(currentPosition)?.employerComment == null) {
                        mTalentEvaluationCenterAdapter?.removeItem(currentPosition)
                        mTalentEvaluationCenterAdapter?.notifyItemRemoved(currentPosition)
                    } else {
                        mTalentEvaluationCenterAdapter?.getItem(currentPosition)?.talentComment = null
                        mTalentEvaluationCenterAdapter?.notifyItemChanged(currentPosition)
                    }

                    UMengEventModule.report(this, BussinessTalentEvent.talent_delete_evaluation)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTalentCommentCenterRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentCommentCenterParm()
        body.pageNum = currentPage

        commentVM.fetchTalentCommentCenter(token,body)
    }

    fun sendTalentDeleteCommentRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentDeleteCommentParm()
        body.commentId = mTalentEvaluationCenterAdapter?.getItem(currentPosition)?.talentComment?.commentId

        commentVM.talentDeleteComment(token,body)
    }

    fun showTalentCommentCenterData (datas: TalentCommentCenterReq) {
        mTalentEvaluationCenterAdapter?.showData(datas.data,mTvNoData,mRvEvaluation,currentPage)
    }

    fun showDeleteEvaluationTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "确定删除我的评价吗？"
        commonTipDialog.mContent = "删除后将没有评价内容。"
        commonTipDialog.mCancelText = "暂不删除"
        commonTipDialog.mOkText = "确定删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendTalentDeleteCommentRequest()
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
        mTalentEvaluationCenterAdapter?.clear()
        mTalentEvaluationCenterAdapter?.setFooterVisible(false)
        mTalentEvaluationCenterAdapter?.notifyDataSetChanged()

        mRvEvaluation.setHasMore(false)

        sendTalentCommentCenterRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentCommentCenterRequest()
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