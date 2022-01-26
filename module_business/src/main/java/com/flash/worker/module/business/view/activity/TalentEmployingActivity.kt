package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.SettlementDateData
import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo
import com.flash.worker.lib.coremodel.data.bean.TalentEmployingInfo
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnSettlementDateSelectListener
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.module.business.view.dialog.SettlementDateDialog
import com.flash.worker.module.business.view.fragment.*
import kotlinx.android.synthetic.main.activity_talent_employing.*

class TalentEmployingActivity : BaseActivity(),View.OnClickListener, OnSettlementDateSelectListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()

    var mSettlementDateInfo: SettlementDateInfo? = null

    var mSettlementDateData: SettlementDateData? = null
    var mTalentEmployingInfo: TalentEmployingInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: SettlementDateData?, employingData: TalentEmployingInfo?) {
            var intent = Intent(activity, TalentEmployingActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_employing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent)
        initialize()
    }

    fun initialize () {
        pagerTitles = ResUtils.getStringArrayRes(R.array.business_talent_employing_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(TalentWaitPrepaidFragment.newInstance())//待预付
        fragmentList?.add(TalentWaitSettlementFragment.newInstance())//待结算
        fragmentList?.add(TalentSettledFragment.newInstance())//已结算

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpEmploying.adapter = mTabPagerAdapter
        mTabEmploying.setupWithViewPager(mVpEmploying)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)
        mIvCheckIn.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mTalentEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as TalentEmployingInfo?

        mSettlementDateData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as SettlementDateData?

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

        ImageUtils.instance.loadImage(this,mIvCheckIn,R.mipmap.gif_check_in)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvDate -> {
                getSettlementDateDialog().show()
            }
            R.id.mIvCheckIn -> {
                AttendancePunchCardActivity.intentStart(this,mTalentEmployingInfo)
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

    override fun OnSettlementDate(data: SettlementDateInfo?) {
        mSettlementDateInfo = data
        sendRefreshFragEvent()

        var settlementStartTime = DateUtil.transDate(data?.settlementStartTime,"yyyy.MM.dd","MM月dd日")
        var settlementEndTime = DateUtil.transDate(data?.settlementEndTime,"yyyy.MM.dd","MM月dd日")

        when (mSettlementDateData?.settlementMethod) {
            1 -> {//日结
                mTvDate.text = settlementStartTime
            }
            2 -> {//周结
                mTvDate.text = "$settlementStartTime-$settlementEndTime"
            }
            3 -> {//整单结
                mTvDate.text = "$settlementStartTime-$settlementEndTime"
            }
        }
    }

    fun sendRefreshFragEvent () {
        when (mVpEmploying.currentItem) {
            0 -> {
                LiveDataBus.send(BusinessActions.REFRESH_T_WAIT_PREPAID)
            }
            1 -> {
                LiveDataBus.send(BusinessActions.REFRESH_T_WAIT_SETTLEMENT)
            }
            2 -> {
                LiveDataBus.send(BusinessActions.REFRESH_T_SETTLED)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}