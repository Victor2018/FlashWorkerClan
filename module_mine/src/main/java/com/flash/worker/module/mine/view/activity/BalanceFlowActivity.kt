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
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.ComplexAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.DatePickerDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.parm.BalanceFlowParm
import com.flash.worker.lib.coremodel.data.req.BalanceFlowReq
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AccountVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.BalanceFlowAdapter
import kotlinx.android.synthetic.main.act_balance_flow_content.*
import kotlinx.android.synthetic.main.activity_balance_flow.*
import kotlinx.android.synthetic.main.activity_balance_flow.mSrlRefresh
import kotlinx.android.synthetic.main.balance_detail_menu.view.*


class BalanceFlowActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    OnDropDownMenuClickListener, OnDatePickListener, SwipeRefreshLayout.OnRefreshListener,
        LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, BalanceFlowActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val accountVM: AccountVM by viewModels {
        InjectorUtils.provideAccountVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var headers: Array<String>? = null
    var popupViews: MutableList<View> = ArrayList()
    var balanceDetailView: View? = null
    var balanceContentView: View? = null
    var mComplexAdapter: ComplexAdapter? = null
    var mBalanceFlowAdapter: BalanceFlowAdapter? = null

    var currentPage: Int = 1
    var currentDate: String? = null
    var mTradeType: Int? = null

    var mDatePickerDialog: DatePickerDialog? = null

    override fun getLayoutResource() = R.layout.activity_balance_flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        headers = ResUtils.getStringArrayRes(R.array.mine_balance_detail_menu)

        balanceDetailView = layoutInflater.inflate(R.layout.balance_detail_menu, null)

        mComplexAdapter = ComplexAdapter(this,this)
        balanceDetailView?.mRvBalanceDetailFilter?.adapter = mComplexAdapter

        popupViews.clear()
        popupViews.add(balanceDetailView!!)
        balanceContentView = layoutInflater.inflate(R.layout.act_balance_flow_content, null)
        //init dropdownview
        mBalanceDetailDropDownMenu.tabMenuView?.removeAllViews()
        mBalanceDetailDropDownMenu.popupMenuViews?.removeAllViews()
        mBalanceDetailDropDownMenu.isFillWidth = false
        mBalanceDetailDropDownMenu.setDropDownMenu(headers?.toList()!!, popupViews, balanceContentView)
        mBalanceDetailDropDownMenu.mOnDropDownMenuClickListener = this

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)

        mRvBalanceFlow.setLoadMoreListener(this)

        mBalanceFlowAdapter = BalanceFlowAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mBalanceFlowAdapter,mRvBalanceFlow)

        mRvBalanceFlow.adapter = animatorAdapter

    }

    fun initData () {
        currentDate = DateUtil.getTodayDate("yyyy.MM")
        var dateTxt = DateUtil.transDate(currentDate,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        var complexData = ResUtils.getStringArrayRes(R.array.mine_balance_detail_titles)
        mComplexAdapter?.add(complexData?.toList())
        mComplexAdapter?.notifyDataSetChanged()

        sendBalanceFlowRequest()
    }

    fun subscribeUi () {
        accountVM.balanceFlowData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showBalanceFlow(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendBalanceFlowRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = BalanceFlowParm()
        body.pageNum = currentPage
        body.yearMonth = currentDate
        body.tradeType = mTradeType

        accountVM.fetchBalanceFlow(token,body)
    }

    fun showBalanceFlow (data: BalanceFlowReq?) {
        mBalanceFlowAdapter?.showData(data?.data,mTvNoData,mRvBalanceFlow,currentPage)
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
                if (mBalanceDetailDropDownMenu.isShowing()) {
                    mBalanceDetailDropDownMenu.closeMenu()
                }
                getDatePickerDialog("日期")?.show()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mClComplexCell -> {
                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()

                mTradeType = getTradeType(position)
                mBalanceDetailDropDownMenu.setTabText(mComplexAdapter?.getItem(position))
                mBalanceDetailDropDownMenu.closeMenu()

                currentPage = 1
                mBalanceFlowAdapter?.clear()
                mBalanceFlowAdapter?.notifyDataSetChanged()

                sendBalanceFlowRequest()
            }
            else -> {
                var data = mBalanceFlowAdapter?.getItem(position)
                var bizType = data?.bizType
                Loger.e(TAG,"bizType = $bizType")
                if (bizType == 10) {//充值
                    RechargeDetailActivity.intentStart(this,data)
                } else if (bizType == 15) {//提现
                    WithdrawDetailActivity.intentStart(this,data)
                } else {
                    BalanceFlowDetailActivity.intentStart(this,data)
                }
            }
        }
    }

    fun getTradeType (position: Int): Int? {
        var bizType:Int? = null
        when (position) {
            0 -> {
                bizType = null
            }
            1 -> {//充值
                bizType = 3
            }
            2 -> {//提现
                bizType = 4
            }
            3 -> {//收入
                bizType = 1
            }
            4 -> {//支出
                bizType = 2
            }
            5 -> {//其他
                bizType = 5
            }
        }
        return bizType
    }

    override fun OnDropDownMenuClick(position: Int) {
    }

    override fun OnDatePick(date: String) {
        currentDate = date
        var dateTxt = DateUtil.transDate(date,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        currentPage = 1
        mBalanceFlowAdapter?.clear()
        mBalanceFlowAdapter?.notifyDataSetChanged()

        sendBalanceFlowRequest()
    }

    override fun onRefresh() {
        currentPage = 1
        mBalanceFlowAdapter?.clear()
        mBalanceFlowAdapter?.setFooterVisible(false)
        mBalanceFlowAdapter?.notifyDataSetChanged()

        mRvBalanceFlow.setHasMore(false)

        sendBalanceFlowRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendBalanceFlowRequest()
    }

    override fun onBackPressed() {
        if (mBalanceDetailDropDownMenu.isShowing()) {
            mBalanceDetailDropDownMenu.closeMenu()
            return
        }
        super.onBackPressed()
    }

}