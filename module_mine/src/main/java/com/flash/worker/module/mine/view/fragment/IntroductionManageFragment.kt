package com.flash.worker.module.mine.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.req.GuildDetailReq
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.factory.GuildVMFactory
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.activity.GuildNewsManageActivity
import com.flash.worker.module.mine.view.activity.IntroductionManageActivity
import com.flash.worker.module.mine.view.adapter.GuildImageAdapter
import kotlinx.android.synthetic.main.activity_guild_detail.*
import kotlinx.android.synthetic.main.frag_introduction_manage.*
import kotlinx.android.synthetic.main.frag_introduction_manage.mRvImage
import kotlinx.android.synthetic.main.frag_introduction_manage.mSrlRefresh
import kotlinx.android.synthetic.main.frag_introduction_manage.mTvIntroduction


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: IntroduceManageFragment
 * Author: Victor
 * Date: 2021/5/19 19:14
 * Description: 
 * -----------------------------------------------------------------
 */
class IntroductionManageFragment: BaseFragment(), AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {

    companion object {

        fun newInstance(): IntroductionManageFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): IntroductionManageFragment {
            val fragment = IntroductionManageFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var guildVM: GuildVM

    var mGuildImageAdapter: GuildImageAdapter? = null
    var mGuildDetailReq: GuildDetailReq? = null

    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    override fun getLayoutResource() = R.layout.frag_introduction_manage

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

        mGuildImageAdapter = GuildImageAdapter(activity!!,this)
        mGuildImageAdapter?.isPreview = true

        val animatorAdapter
                = AlphaAnimatorAdapter(mGuildImageAdapter,mRvImage)

        mRvImage.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mTvEdit.setOnClickListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder: RecyclerView.ViewHolder = mRvImage
                        .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvRelatedCertificate)
                }
            })

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
        mTvIntroduction.text = data?.data?.guildProfile
        mGuildImageAdapter?.clear()
        mGuildImageAdapter?.add(getPics(data.data?.profilePics))
        mGuildImageAdapter?.notifyDataSetChanged()

        var activeGuildProfile = data.data?.activeGuildProfile ?: false
        if (activeGuildProfile) {
            mTvEdit.setBackgroundResource(R.color.color_F7E047)
        } else {
            mTvEdit.setBackgroundResource(R.color.color_DDDDDD)
        }
    }

    fun getPics(urls: List<String>?): ArrayList<WorkPicInfo> {
        var pics = ArrayList<WorkPicInfo>()
        urls?.let {
            urls?.forEach {
                var item = WorkPicInfo()
                item.pic = it
                pics.add(item)
            }
        }
        return pics
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentWorkImagePositon = position
        ViewImageActivity.intentStart(activity as AppCompatActivity,
            getViewImageUrls(mGuildImageAdapter?.getDatas()),
            position,
            view?.findViewById(R.id.mIvRelatedCertificate),
            ResUtils.getStringRes(R.string.img_transition_name))
    }

    override fun onRefresh() {
        sendGuildDetailRequest()
    }

    fun getViewImageUrls(urls: List<WorkPicInfo>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                it.pic?.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvEdit -> {
                var activeGuildProfile = mGuildDetailReq?.data?.activeGuildProfile ?: false
                if (!activeGuildProfile) return
                IntroductionManageActivity.intentStart(activity as AppCompatActivity,
                    mGuildDetailReq?.data)
            }
        }
    }
}