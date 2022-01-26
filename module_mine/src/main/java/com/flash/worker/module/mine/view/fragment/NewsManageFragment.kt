package com.flash.worker.module.mine.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.DeleteGuildNewsParm
import com.flash.worker.lib.coremodel.data.parm.GuildNewsParm
import com.flash.worker.lib.coremodel.data.req.GuildNewsReq
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.factory.GuildVMFactory
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.activity.GuildNewsManageActivity
import com.flash.worker.module.mine.view.activity.NewsReleaseActivity
import com.flash.worker.module.mine.view.adapter.GuildNewsAdapter
import com.flash.worker.module.mine.view.dialog.DeleteGuildNewsPopWindow
import com.flash.worker.module.mine.view.interfaces.OnGuildNewsDeleteListener
import kotlinx.android.synthetic.main.frag_news_manage.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NewManageFragment
 * Author: Victor
 * Date: 2021/5/19 17:04
 * Description: 
 * -----------------------------------------------------------------
 */
class NewsManageFragment: BaseFragment(),View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
    LMRecyclerView.OnLoadMoreListener,AdapterView.OnItemClickListener, OnGuildNewsDeleteListener {
    
    companion object {

        fun newInstance(): NewsManageFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): NewsManageFragment {
            val fragment = NewsManageFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var guildVM: GuildVM

    var mLoadingDialog: LoadingDialog? = null
    var mGuildNewsAdapter: GuildNewsAdapter? = null
    var currentPage = 1
    var currentPosition = -1

    override fun getLayoutResource() = R.layout.frag_news_manage

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        guildVM = ViewModelProvider(this, GuildVMFactory(this))
                .get(GuildVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()

        mGuildNewsAdapter = GuildNewsAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mGuildNewsAdapter,mRvNews)

        mRvNews.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        
        mRvNews.setLoadMoreListener(this)

        mTvNewsRelease.setOnClickListener(this)
    }

    fun initData () {
        sendGuildNewsRequest()
    }

    fun subscribeUi() {
        guildVM.guildNewsData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildNewsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        guildVM.deleteGuildNewsData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("删除成功")
                    mGuildNewsAdapter?.removeItem(currentPosition)
                    mGuildNewsAdapter?.notifyItemRemoved(currentPosition)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendGuildNewsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var parentAct = activity as GuildNewsManageActivity
        var guildId = parentAct.guildId
        
        val body = GuildNewsParm()
        body.guildId = guildId
        body.pageNum = currentPage
        
        
        guildVM.fetchGuildNews(token,body)
    }

    fun sendDeleteGuildNewsRequest (newsId: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = DeleteGuildNewsParm()
        body.newsId = newsId

        guildVM.deleteGuildNews(token,body)
    }

    fun showGuildNewsData (datas: GuildNewsReq) {
        mGuildNewsAdapter?.showData(datas.data,mTvNoData,mRvNews,currentPage)
    }

    fun showDeleteGuildNewsTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "确定删除该条资讯？"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                var newsId = mGuildNewsAdapter?.getItem(currentPosition)?.newsId
                sendDeleteGuildNewsRequest(newsId)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvNewsRelease -> {
                var parentAct = activity as GuildNewsManageActivity
                var guildId = parentAct.guildId
                NewsReleaseActivity.intentStart(activity as AppCompatActivity,guildId)
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mGuildNewsAdapter?.clear()
        mGuildNewsAdapter?.setFooterVisible(false)
        mGuildNewsAdapter?.notifyDataSetChanged()

        mRvNews.setHasMore(false)

        sendGuildNewsRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendGuildNewsRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_50) * -1.0f)
                val yOffset = DensityUtil.dip2px(activity!!, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var deleteGuildNewsPopWindow = DeleteGuildNewsPopWindow(activity)
                deleteGuildNewsPopWindow.mOnGuildNewsDeleteListener = this
                deleteGuildNewsPopWindow.show(view!!, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
            else -> {

            }
        }
    }

    override fun OnGuildNewsDelete() {
        showDeleteGuildNewsTipDlg()
    }

}