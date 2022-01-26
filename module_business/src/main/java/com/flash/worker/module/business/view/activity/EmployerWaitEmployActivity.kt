package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.AmountUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitEmployInfo
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.module.business.view.fragment.CancelledFragment
import com.flash.worker.module.business.view.fragment.EmployedFragment
import com.flash.worker.module.business.view.fragment.SignedUpFragment
import kotlinx.android.synthetic.main.activity_employer_wait_employ.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentWaitEmployActivity
 * Author: Victor
 * Date: 2020/7/3 下午 06:45
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerWaitEmployActivity: BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()

    var mEmployerWaitEmployInfo: EmployerWaitEmployInfo? = null

    private val employerJobVM: EmployerJobVM by viewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: EmployerWaitEmployInfo?) {
            var intent = Intent(activity, EmployerWaitEmployActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_employer_wait_employ
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mIvBack.setOnClickListener(this)
    }

    fun subscribeUi() {
        employerJobVM.employmentNumData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    setPagerTabTitle(0,it.value.data?.signupNum ?: 0)
                    setPagerTabTitle(1,it.value.data?.realEmploymentNum ?: 0)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun initData (intent: Intent?) {
        mEmployerWaitEmployInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployerWaitEmployInfo?
        pagerTitles = ResUtils.getStringArrayRes(R.array.business_talent_wait_employ_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(SignedUpFragment.newInstance(mEmployerWaitEmployInfo))//已报名
        fragmentList?.add(EmployedFragment.newInstance(mEmployerWaitEmployInfo))//已雇用
        fragmentList?.add(CancelledFragment.newInstance(mEmployerWaitEmployInfo))//已取消

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpWaitEmploy.adapter = mTabPagerAdapter
        mTabWaitEmploy.setupWithViewPager(mVpWaitEmploy)

        var identity = ""
        if (mEmployerWaitEmployInfo?.identity == 1) {
            identity = "企业"
        } else if (mEmployerWaitEmployInfo?.identity == 2) {
            identity = "商户"
        } else if (mEmployerWaitEmployInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mEmployerWaitEmployInfo?.employerName}($identity)"

        var licenceAuth = mEmployerWaitEmployInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }
        mTvTitle.text = mEmployerWaitEmployInfo?.title
        mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerWaitEmployInfo?.settlementAmount)

        if (mEmployerWaitEmployInfo?.settlementMethod == 1) {
            tv_daily_salary.text = "元/日/人"
        } else if (mEmployerWaitEmployInfo?.settlementMethod == 2) {
            tv_daily_salary.text = "元/周/人"
        } else if (mEmployerWaitEmployInfo?.settlementMethod == 3) {
            tv_daily_salary.text = "元/单/人"
        }
    }

    fun sendEmploymentNumRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val employerReleaseId = mEmployerWaitEmployInfo?.employerReleaseId

        employerJobVM.fetchEmploymentNum(token,employerReleaseId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    fun setPagerTabTitle (position: Int,count: Int) {
        when (position) {
            0 -> {
                mTabPagerAdapter?.setPageTitle(position,"已报名($count)")
            }
            1 -> {
                mTabPagerAdapter?.setPageTitle(position,"已雇用($count)")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}