package com.flash.worker.module.job.view.activity

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
import com.flash.worker.lib.coremodel.data.parm.EmployerCommentParm
import com.flash.worker.lib.coremodel.data.parm.EmployerCommentStatisticsParm
import com.flash.worker.lib.coremodel.data.req.*
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.module.job.R
import com.flash.worker.lib.common.view.adapter.EmployerCommentAdapter
import kotlinx.android.synthetic.main.activity_employer_all_evaluation.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerAllEvaluationActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */

@Route(path = ARouterPath.EmployerAllEvaluationAct)
class EmployerAllEvaluationActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerCommentAdapter: EmployerCommentAdapter? = null
    var currentPage = 1
    var mLabel: Int? = null
    var employerId: String? = null
    var currentPosition: Int = -1

    var isInit: Boolean = true

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,employerId: String?,label: Int?) {
            var intent = Intent(activity, EmployerAllEvaluationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,employerId)
            intent.putExtra(Constant.LABEL_KEY,label)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_all_evaluation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mEmployerCommentAdapter = EmployerCommentAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerCommentAdapter,mRvEvaluation)

        mRvEvaluation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvEvaluation.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)
    }

    fun initData (intent: Intent?) {
        employerId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
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
        commentVM.employerCommentStatisticsData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showCommentStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commentVM.employerCommentData.observe(this, Observer {
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

        val body = EmployerCommentStatisticsParm()
        body.employerId = employerId

        commentVM.fetchEmployerCommentStatistics(token,body)
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
        val body = EmployerCommentParm()
        body.employerId = employerId
        body.pageNum = currentPage
        body.label = label

        commentVM.fetchEmployerComment(token,body)
    }

    fun showCommentStatisticsData (data: EmployerCommentStatisticsReq) {
        mRbAll.text = "全部\t${AmountUtil.getEvaluationCount(data.data?.totalCommentNum ?: 0)}"
        mRbVeryGood.text = AmountUtil.getEvaluationCount(data.data?.goodCommentNum ?: 0)
        mRbGeneral.text = AmountUtil.getEvaluationCount(data.data?.generalCommentNum ?: 0)
        mRbVeryBad.text = AmountUtil.getEvaluationCount(data.data?.badCommentNum ?: 0)
    }

    fun showCommentData (datas: EmployerCommentReq) {
        isInit = false
        mEmployerCommentAdapter?.showData(datas.data,mTvNoData,mRvEvaluation,currentPage)
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
        mEmployerCommentAdapter?.clear()
        mEmployerCommentAdapter?.setFooterVisible(false)
        mEmployerCommentAdapter?.notifyDataSetChanged()

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
        when (view?.id) {
            else -> {
            }
        }

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
            mEmployerCommentAdapter?.clear()
            mEmployerCommentAdapter?.setFooterVisible(false)
            mEmployerCommentAdapter?.notifyDataSetChanged()

            mRvEvaluation.setHasMore(false)

            sendTalentCommentRequest()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}