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
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.MemberIncomeRankParm
import com.flash.worker.lib.coremodel.data.req.MemberIncomeRankReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.MonthlyIncomeAdapter
import com.flash.worker.module.mine.view.dialog.MonthlyIncomeDatePickerDialog
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_monthly_income.*

class MonthlyIncomeActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, OnDatePickListener, AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?) {
            var intent = Intent(activity, MonthlyIncomeActivity::class.java)
            intent.putExtra(Constant.GUILD_ID_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mMonthlyIncomeAdapter: MonthlyIncomeAdapter? = null
    var guildId: String? = null

    var currentDate: String? = null
    var mMonthlyIncomeDatePickerDialog: MonthlyIncomeDatePickerDialog? = null

    override fun getLayoutResource() = R.layout.activity_monthly_income

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mMonthlyIncomeAdapter = MonthlyIncomeAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mMonthlyIncomeAdapter,mRvMonthlyIncome)

        mRvMonthlyIncome.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)

        appbar.addOnOffsetChangedListener(this)

    }

    fun initData (intent: Intent?) {
        currentDate = DateUtil.getTodayDate("yyyy.MM")
        var dateTxt = DateUtil.transDate(currentDate,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        guildId = intent?.getStringExtra(Constant.GUILD_ID_KEY)
        sendMemberIncomeRankRequest()
    }

    fun subscribeUi () {
        guildVM.memberIncomeRankData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showMemberIncomeRankData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendMemberIncomeRankRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = MemberIncomeRankParm()
        body.guildId = guildId
        body.statisticsMonth = currentDate

        guildVM.fetchMemberIncomeRank(token,body)
    }

    fun showMemberIncomeRankData (data: MemberIncomeRankReq?) {
        if (data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvMonthlyIncome.visibility = View.GONE
            mMonthlyIncomeAdapter?.setFooterVisible(false)
            mMonthlyIncomeAdapter?.clear()
            mMonthlyIncomeAdapter?.notifyDataSetChanged()
            mRvMonthlyIncome.setHasMore(false)
            return
        }
        if (data.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvMonthlyIncome.visibility = View.GONE
            mMonthlyIncomeAdapter?.setFooterVisible(false)
            mMonthlyIncomeAdapter?.clear()
            mMonthlyIncomeAdapter?.notifyDataSetChanged()
            mRvMonthlyIncome.setHasMore(false)
            return
        }
        if (data.data?.size == 0) {
            mTvNoData.visibility = View.VISIBLE
            mRvMonthlyIncome.visibility = View.GONE
            mMonthlyIncomeAdapter?.setFooterVisible(false)
            mMonthlyIncomeAdapter?.clear()
            mMonthlyIncomeAdapter?.notifyDataSetChanged()
            mRvMonthlyIncome.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        mRvMonthlyIncome.visibility = View.VISIBLE
        mMonthlyIncomeAdapter?.clear()
        mMonthlyIncomeAdapter?.setFooterVisible(true)
        mMonthlyIncomeAdapter?.add(data?.data)

        var count = data?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvMonthlyIncome.setHasMore(false)
            mMonthlyIncomeAdapter?.setLoadState(BaseRecycleAdapter.LOADING_END)
        } else {
            mRvMonthlyIncome.setHasMore(true)
            mMonthlyIncomeAdapter?.setLoadState(BaseRecycleAdapter.LOADING)
        }
        mMonthlyIncomeAdapter?.notifyDataSetChanged()
    }

    fun getDatePickerDialog (title: String): MonthlyIncomeDatePickerDialog? {
        if (mMonthlyIncomeDatePickerDialog == null) {
            mMonthlyIncomeDatePickerDialog = MonthlyIncomeDatePickerDialog(this)
            mMonthlyIncomeDatePickerDialog?.mDatePickerTitle = title
            mMonthlyIncomeDatePickerDialog?.mOnDatePickListener = this
        }
        return mMonthlyIncomeDatePickerDialog
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

    override fun onRefresh() {
        mMonthlyIncomeAdapter?.clear()
        mMonthlyIncomeAdapter?.setFooterVisible(false)
        mMonthlyIncomeAdapter?.notifyDataSetChanged()

        mRvMonthlyIncome.setHasMore(false)

        sendMemberIncomeRankRequest()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

    override fun OnDatePick(date: String) {
        currentDate = date
        var dateTxt = DateUtil.transDate(date,"yyyy.MM","yyyy年MM月")
        mTvDate.text = dateTxt

        mMonthlyIncomeAdapter?.clear()
        mMonthlyIncomeAdapter?.notifyDataSetChanged()

        sendMemberIncomeRankRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }


}