package com.flash.worker.lib.common.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.ViolationAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.ViolationLabelReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_violation_report.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentViolationReportActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */

class ViolationReportActivity: BaseActivity(),View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mViolationAdapter: ViolationAdapter? = null
    var isReportTalentViolation: Boolean = false
    var releaseId: String? = null

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,isReportTalentViolation: Boolean,releaseId:String?) {
            var intent = Intent(activity, ViolationReportActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,isReportTalentViolation)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_violation_report

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mViolationAdapter = ViolationAdapter(this,this)
        mViolationAdapter?.setHeaderVisible(true)

        val animatorAdapter
                = AlphaAnimatorAdapter(mViolationAdapter!!,mRvViolation)

        mRvViolation.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        isReportTalentViolation = intent?.getBooleanExtra(Constant.INTENT_DATA_KEY,false) ?: false
        releaseId = intent?.getStringExtra(Constant.RELEASE_ID_KEY)

        if (isReportTalentViolation) {
            mTvTitle.text = "检举人才违规"
        } else {
            mTvTitle.text = "检举雇主违规"
        }
        mViolationAdapter?.isReportTalentViolation = isReportTalentViolation
        mViolationAdapter?.notifyDataSetChanged()
        
        sendViolationLabelRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.BACK_VIOLATION_REPORT)
                .observe(this, Observer {
                    finish()
                })
    }

    fun subscribeUi() {
        commonVM.talentViolationLabelData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showViolationLabelData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commonVM.employerViolationLabelData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showViolationLabelData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendViolationLabelRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        if (isReportTalentViolation) {
            commonVM.fetchTalentViolationLabel(token)
        } else {
            commonVM.fetchEmployerViolationLabel(token)
        }
    }

    fun showViolationLabelData (datas: ViolationLabelReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolation.visibility = View.GONE
            mViolationAdapter?.setFooterVisible(false)
            mViolationAdapter?.clear()
            mViolationAdapter?.notifyDataSetChanged()
            mRvViolation.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolation.visibility = View.GONE
            mViolationAdapter?.setFooterVisible(false)
            mViolationAdapter?.clear()
            mViolationAdapter?.notifyDataSetChanged()
            mRvViolation.setHasMore(false)
            return
        }
        if (datas.data?.size == 0) {
            mTvNoData.visibility = View.VISIBLE
            mRvViolation.visibility = View.GONE
            mViolationAdapter?.setFooterVisible(false)
            mViolationAdapter?.clear()
            mViolationAdapter?.notifyDataSetChanged()
            mRvViolation.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        mRvViolation.visibility = View.VISIBLE
        mViolationAdapter?.clear()
        mViolationAdapter?.add(datas?.data)
        mViolationAdapter?.notifyDataSetChanged()

        mViolationAdapter?.getDatas()?.forEach {
            Loger.e(TAG,"showViolationLabelData-id = " + it.id)
            Loger.e(TAG,"showViolationLabelData-name = " + it.name)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        mViolationAdapter?.clear()
        mViolationAdapter?.notifyDataSetChanged()

        sendViolationLabelRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mTvParentTitle -> {

            }
            else -> {
                var data = mViolationAdapter?.getItem(position)
                data?.isReportTalentViolation = isReportTalentViolation
                FillReportEvidenceActivity.intentStart(this,data,releaseId)
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}