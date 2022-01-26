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
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.DatePickerDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.parm.FrozenFlowParm
import com.flash.worker.lib.coremodel.data.req.FrozenFlowReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.FrozenFlowAdapter
import kotlinx.android.synthetic.main.act_frozen_flow_content.*
import kotlinx.android.synthetic.main.act_frozen_flow_content.mTvNoData
import kotlinx.android.synthetic.main.activity_frozen_flow.*
import kotlinx.android.synthetic.main.activity_frozen_flow.mSrlRefresh


class FrozenFlowActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        OnDatePickListener, SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, FrozenFlowActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mFrozenFlowAdapter: FrozenFlowAdapter? = null

    var currentPage: Int = 1
    var currentDate: String? = null

    var mDatePickerDialog: DatePickerDialog? = null

    override fun getLayoutResource() = R.layout.activity_frozen_flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mFrozenFlowAdapter = FrozenFlowAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mFrozenFlowAdapter,mRvFrozenFlow)

        mRvFrozenFlow.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)

        mRvFrozenFlow.setLoadMoreListener(this)
    }

    fun initData () {
        currentDate = DateUtil.getTodayDate("yyyy.MM")
        var dateTxt = DateUtil.transDate(currentDate,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        sendFrozenFlowRequest()
    }

    fun subscribeUi () {
        accountVM.frozenFlowData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showBalanceFlow(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendFrozenFlowRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = FrozenFlowParm()
        body.pageNum = currentPage
        body.yearMonth = currentDate

        accountVM.fetchFrozenFlow(token,body)
    }

    fun showBalanceFlow (data: FrozenFlowReq?) {
        mFrozenFlowAdapter?.showData(data?.data,mTvNoData,mRvFrozenFlow,currentPage)
    }

    fun getDatePickerDialog (title: String): DatePickerDialog? {
        if (mDatePickerDialog == null) {
            mDatePickerDialog = DatePickerDialog(this)
            mDatePickerDialog?.mDatePickerTitle = title
            mDatePickerDialog?.showDayPicker = false
            mDatePickerDialog?.mOnDatePickListener = this
        }
        return mDatePickerDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvDate -> {
                getDatePickerDialog("日期")?.show()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }


    override fun OnDatePick(date: String) {
        currentDate = date
        var dateTxt = DateUtil.transDate(date,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        currentPage = 1
        mFrozenFlowAdapter?.clear()
        mFrozenFlowAdapter?.notifyDataSetChanged()

        sendFrozenFlowRequest()
    }

    override fun onRefresh() {
        currentPage = 1
        mFrozenFlowAdapter?.clear()
        mFrozenFlowAdapter?.setFooterVisible(false)
        mFrozenFlowAdapter?.notifyDataSetChanged()

        mRvFrozenFlow.setHasMore(false)

        sendFrozenFlowRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendFrozenFlowRequest()
    }


}