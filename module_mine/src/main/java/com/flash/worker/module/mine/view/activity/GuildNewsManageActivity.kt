package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.fragment.IntroductionManageFragment
import com.flash.worker.module.mine.view.fragment.NewsManageFragment
import com.flash.worker.module.mine.view.fragment.RegulationManageFragment
import kotlinx.android.synthetic.main.activity_guild_news_manage.*

class GuildNewsManageActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, pagerPosition: Int,guildId: String?) {
            var intent = Intent(activity, GuildNewsManageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,pagerPosition)
            intent.putExtra(Constant.GUILD_ID_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()
    var pagePosition: Int = 0
    var guildId: String? = null

    override fun getLayoutResource() = R.layout.activity_guild_news_manage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        pagePosition = intent?.getIntExtra(Constant.INTENT_DATA_KEY,0) ?: 0
        guildId = intent?.getStringExtra(Constant.GUILD_ID_KEY)

        pagerTitles = ResUtils.getStringArrayRes(R.array.mine_news_manage_titles)

        fragmentList?.clear()
        fragmentList?.add(NewsManageFragment.newInstance())//资讯管理
        fragmentList?.add(IntroductionManageFragment.newInstance())//简介管理
        fragmentList?.add(RegulationManageFragment.newInstance())//制度管理

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpNewsManage.adapter = mTabPagerAdapter
        mTabNewsManage.setupWithViewPager(mVpNewsManage)

        mVpNewsManage.currentItem = pagePosition
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