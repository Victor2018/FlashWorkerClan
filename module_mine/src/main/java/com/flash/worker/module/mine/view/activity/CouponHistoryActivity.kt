package com.flash.worker.module.mine.view.activity

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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UserCouponParm
import com.flash.worker.lib.coremodel.data.req.UserCouponReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.UserCouponVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.CouponHistoryAdapter
import kotlinx.android.synthetic.main.activity_coupon_history.*

class CouponHistoryActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, CouponHistoryActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val userCouponVM: UserCouponVM by viewModels {
        InjectorUtils.provideUserCouponVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mCouponHistoryAdapter: CouponHistoryAdapter? = null
    var currentPage: Int = 1

    override fun getLayoutResource() = R.layout.activity_coupon_history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mCouponHistoryAdapter = CouponHistoryAdapter(this,this)
        mRvEmploymentReward.adapter = mCouponHistoryAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmploymentReward.setLoadMoreListener(this)
        
        mIvBack.setOnClickListener(this)
    }
    
    fun initData () {
        sendUserCouponRequest()
    }

    fun subscribeUi () {
        userCouponVM.userCouponData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showUserCouponData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendUserCouponRequest () {
        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UserCouponParm()
        body.pageNum = currentPage
        body.type = 20

        userCouponVM.fetchUserCoupon(token,body)
    }

    fun showUserCouponData (data: UserCouponReq) {
        mCouponHistoryAdapter?.showData(data.data,mTvNoData,mRvEmploymentReward,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        currentPage = 1
        mCouponHistoryAdapter?.clear()
        mCouponHistoryAdapter?.setFooterVisible(false)
        mCouponHistoryAdapter?.notifyDataSetChanged()

        mRvEmploymentReward.setHasMore(false)

        sendUserCouponRequest()

    }

    override fun OnLoadMore() {
        currentPage++
        sendUserCouponRequest()
    }

}