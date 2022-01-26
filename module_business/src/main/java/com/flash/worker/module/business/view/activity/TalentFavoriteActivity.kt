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
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerFavReleaseParm
import com.flash.worker.lib.coremodel.data.req.EmployerFavReleaseReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.EmployerFavReleaseVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.TalentFavoriteAdapter
import kotlinx.android.synthetic.main.activity_talent_favorite.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFavoriteActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFavoriteActivity: BaseActivity(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mTalentFavoriteAdapter: TalentFavoriteAdapter? = null
    var currentPage = 1

    var currentPosition: Int = -1

    private val employerFavReleaseVM: EmployerFavReleaseVM by viewModels {
        InjectorUtils.provideEmployerFavReleaseVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TalentFavoriteActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_favorite

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

        mTalentFavoriteAdapter = TalentFavoriteAdapter(this,this)

        val animatorAdapter = AlphaAnimatorAdapter(mTalentFavoriteAdapter,mRvFavorite)

        mRvFavorite.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        mRvFavorite.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendEmployerFavReleaseRequest()
    }

    fun subscribeUi() {
        employerFavReleaseVM.favReleaseData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerFavReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendEmployerFavReleaseRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerFavReleaseParm()
        body.pageNum = currentPage

        employerFavReleaseVM.fetchFavRelease(token,body)
    }


    fun showEmployerFavReleaseData (datas: EmployerFavReleaseReq) {
        mTalentFavoriteAdapter?.showData(datas.data,mTvNoData,mRvFavorite,currentPage)
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
        mTalentFavoriteAdapter?.clear()
        mTalentFavoriteAdapter?.setFooterVisible(false)
        mTalentFavoriteAdapter?.notifyDataSetChanged()

        mRvFavorite.setHasMore(false)

        sendEmployerFavReleaseRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerFavReleaseRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            else -> {
                var releaseId = mTalentFavoriteAdapter?.getItem(position)?.talentReleaseId
                NavigationUtils.goTalentDetailActivity(this,releaseId, TalentDetailAction.NORMAL)
            }
        }
    }

}