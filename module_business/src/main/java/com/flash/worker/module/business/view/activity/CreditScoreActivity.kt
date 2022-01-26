package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.AccountInfoReq
import com.flash.worker.lib.coremodel.data.req.CreditScoreReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CreditScoreVM
import com.flash.worker.module.business.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.act_credit_score_header.*
import kotlinx.android.synthetic.main.activity_credit_score.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CreditScoreActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class CreditScoreActivity: BaseActivity(),View.OnClickListener, AppBarLayout.OnOffsetChangedListener,
        SwipeRefreshLayout.OnRefreshListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity,isEmployer: Boolean) {
            var intent = Intent(activity, CreditScoreActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,isEmployer)
            activity.startActivity(intent)
        }
    }

    var isEmployer: Boolean = false

    private val creditScoreVM: CreditScoreVM by viewModels {
        InjectorUtils.provideCreditScoreVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_credit_score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        appbar.addOnOffsetChangedListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        isEmployer = intent?.getBooleanExtra(Constant.INTENT_DATA_KEY,false) ?: false

        if (isEmployer) {
            mTvTitle.text = "雇主信用"
            tv_credit_score.text = "累计雇主信用"
            sendEmployerCreditScoreRequest()
        } else {
            mTvTitle.text = "我的信用"
            tv_credit_score.text = "累计人才信用"
            sendTalentCreditScoreRequest()
        }
    }

    fun subscribeUi() {
        creditScoreVM.talentCreditScoreData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCreditScoreData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        creditScoreVM.employerCreditScoreData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showCreditScoreData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTalentCreditScoreRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        creditScoreVM.fetchTalentCreditScore(token)
    }

    fun sendEmployerCreditScoreRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        creditScoreVM.fetchEmployerCreditScore(token)
    }

    fun showCreditScoreData (data: CreditScoreReq?) {
        mTvCreditScore.text = data?.data?.creditScore.toString()
        mTvPraiseCount.text = data?.data?.goodCommentNum.toString()
        mTvAverageCount.text = data?.data?.generalCommentNum.toString()
        mTvBadCount.text = data?.data?.badCommentNum.toString()
        mTvTotalScore.text = "总计：${data?.data?.totalCommentNum}"
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        if (isEmployer) {
            sendEmployerCreditScoreRequest()
        } else {
            sendTalentCreditScoreRequest()
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}