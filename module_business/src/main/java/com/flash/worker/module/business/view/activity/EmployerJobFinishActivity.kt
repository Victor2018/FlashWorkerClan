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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.EmployerJobFinishInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerFinishUserParm
import com.flash.worker.lib.coremodel.data.req.EmployerFinishUserReq
import com.flash.worker.lib.coremodel.data.req.EmploymentNumReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.EmployerFinishUserAdapter
import kotlinx.android.synthetic.main.activity_employer_job_finish.*
import kotlinx.android.synthetic.main.rv_employer_finish_user_cell.view.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerJobFinishActivity
 * Author: Victor
 * Date: 2020/7/3 下午 06:45
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerJobFinishActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener, AdapterView.OnItemClickListener {

    var mEmployerJobFinishInfo: EmployerJobFinishInfo? = null
    var currentPage = 1
    var mEmployerFinishUserAdapter: EmployerFinishUserAdapter? = null

    private val employerJobVM: EmployerJobVM by viewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: EmployerJobFinishInfo?) {
            var intent = Intent(activity, EmployerJobFinishActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_employer_job_finish
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mEmployerFinishUserAdapter = EmployerFinishUserAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerFinishUserAdapter,mRvJobFinish)

        mRvJobFinish.adapter = animatorAdapter
        
        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvJobFinish.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mEmployerJobFinishInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployerJobFinishInfo?

        var identity = ""
        if (mEmployerJobFinishInfo?.identity == 1) {
            identity = "企业"
        } else if (mEmployerJobFinishInfo?.identity == 2) {
            identity = "商户"
        } else if (mEmployerJobFinishInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mEmployerJobFinishInfo?.employerName}($identity)"

        var licenceAuth = mEmployerJobFinishInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }


        var taskType = mEmployerJobFinishInfo?.taskType ?: 0
        if (taskType == 1) {
            mTvTitle.text = mEmployerJobFinishInfo?.title
            mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerJobFinishInfo?.settlementAmount)
            if (mEmployerJobFinishInfo?.settlementMethod == 1) {
                tv_daily_salary.text = "元/日/人"
            } else if (mEmployerJobFinishInfo?.settlementMethod == 2) {
                tv_daily_salary.text = "元/周/人"
            } else if (mEmployerJobFinishInfo?.settlementMethod == 3) {
                tv_daily_salary.text = "元/单/人"
            }
        } else if (taskType == 2) {
            mTvTitle.text = "${mEmployerJobFinishInfo?.title}(${mEmployerJobFinishInfo?.taskQty}件)"
            mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerJobFinishInfo?.price)
            tv_daily_salary.text = "元/件/人"
        }

        sendEmploymentNumRequest()
        sendEmployerFinishUserRequest()
    }

    fun subscribeUi() {
        employerJobVM.employmentNumData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showEmploymentNum(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.employerFinishUserData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerFinishUserData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendEmploymentNumRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var employerReleaseId = mEmployerJobFinishInfo?.employerReleaseId

        employerJobVM.fetchEmploymentNum(token,employerReleaseId)
    }

    fun sendEmployerFinishUserRequest () {
        mSrlRefresh.isRefreshing = false
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerFinishUserParm()
        body.pageNum = currentPage
        body.employerReleaseId = mEmployerJobFinishInfo?.employerReleaseId

        employerJobVM.fetchEmployerFinishUser(token,body)
    }

    fun showEmploymentNum(data: EmploymentNumReq) {
        mTvSettlementSalaryCount.text = "结算${data.data?.settledNum}人"
        mTvCompensationCount.text = "解约${data.data?.cancelContractNum}人"
    }

    fun showEmployerFinishUserData (datas: EmployerFinishUserReq) {
        mEmployerFinishUserAdapter?.showData(datas.data,mTvNoData,mRvJobFinish,currentPage)
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
        mEmployerFinishUserAdapter?.clear()
        mEmployerFinishUserAdapter?.setFooterVisible(false)
        mEmployerFinishUserAdapter?.notifyDataSetChanged()

        mRvJobFinish.setHasMore(false)

        sendEmploymentNumRequest()
        sendEmployerFinishUserRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerFinishUserRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mTvJobOrderId -> {
                var jobOrderId = mEmployerFinishUserAdapter?.getItem(position)?.jobOrderId
                ClipboardUtil.copy(this, Constant.ORDER_NO,jobOrderId)
                ToastUtils.show("已复制到剪贴板")
            }
            else -> {
                var resumeId =  mEmployerFinishUserAdapter?.getItem(position)?.resumeId
                NavigationUtils.goTalentResumeDetailActivity(this,resumeId)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}