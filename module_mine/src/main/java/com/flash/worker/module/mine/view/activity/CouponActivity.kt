package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.DensityUtil
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UseCouponParm
import com.flash.worker.lib.coremodel.data.parm.UserCouponParm
import com.flash.worker.lib.coremodel.data.req.UserCouponReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.UserCouponVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.CouponAdapter
import com.flash.worker.module.mine.view.dialog.CouponMorePopWindow
import com.flash.worker.module.mine.view.dialog.RedemptionCodeDialog
import com.flash.worker.module.mine.view.interfaces.OnCouponMoreListener
import com.flash.worker.module.mine.view.interfaces.OnRedemptionCodeListener
import kotlinx.android.synthetic.main.activity_coupon.*

@Route(path = ARouterPath.CouponAct)
class CouponActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener,
        OnCouponMoreListener, OnRedemptionCodeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, CouponActivity::class.java)
            activity.startActivity(intent)
        }
    }
    
    private val userCouponVM: UserCouponVM by viewModels {
        InjectorUtils.provideUserCouponVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mCouponAdapter: CouponAdapter? = null
    var currentPage: Int = 1
    var currentPosition: Int = -1

    override fun getLayoutResource() = R.layout.activity_coupon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mCouponAdapter = CouponAdapter(this,this)
        mRvEmploymentReward.adapter = mCouponAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRvEmploymentReward.setLoadMoreListener(this)
        
        mIvBack.setOnClickListener(this)
        mIvMore.setOnClickListener(this)
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

        userCouponVM.useCouponData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("兑换成功")
                    currentPage = 1
                    mCouponAdapter?.clear()
                    mCouponAdapter?.setFooterVisible(false)
                    mCouponAdapter?.notifyDataSetChanged()

                    mRvEmploymentReward.setHasMore(false)

                    sendUserCouponRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
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
        body.type = 10

        userCouponVM.fetchUserCoupon(token,body)
    }

    fun sendUseCouponRequest (couponCode: String?) {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UseCouponParm()
        body.couponId = mCouponAdapter?.getItem(currentPosition)?.couponId
        body.couponCode = couponCode

        userCouponVM.useCoupon(token,body)
    }

    fun showUserCouponData (data: UserCouponReq) {
        mCouponAdapter?.showData(data.data,mTvNoData,mRvEmploymentReward,currentPage)
    }

    fun showRedemptionCodeDialog () {
        val mRedemptionCodeDialog = RedemptionCodeDialog(this)
        mRedemptionCodeDialog?.mOnRedemptionCodeListener = this
        mRedemptionCodeDialog?.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_30) * -1.0f)
                val yOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var couponMorePopWindow = CouponMorePopWindow(this)
                couponMorePopWindow.mOnCouponMoreListener = this
                couponMorePopWindow.show(mIvMore, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when(view?.id) {
            R.id.mTvUse -> {
                showRedemptionCodeDialog()
            }
            else -> {
                val data = mCouponAdapter?.getItem(position)
                CouponDetailActivity.intentStart(this,data)
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mCouponAdapter?.clear()
        mCouponAdapter?.setFooterVisible(false)
        mCouponAdapter?.notifyDataSetChanged()

        mRvEmploymentReward.setHasMore(false)

        sendUserCouponRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendUserCouponRequest()
    }

    override fun OnCouponMore() {
        CouponHistoryActivity.intentStart(this)
    }

    override fun OnRedemptionCode(code: String?) {
        sendUseCouponRequest(code)
    }
}