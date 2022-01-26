package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.DisputeHistoryReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.DisputeVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.ReportModifyHistoryAdapter
import kotlinx.android.synthetic.main.activity_report_modify_history.*
import kotlinx.android.synthetic.main.rv_report_modify_his_cell.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ReportModifyHistoryActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class ReportModifyHistoryActivity: BaseActivity(),View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,complaintNo: String?) {
            var intent = Intent(activity, ReportModifyHistoryActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,complaintNo)
            activity.startActivity(intent)
        }
    }

    private val disputeVM: DisputeVM by viewModels {
        InjectorUtils.provideDisputeVMFactory(this)
    }

    var complaintNo: String? = null
    var mReportModifyHistoryAdapter: ReportModifyHistoryAdapter? = null
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    override fun getLayoutResource() = R.layout.activity_report_modify_history

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
        subscribeEvent()

        mReportModifyHistoryAdapter = ReportModifyHistoryAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mReportModifyHistoryAdapter,mRvHistory)

        mRvHistory.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder: RecyclerView.ViewHolder = mRvCertificate
                        .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvRelatedCertificate)
                }
            })
    }

    fun initData (intent: Intent?) {
        complaintNo = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendDisputeHistoryRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
            .observeForever(this, Observer {
                currentWorkImagePositon = it as Int
            })
    }

    fun subscribeUi() {
        disputeVM.disputeHistoryData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showDisputeHistoryData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendDisputeHistoryRequest () {
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        disputeVM.fetchDisputeHistory(token,complaintNo)
    }

    fun showDisputeHistoryData (datas: DisputeHistoryReq) {
        mReportModifyHistoryAdapter?.showData(datas.data,mTvNoData,mRvHistory)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        mReportModifyHistoryAdapter?.clear()
        mReportModifyHistoryAdapter?.setFooterVisible(false)
        mReportModifyHistoryAdapter?.notifyDataSetChanged()

        mRvHistory.setHasMore(false)

        sendDisputeHistoryRequest()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var categoryPosition = id.toInt()
        when (view?.id) {
            R.id.mClCertificateRoot -> {
                currentWorkImagePositon = position

                var count = mReportModifyHistoryAdapter?.getDatas()?.size ?: 0
                if (categoryPosition < count) {
                    ViewImageActivity.intentStart(this,
                        getViewImageUrls(mReportModifyHistoryAdapter?.getDatas()?.get(categoryPosition)?.complaintPics),
                        position,
                        view?.findViewById(R.id.mIvRelatedCertificate),
                        ResUtils.getStringRes(R.string.img_transition_name))
                }

            }
            else -> {
            }
        }

    }

    fun getViewImageUrls(urls: List<String>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                it.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}