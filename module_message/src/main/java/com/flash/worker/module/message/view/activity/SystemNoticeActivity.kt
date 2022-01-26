package com.flash.worker.module.message.view.activity

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
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.SystemNoticeParm
import com.flash.worker.lib.coremodel.data.req.SystemNoticeReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.SystemNoticeVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.flash.worker.module.message.view.adapter.SystemNoticeAdapter
import kotlinx.android.synthetic.main.activity_system_notice.*

class SystemNoticeActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener {

    var mLoadingDialog: LoadingDialog? = null
    var mSystemNoticeAdapter: SystemNoticeAdapter? = null

    var currentPage: Int = 1

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SystemNoticeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val systemNoticeVM: SystemNoticeVM by viewModels {
        InjectorUtils.provideSystemNoticeVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_system_notice


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

        mSystemNoticeAdapter = SystemNoticeAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSystemNoticeAdapter,mRvNotice)

        mRvNotice.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvNotice.setLoadMoreListener(this)

        mIvBack.setOnClickListener(this)
    }

    fun initData () {
        sendSystemNoticeRequest()
    }

    fun subscribeUi() {
        systemNoticeVM.noticeData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSystemNoticeData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendSystemNoticeRequest () {
        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = SystemNoticeParm()
        body.pageNum = currentPage

        systemNoticeVM.fetchNotice(token,body)
    }

    fun showSystemNoticeData (data: SystemNoticeReq) {
        mSystemNoticeAdapter?.showData(data.data,mTvNoData,mRvNotice,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            else -> {
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mSystemNoticeAdapter?.clear()
        mSystemNoticeAdapter?.setFooterVisible(false)
        mSystemNoticeAdapter?.notifyDataSetChanged()

        mRvNotice.setHasMore(false)

        sendSystemNoticeRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendSystemNoticeRequest()
    }

}