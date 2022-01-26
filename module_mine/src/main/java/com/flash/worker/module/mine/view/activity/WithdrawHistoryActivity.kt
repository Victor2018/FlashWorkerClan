package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.WithdrawHistoryParm
import com.flash.worker.lib.coremodel.data.req.WithdrawHistoryReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.WithdrawHistoryAdapter
import com.flash.worker.module.mine.view.dialog.WithdrawNoticePopWindow
import kotlinx.android.synthetic.main.activity_withdraw_history.*


class WithdrawHistoryActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, WithdrawHistoryActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mWithdrawHistoryAdapter: WithdrawHistoryAdapter? = null

    var currentPage: Int = 1
    
    override fun getLayoutResource() = R.layout.activity_withdraw_history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mWithdrawHistoryAdapter = WithdrawHistoryAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mWithdrawHistoryAdapter,mRvWithdrawHistory)

        mRvWithdrawHistory.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

        mRvWithdrawHistory.setLoadMoreListener(this)
    }

    fun initData () {
        sendWithdrawHistoryRequest()
    }

    fun subscribeUi () {
        accountVM.withdrawHistoryData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showWithdrawHistoryData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendWithdrawHistoryRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = WithdrawHistoryParm()
        body.pageNum = currentPage


        accountVM.fetchWithdrawHistory(token,body)
    }

    fun showWithdrawHistoryData (datas: WithdrawHistoryReq) {
        mWithdrawHistoryAdapter?.showData(datas.data,mTvNoData,mRvWithdrawHistory,currentPage)
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
            R.id.mIvStatus -> {
                var remark = mWithdrawHistoryAdapter?.getItem(position)?.remark ?: ""
                if (TextUtils.isEmpty(remark)) return
                val xOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_10) * 1.0f)
                val yOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var withdrawNoticePopWindow = WithdrawNoticePopWindow(this,remark)
                var mIvStatus = view?.findViewById<ImageView>(R.id.mIvStatus)
                withdrawNoticePopWindow.show(mIvStatus!!, AbsPopWindow.LocationGravity.BOTTOM_LEFT,xOffset, yOffset)
            }
            else -> {

            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mWithdrawHistoryAdapter?.clear()
        mWithdrawHistoryAdapter?.setFooterVisible(false)
        mWithdrawHistoryAdapter?.notifyDataSetChanged()

        mRvWithdrawHistory.setHasMore(false)

        sendWithdrawHistoryRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendWithdrawHistoryRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData()
    }

}