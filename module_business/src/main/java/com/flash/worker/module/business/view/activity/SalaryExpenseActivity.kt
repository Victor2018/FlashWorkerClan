package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.ExpendStatisticsReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.business.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_salary_expense.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SalaryExpenseActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class SalaryExpenseActivity: BaseActivity(),View.OnClickListener, AppBarLayout.OnOffsetChangedListener,
        SwipeRefreshLayout.OnRefreshListener{

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SalaryExpenseActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_salary_expense

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        appbar.addOnOffsetChangedListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendExpendStatisticsRequest()
    }

    fun subscribeUi() {
        accountVM.expendStatisticsData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showExpendStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendExpendStatisticsRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchExpendStatistics(token)
    }

    fun showExpendStatisticsData (data: ExpendStatisticsReq) {
        val totalStr = "${AmountUtil.addCommaDots(data.data?.dayExpendAmount)}\t元"
        val spannableString = SpannableString(totalStr)

        spannableString.setSpan(AbsoluteSizeSpan(ResUtils.getDimenPixRes(R.dimen.dp_28)),
                spannableString.length - 2, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mTvTodaySalaryExpense.text = spannableString

        mTvMonthSalaryExpense.text = AmountUtil.addCommaDots(data.data?.monthExpendAmount)
        mTvTotalSalaryExpense.text = AmountUtil.addCommaDots(data.data?.totalExpendAmount)
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        sendExpendStatisticsRequest()
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
        initData()
    }

}