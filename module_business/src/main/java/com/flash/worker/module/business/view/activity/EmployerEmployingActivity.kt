package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.EmployerEmployingInfo
import com.flash.worker.lib.coremodel.data.bean.SettlementDateData
import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnSettlementDateSelectListener
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.module.business.view.dialog.SettlementDateDialog
import com.flash.worker.module.business.view.fragment.*
import kotlinx.android.synthetic.main.activity_employer_employing.*

class EmployerEmployingActivity : BaseActivity(),View.OnClickListener, OnSettlementDateSelectListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()

    var mSettlementDateInfo: SettlementDateInfo? = null

    var mSettlementDateData: SettlementDateData? = null
    var mEmployerEmployingInfo: EmployerEmployingInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: SettlementDateData?, employingData: EmployerEmployingInfo?) {
            var intent = Intent(activity, EmployerEmployingActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_employing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent)
        initialize()
    }

    fun initialize () {
        subscribeUi()

        pagerTitles = ResUtils.getStringArrayRes(R.array.business_employer_employing_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(EmployerWaitPrepaidFragment.newInstance())//待预付
        fragmentList?.add(EmployerWaitSettlementFragment.newInstance())//待结算
        fragmentList?.add(EmployerSettledFragment.newInstance())//已结算
        fragmentList?.add(EmployerCancelledFragment.newInstance())//已解约

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpEmploying.adapter = mTabPagerAdapter
        mTabEmploying.setupWithViewPager(mVpEmploying)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)
        mTvCheckInRecord.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mEmployerEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as EmployerEmployingInfo?
        mSettlementDateData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as SettlementDateData?

        var identity = ""
        if (mEmployerEmployingInfo?.identity == 1) {
            identity = "企业"
        } else if (mEmployerEmployingInfo?.identity == 2) {
            identity = "商户"
        } else if (mEmployerEmployingInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mEmployerEmployingInfo?.employerName}($identity)"

        var licenceAuth = mEmployerEmployingInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        mTvTitle.text = mEmployerEmployingInfo?.title

        var taskType = mEmployerEmployingInfo?.taskType ?: 0
        if (taskType == 1) {
            if (mEmployerEmployingInfo?.settlementMethod == 1) {
                tv_daily_salary.text = "元/日/人"
            } else if (mEmployerEmployingInfo?.settlementMethod == 2) {
                tv_daily_salary.text = "元/周/人"
            } else if (mEmployerEmployingInfo?.settlementMethod == 3) {
                tv_daily_salary.text = "元/单/人"
            }
        }

        mTvSettlementAmount.text = AmountUtil.addCommaDots(mEmployerEmployingInfo?.settlementAmount)

        var formater = "yyyy.MM.dd"
        var today = DateUtil.getTodayDate(formater)

        var count = mSettlementDateData?.settlementDates?.size ?: 0
        if (mSettlementDateData?.settlementDates != null && count > 0) {
            mSettlementDateData?.settlementDates?.forEach {
                if (it.isDefualt) {
                    mSettlementDateInfo = it
                }
            }
            if (mSettlementDateInfo == null) {
                mSettlementDateInfo = mSettlementDateData?.settlementDates?.get(0)
            }
        } else {
            mSettlementDateInfo = SettlementDateInfo()
            mSettlementDateInfo?.settlementStartTime = today
            mSettlementDateInfo?.settlementEndTime = today
        }

        when (mSettlementDateData?.settlementMethod) {
            1 -> {//日结
                var startTime = mSettlementDateInfo?.settlementStartTime
                mTvDate.text = DateUtil.transDate(startTime,formater,"MM月dd日")

            }
            2,3 -> {//周结、整单结
                var startTime = mSettlementDateInfo?.settlementStartTime
                var endTime = mSettlementDateInfo?.settlementEndTime

                var startTxt = DateUtil.transDate(startTime,formater,"MM月dd日")
                var endTxt = DateUtil.transDate(endTime,formater,"MM月dd日")

                mTvDate.text = "$startTxt-$endTxt"
            }
        }
    }

    fun subscribeUi() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvDate -> {
                getSettlementDateDialog().show()
            }
            R.id.mTvCheckInRecord -> {
                AttendanceRecordsActivity.intentStart(this,mEmployerEmployingInfo)
            }
        }
    }

    fun getSettlementDateDialog(): SettlementDateDialog {

        var mSettlementDateDialog = SettlementDateDialog(this)
        mSettlementDateDialog?.settlementMethod = mSettlementDateData?.settlementMethod
        mSettlementDateDialog?.settleDateList = mSettlementDateData?.settlementDates
        mSettlementDateDialog?.mOnSettlementDateSelectListener = this
        return mSettlementDateDialog
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun OnSettlementDate(data: SettlementDateInfo?) {
        mSettlementDateInfo = data
        sendRefreshFragEvent()
        when (mSettlementDateData?.settlementMethod) {
            1 -> {//日结
                mTvDate.text = data?.settlementStartTime
            }
            2 -> {//周结
                mTvDate.text = "${data?.settlementStartTime}-${data?.settlementEndTime}"
            }
            3 -> {//件结
                mTvDate.text = "${data?.settlementStartTime}-${data?.settlementEndTime}"
            }
        }
    }

    fun sendRefreshFragEvent () {
        when (mVpEmploying.currentItem) {
            0 -> {
                LiveDataBus.send(BusinessActions.REFRESH_E_WAIT_PREPAID)
            }
            1 -> {
                LiveDataBus.send(BusinessActions.REFRESH_E_WAIT_SETTLEMENT)
            }
            2 -> {
                LiveDataBus.send(BusinessActions.REFRESH_E_SETTLED)
            }
            3 -> {
                LiveDataBus.send(BusinessActions.REFRESH_E_TC)
            }
        }
    }
}