package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.adapter.TabPagerAdapter
import kotlinx.android.synthetic.main.fragment_business.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MineFragment
 * Author: Victor
 * Date: 2020/11/27 16:33
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.BusinessFgt)
class BusinessFragment: BaseFragment(), AdapterView.OnItemClickListener {
    var mBusinessPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: ArrayList<Fragment>? = null

    companion object {

        fun newInstance(): BusinessFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): BusinessFragment {
            val fragment = BusinessFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_business
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    fun initialize () {
        pagerTitles = ResUtils.getStringArrayRes(R.array.business_tab_titles)

        fragmentList = ArrayList()
        fragmentList?.add(TalentFragment.newInstance())
        fragmentList?.add(EmployerFragment.newInstance())

        mBusinessPagerAdapter = TabPagerAdapter(childFragmentManager)
        mBusinessPagerAdapter?.titles = pagerTitles
        mBusinessPagerAdapter?.frags = fragmentList

        mVpBusiness.adapter = mBusinessPagerAdapter
        mTabBusiness.setupWithViewPager(mVpBusiness)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentList?.clear()
        fragmentList = null
        pagerTitles = null
    }

}