package com.flash.worker.lib.common.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.flash.worker.lib.common.base.BaseFragment

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ViewPagerAdapter
 * Author: Victor
 * Date: 2020/7/13 下午 03:38
 * Description: 
 * -----------------------------------------------------------------
 */
class ViewPagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var fragmetList: List<BaseFragment>? = null
    override fun getItem(position: Int): Fragment {
        return fragmetList?.get(position) ?: Fragment()
    }

    override fun getCount(): Int {
        return fragmetList?.size ?: 0
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
//        return POSITION_NONE
    }
}