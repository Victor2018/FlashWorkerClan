package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.module.business.view.fragment.HireRejectedReleaseFragment
import com.flash.worker.module.business.view.fragment.HireReleaseFragment
import com.flash.worker.module.business.view.fragment.TaskReviewFragment
import kotlinx.android.synthetic.main.activity_hire_release.*

@Route(path = ARouterPath.HireReleaseAct)
class HireReleaseActivity : BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()
    var pagePosition: Int = 0

    companion object {
        fun  intentStart (activity: AppCompatActivity,pagerPosition: Int) {
            var intent = Intent(activity, HireReleaseActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,pagerPosition)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_hire_release

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()

        mIvBack.setOnClickListener(this)
        mCvNewRelease.setOnClickListener(this)
        mClTaskRelease.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        pagePosition = intent?.getIntExtra(Constant.INTENT_DATA_KEY,0) ?: 0

        pagerTitles = ResUtils.getStringArrayRes(R.array.business_talent_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(HireReleaseFragment.newInstance(2))//发布中
        fragmentList?.add(HireReleaseFragment.newInstance(3))//已下架
        fragmentList?.add(HireReleaseFragment.newInstance(1))//编辑中
        fragmentList?.add(TaskReviewFragment.newInstance())//审核中
        fragmentList?.add(HireRejectedReleaseFragment.newInstance())//已驳回

        mTabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpHire.setAdapter(mTabPagerAdapter)
        mTabHire.setupWithViewPager(mVpHire)

        mVpHire.currentItem = pagePosition
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.EXIT_HIRE_RELEASE)
            .observe(this, Observer {
                finish()
            })
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
                HireNewReleaseActivity.intentStart(this)
            }
            R.id.mClTaskRelease -> {
                if (!App.get().hasLogin()) {
                    LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
                    return
                }
                NavigationUtils.goTaskNewReleaseActivity(this)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}