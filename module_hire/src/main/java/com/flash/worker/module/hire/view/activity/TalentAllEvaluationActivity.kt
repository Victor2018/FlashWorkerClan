package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentCommentParm
import com.flash.worker.lib.coremodel.data.parm.TalentCommentStatisticsParm
import com.flash.worker.lib.coremodel.data.req.TalentCommentReq
import com.flash.worker.lib.coremodel.data.req.TalentCommentStatisticsReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.adapter.TalentCommentAdapter
import kotlinx.android.synthetic.main.activity_talent_all_evaluation.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentAllEvaluationActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */

@Route(path = ARouterPath.TalentAllEvaluationAct)
class TalentAllEvaluationActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener,RadioGroup.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null
    var mTalentCommentAdapter: TalentCommentAdapter? = null
    var currentPage = 1
    var mLabel: Int? = null
    var userId: String? = null
    var currentPosition: Int = -1

    var isInit: Boolean = true

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,userId: String?,label: Int?) {
            var intent = Intent(activity, TalentAllEvaluationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,userId)
            intent.putExtra(Constant.LABEL_KEY,label)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_all_evaluation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mTalentCommentAdapter = TalentCommentAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentCommentAdapter,mRvEvaluation)

        mRvEvaluation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEvaluation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)
    }

    fun initData (intent: Intent?) {
        userId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        mLabel = intent?.getIntExtra(Constant.LABEL_KEY,0)

        when (mLabel) {
            0 -> {
                mRbAll.isChecked = true
            }
            1 -> {
                mRbVeryGood.isChecked = true
            }
            2 -> {
                mRbGeneral.isChecked = true
            }
            3 -> {
                mRbVeryBad.isChecked = true
            }
        }
        sendTalentCommentStatisticsRequest()
        sendTalentCommentRequest()
    }

    fun subscribeUi() {
        commentVM.talentCommentStatisticsData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showCommentStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commentVM.talentCommentData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCommentData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendTalentCommentStatisticsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentCommentStatisticsParm()
        body.userId = userId

        commentVM.fetchTalentCommentStatistics(token,body)
    }

    fun sendTalentCommentRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var label: Int? = null
        if (mLabel != 0) {
            label = mLabel
        }
        val body = TalentCommentParm()
        body.userId = userId
        body.pageNum = currentPage
        body.label = label

        commentVM.fetchTalentComment(token,body)
    }

    fun showCommentStatisticsData (data: TalentCommentStatisticsReq) {
        var totalCommentNum = AmountUtil.getEvaluationCount(data.data?.totalCommentNum ?: 0)
        mRbAll.text = "全部\t$totalCommentNum"
        mRbVeryGood.text = AmountUtil.getEvaluationCount(data.data?.goodCommentNum ?: 0)
        mRbGeneral.text = AmountUtil.getEvaluationCount(data.data?.generalCommentNum ?: 0)
        mRbVeryBad.text = AmountUtil.getEvaluationCount(data.data?.badCommentNum ?: 0)
    }

    fun showCommentData (datas: TalentCommentReq) {
        isInit = false
        mTalentCommentAdapter?.showData(datas.data,mTvNoData,mRvEvaluation,currentPage)
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
        mTalentCommentAdapter?.clear()
        mTalentCommentAdapter?.setFooterVisible(false)
        mTalentCommentAdapter?.notifyDataSetChanged()

        mRvEvaluation.setHasMore(false)

        sendTalentCommentStatisticsRequest()
        sendTalentCommentRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentCommentRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbAll -> {
                mLabel = 0
            }
            R.id.mRbVeryGood -> {
                mLabel = 1
            }
            R.id.mRbGeneral -> {
                mLabel = 2
            }
            R.id.mRbVeryBad -> {
                mLabel = 3
            }
        }

        if (!isInit) {
            currentPage = 1
            mTalentCommentAdapter?.clear()
            mTalentCommentAdapter?.setFooterVisible(false)
            mTalentCommentAdapter?.notifyDataSetChanged()

            mRvEvaluation.setHasMore(false)

            sendTalentCommentRequest()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}