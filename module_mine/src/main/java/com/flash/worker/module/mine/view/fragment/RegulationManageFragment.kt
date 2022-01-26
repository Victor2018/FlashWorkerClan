package com.flash.worker.module.mine.view.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.GuildDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.factory.GuildVMFactory
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.activity.GuildNewsManageActivity
import com.flash.worker.module.mine.view.activity.RegulationManageActivity
import kotlinx.android.synthetic.main.frag_regulation_manage.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RegulationManageFragment
 * Author: Victor
 * Date: 2021/5/19 19:14
 * Description: 
 * -----------------------------------------------------------------
 */
class RegulationManageFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener {

    companion object {

        fun newInstance(): RegulationManageFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): RegulationManageFragment {
            val fragment = RegulationManageFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var guildVM: GuildVM

    var mGuildDetailReq: GuildDetailReq? = null

    override fun getLayoutResource() = R.layout.frag_regulation_manage

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

        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mTvEdit.setOnClickListener(this)

    }

    fun initData () {
        sendGuildDetailRequest()
    }

    fun subscribeUi() {
        guildVM.guildDetailData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }


    fun sendGuildDetailRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        var parentAct = activity as GuildNewsManageActivity
        var guildId = parentAct.guildId

        mSrlRefresh.isRefreshing = true

        val token = App.get().getLoginReq()?.data?.token
        guildVM.fetchGuildDetail(token,guildId)
    }

    fun showGuildDetailData (data: GuildDetailReq) {
        mGuildDetailReq = data
        mTvRegulation.text = data?.data?.guildRules

        var activeGuildRules = data.data?.activeGuildRules ?: false
        if (activeGuildRules) {
            mTvEdit.setBackgroundResource(R.color.color_FFD424)
        } else {
            mTvEdit.setBackgroundResource(R.color.color_DDDDDD)
        }
    }


    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onRefresh() {
        sendGuildDetailRequest()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvEdit -> {
                var activeGuildRules = mGuildDetailReq?.data?.activeGuildRules ?: false
                if (!activeGuildRules) return
                RegulationManageActivity.intentStart(activity as AppCompatActivity,
                        mGuildDetailReq?.data)
            }
        }
    }
}