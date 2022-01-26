package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.interfaces.OnLabelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.SearchKeywordType
import com.flash.worker.lib.coremodel.data.parm.HotKeywordParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.req.HotKeywordReq
import com.flash.worker.lib.coremodel.data.req.SearchTalentReleaseReq
import com.flash.worker.lib.coremodel.db.entity.SearchKeywordEntity
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.HomeVM
import com.flash.worker.lib.coremodel.viewmodel.SearchKeywordVM
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.adapter.SearchTalentReleaseAdapter
import kotlinx.android.synthetic.main.activity_talent_search.*
/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentSearchActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */

class TalentSearchActivity: BaseActivity(),View.OnClickListener,LMRecyclerView.OnLoadMoreListener,
        AdapterView.OnItemClickListener, TextView.OnEditorActionListener, OnLabelClickListener,TextWatcher {

    var mLoadingDialog: LoadingDialog? = null
    var mSearchTalentReleaseAdapter: SearchTalentReleaseAdapter? = null

    var mSearchTalentReleaseParm: SearchTalentReleaseParm? = null
    var currentPage = 1
    var currentPosition: Int = -1

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    private val searchKeywordVM: SearchKeywordVM by viewModels {
        var userId = App.get().getUserInfo()?.userId ?: ""
        InjectorUtils.provideSearchKeywordVMFactory(this,userId)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TalentSearchActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mSearchTalentReleaseAdapter = SearchTalentReleaseAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSearchTalentReleaseAdapter,mRvSearch)

        mRvSearch.adapter = animatorAdapter

        mRvSearch.setLoadMoreListener(this)

        mTvCancel.setOnClickListener(this)
        mIvClear.setOnClickListener(this)
        mIvDetete.setOnClickListener(this)

        mEtSearch.setOnEditorActionListener(this)
        mEtSearch.addTextChangedListener(this)

        mFlHistory.mOnLabelClickListener = this
        mFlHot.mOnLabelClickListener = this

    }

    fun initData () {
        sendHotKeywordRequest()
    }

    fun subscribeUi() {
        homeVM.searchTalentReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showSearchTalentReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        homeVM.hotKeywordData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showHotKeywordData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        searchKeywordVM.talentSearchKeywordData.observe(this, Observer {
           showSearchKeywordData(it)
        })

        searchKeywordVM.talentAllSearchKeywordData.observe(this, Observer {
            if (it.size > 10) {
                searchKeywordVM.deleteSearchKeyword(it[0])
            }
        })
    }

    fun sendSearchTalentReleaseRequest (keyword: String?) {
        Loger.e(TAG,"sendSearchTalentReleaseRequest....")
        if (mSearchTalentReleaseParm == null) {
            mSearchTalentReleaseParm = SearchTalentReleaseParm()
            currentPage = 1
        }
        var query: String? = mEtSearch.text.toString()

        if (!TextUtils.isEmpty(keyword)) {
            query = keyword
        }

        if (TextUtils.isEmpty(query)) {
            ToastUtils.show("请输入搜索关键字")
            return
        }

        var userId = App.get().getUserInfo()?.userId ?: ""

        var item = SearchKeywordEntity(query ?: "",userId,SearchKeywordType.TALENT)
        searchKeywordVM.insertSearchKeyword(item)

        mSearchTalentReleaseParm?.keywords = query
        mSearchTalentReleaseParm?.workCity = App.get().mCity

        mSearchTalentReleaseParm?.pageNum = currentPage

        if (currentPage == 1) {
            mLoadingDialog?.show()
        }
        homeVM.searchTalentRelease(mSearchTalentReleaseParm)

    }

    fun sendHotKeywordRequest () {
        if (!App.get().hasLogin()) {
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = HotKeywordParm()
        body.type = 10

        homeVM.fetchHotKeyword(token,body)

    }

    fun showSearchKeywordData (datas: List<SearchKeywordEntity>) {
        var inflater = LayoutInflater.from(this)
        var views = ArrayList<View>()

        datas.forEach {
            val mTvKey = inflater?.inflate(R.layout.fl_talent_search_cell, null) as TextView
            mTvKey.text = it.keyword
            views.add(mTvKey)
        }
        mFlHistory.addLabelView(views)
    }

    fun showHotKeywordData (data: HotKeywordReq) {
        var inflater = LayoutInflater.from(this)
        var views = ArrayList<View>()

        data.data?.forEach {
            val mTvKey = inflater?.inflate(R.layout.fl_talent_search_cell, null) as TextView
            mTvKey.text = it
            views.add(mTvKey)
        }
        mFlHot.addLabelView(views)
    }

    fun showSearchTalentReleaseData (datas: SearchTalentReleaseReq) {
        showSearchResultUI()
        mSearchTalentReleaseAdapter?.showData(datas.data,mTvNoData,mRvSearch,currentPage)
    }

    fun showSearchResultUI () {
        tv_search_history.visibility = View.GONE
        mIvDetete.visibility = View.GONE
        mFlHistory.visibility = View.GONE
        tv_hot_keyword.visibility = View.GONE
        mFlHot.visibility = View.GONE
    }

    fun showSearchNormalUI () {
        tv_search_history.visibility = View.VISIBLE
        mIvDetete.visibility = View.VISIBLE
        mFlHistory.visibility = View.VISIBLE
        tv_hot_keyword.visibility = View.VISIBLE
        mFlHot.visibility = View.VISIBLE
        mTvNoData.visibility = View.GONE

        mSearchTalentReleaseAdapter?.clear()
        mSearchTalentReleaseAdapter?.notifyDataSetChanged()

        mSearchTalentReleaseAdapter?.setFooterVisible(false)
        mRvSearch.setHasMore(false)

        mEtSearch.setText("")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                onBackPressed()
            }
            R.id.mIvClear -> {
                currentPage = 1
                showSearchNormalUI()
            }
            R.id.mIvDetete -> {
                searchKeywordVM.clearAllSearchKeyword(SearchKeywordType.TALENT)
            }
        }
    }


    override fun OnLoadMore() {
        currentPage++
        sendSearchTalentReleaseRequest("")
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            else -> {
                TalentDetailActivity.intentStart(this,
                        mSearchTalentReleaseAdapter?.getItem(position)?.releaseId, TalentDetailAction.NORMAL)
            }
        }

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mSearchTalentReleaseParm = null
            sendSearchTalentReleaseRequest("")
            return true
        }
        return false
    }

    override fun OnLabelClick(text: String?,position: Int) {
        Loger.e(TAG,"OnLabelClick------>position = $position")
        Loger.e(TAG,"OnLabelClick------>text = $text")

        mEtSearch.setText(text)
        sendSearchTalentReleaseRequest(text)
    }

    override fun afterTextChanged(s: Editable?) {
        var text = mEtSearch.text.toString()
        if (TextUtils.isEmpty(text)) {
            mIvClear.visibility = View.GONE
        } else {
            mIvClear.visibility = View.VISIBLE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


}