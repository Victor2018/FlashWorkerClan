package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.view.adapter.ViewPagerAdapter
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.fragment.CancelAccountFragment
import com.flash.worker.module.mine.view.fragment.CancelAccountNotesFragment
import kotlinx.android.synthetic.main.activity_cancel_account.*
import java.util.ArrayList

class CancelAccountActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, CancelAccountActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var mViewPagerAdapter: ViewPagerAdapter? = null
    var fragmentList: MutableList<BaseFragment> = ArrayList()

    override fun getLayoutResource() = R.layout.activity_cancel_account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        fragmentList.add(CancelAccountNotesFragment.newInstance())
        fragmentList.add(CancelAccountFragment.newInstance())

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mViewPagerAdapter?.fragmetList = fragmentList
        mVpCancelAccount.adapter = mViewPagerAdapter

        mIvBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                if (mVpCancelAccount.currentItem > 0) {
                    mVpCancelAccount.currentItem = 0
                    return
                }
                onBackPressed()
            }
        }
    }
}