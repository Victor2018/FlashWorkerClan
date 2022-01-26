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
import kotlinx.android.synthetic.main.activity_talent_task_employing.*

class TalentTaskEmployingActivity : BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()

    var mTalentEmployingInfo: TalentEmployingInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity, employingData: TalentEmployingInfo?) {
            var intent = Intent(activity, TalentTaskEmployingActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_task_employing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent)
        initialize()
    }

    fun initialize () {
        pagerTitles = ResUtils.getStringArrayRes(R.array.business_talent_task_employing_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(TalentTaskingFragment.newInstance())//任务中
        fragmentList?.add(TalentTaskWaitSettlementFragment.newInstance())//待结算

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpEmploying.setAdapter(mTabPagerAdapter)
        mTabEmploying.setupWithViewPager(mVpEmploying)

        mIvBack.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mTalentEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as TalentEmployingInfo?


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}