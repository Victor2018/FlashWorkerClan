package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.module.business.view.fragment.TalentRejectedReleaseFragment
import com.flash.worker.module.business.view.fragment.TalentReleaseFragment
import kotlinx.android.synthetic.main.activity_talent_release.*

class TalentReleaseActivity : BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()
    var pagePosition: Int = 0

    companion object {
        fun  intentStart (activity: AppCompatActivity,pagerPosition: Int) {
            var intent = Intent(activity, TalentReleaseActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,pagerPosition)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_release

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {

        mIvBack.setOnClickListener(this)
        mCvNewRelease.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        pagePosition = intent?.getIntExtra(Constant.INTENT_DATA_KEY,0) ?: 0

        Loger.e(TAG,"pagePosition = $pagePosition")

        pagerTitles = ResUtils.getStringArrayRes(R.array.business_talent_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(TalentReleaseFragment.newInstance(2))//发布中
        fragmentList?.add(TalentReleaseFragment.newInstance(3))//已下架
        fragmentList?.add(TalentReleaseFragment.newInstance(1))//编辑中
        fragmentList?.add(TalentRejectedReleaseFragment.newInstance())//已驳回

        mTabPagerAdapter =
            TabPagerAdapter(
                supportFragmentManager
            )
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpTalent.setAdapter(mTabPagerAdapter)
        mTabTalent.setupWithViewPager(mVpTalent)

        mVpTalent.currentItem = pagePosition
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mCvNewRelease -> {
                if (!App.get().hasLogin()) {
                    LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
                    return
                }
                TalentNewReleaseActivity.intentStart(this)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}