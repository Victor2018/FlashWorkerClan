package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.MonthIncomeStatisticsParm
import com.flash.worker.lib.coremodel.data.req.GuildIncomeStatisticsReq
import com.flash.worker.lib.coremodel.data.req.MonthIncomeStatisticsReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.MemberIncomeAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_member_income.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MemberIncomeActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class MemberIncomeActivity: BaseActivity(),View.OnClickListener, AppBarLayout.OnOffsetChangedListener,
        SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener, LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?) {
            var intent = Intent(activity, MemberIncomeActivity::class.java)
            intent.putExtra(Constant.GUILD_ID_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var guildId: String? = null
    
    var mMemberIncomeAdapter: MemberIncomeAdapter? = null
    var currentPage: Int = 1

    override fun getLayoutResource() = R.layout.activity_member_income

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mMemberIncomeAdapter = MemberIncomeAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mMemberIncomeAdapter,mRvMemberIncome)

        mRvMemberIncome.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvMemberIncome.setLoadMoreListener(this)

        appbar.addOnOffsetChangedListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.GUILD_ID_KEY)
        sendGuildIncomeStatisticsRequest()
        sendMonthIncomeStatisticsRequest()
    }

    fun subscribeUi() {
        guildVM.guildIncomeStatisticsData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildIncomeStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        guildVM.monthIncomeStatisticsData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showMonthIncomeStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendGuildIncomeStatisticsRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        guildVM.fetchGuildIncomeStatistics(token,guildId)
    }

    fun sendMonthIncomeStatisticsRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = MonthIncomeStatisticsParm()
        body.guildId = guildId
        body.pageNum = 1

        guildVM.fetchMonthIncomeStatistics(token,body)
    }

    fun showGuildIncomeStatisticsData (data: GuildIncomeStatisticsReq) {
        val monthIncomeAmtStr = AmountUtil.addCommaDots(data.data?.monthIncomeAmount) + "\t元"
        val spannableString = SpannableString(monthIncomeAmtStr)

        spannableString.setSpan(AbsoluteSizeSpan(ResUtils.getDimenPixRes(R.dimen.dp_28)),
                spannableString.length - 2, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mTvMonthIncome.text = spannableString

        mTvTodayIncome.text = AmountUtil.addCommaDots(data.data?.dayIncomeAmount)
        mTvTotalIncome.text = AmountUtil.addCommaDots(data.data?.totalIncomeAmount)

    }

    fun showMonthIncomeStatisticsData (data: MonthIncomeStatisticsReq) {
        mMemberIncomeAdapter?.showData(data.data,mTvNoData,mRvMemberIncome,currentPage)
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mMemberIncomeAdapter?.clear()
        mMemberIncomeAdapter?.setFooterVisible(false)
        mMemberIncomeAdapter?.notifyDataSetChanged()

        mRvMemberIncome.setHasMore(false)
        
        sendGuildIncomeStatisticsRequest()
        sendMonthIncomeStatisticsRequest()
    }


    override fun OnLoadMore() {
        currentPage++
        sendMonthIncomeStatisticsRequest()
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}