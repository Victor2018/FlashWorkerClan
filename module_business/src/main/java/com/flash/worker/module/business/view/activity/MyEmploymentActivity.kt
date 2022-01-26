package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import com.flash.worker.module.business.view.fragment.EmployerEmployingFragment
import com.flash.worker.module.business.view.fragment.EmployerJobFinishFragment
import com.flash.worker.module.business.view.fragment.EmployerWaitCommentFragment
import com.flash.worker.module.business.view.fragment.EmployerWaitEmployFragment
import kotlinx.android.synthetic.main.activity_my_employment.*

@Route(path = ARouterPath.MyEmploymentAct)
class MyEmploymentActivity : BaseActivity(),View.OnClickListener {

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: MutableList<Fragment> = ArrayList()
    var pagePosition: Int = 0

    companion object {
        fun intentStart (activity: AppCompatActivity,pagerPosition: Int) {
            var intent = Intent(activity, MyEmploymentActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,pagerPosition)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_my_employment

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

        pagerTitles = ResUtils.getStringArrayRes(R.array.business_my_employ_tab_titles)

        fragmentList?.clear()
        fragmentList?.add(EmployerWaitEmployFragment.newInstance())//待雇用
        fragmentList?.add(EmployerEmployingFragment.newInstance())//进行中
        fragmentList?.add(EmployerWaitCommentFragment.newInstance())//待评价
        fragmentList?.add(EmployerJobFinishFragment.newInstance())//已结束
//        fragmentList?.add(EmployerWaitEmployFragment.newInstance())//争议处理

        mTabPagerAdapter =
            TabPagerAdapter(
                supportFragmentManager
            )
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        mVpMyEmployment.setAdapter(mTabPagerAdapter)
        mTabMyEmployment.setupWithViewPager(mVpMyEmployment)

        mVpMyEmployment.currentItem = pagePosition
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