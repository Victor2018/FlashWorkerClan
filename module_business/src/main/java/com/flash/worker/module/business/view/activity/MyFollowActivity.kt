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
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.TalentFavReleaseParm
import com.flash.worker.lib.coremodel.data.req.TalentFavReleaseReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.TalentFavReleaseVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.MyFollowAdapter
import kotlinx.android.synthetic.main.activity_my_follow.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyFollowActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class MyFollowActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mMyFollowAdapter: MyFollowAdapter? = null
    var currentPage = 1

    var currentPosition: Int = -1

    private val talentFavReleaseVM: TalentFavReleaseVM by viewModels {
        InjectorUtils.provideTalentFavReleaseVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, MyFollowActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_my_follow

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

        mLoadingDialog = LoadingDialog(this)

        mMyFollowAdapter = MyFollowAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mMyFollowAdapter,mRvFollow)

        mRvFollow.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvFollow.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendTalentFavReleaseRequest()
    }

    fun subscribeUi() {
        talentFavReleaseVM.favReleaseData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentFavReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendTalentFavReleaseRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentFavReleaseParm()
        body.pageNum = currentPage

        talentFavReleaseVM.fetchFavRelease(token,body)
    }


    fun showTalentFavReleaseData (datas: TalentFavReleaseReq) {
        mMyFollowAdapter?.showData(datas.data,mTvNoData,mRvFollow,currentPage)
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
        mMyFollowAdapter?.clear()
        mMyFollowAdapter?.setFooterVisible(false)
        mMyFollowAdapter?.notifyDataSetChanged()

        mRvFollow.setHasMore(false)

        sendTalentFavReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentFavReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            else -> {
                var releaseId = mMyFollowAdapter?.getItem(position)?.employerReleaseId
                NavigationUtils.goJobDetailActivity(this,releaseId,
                        null,null, JobDetailAction.NORMAL)
            }
        }

    }


}