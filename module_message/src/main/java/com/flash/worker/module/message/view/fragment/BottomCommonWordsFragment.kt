package com.flash.worker.module.message.view.fragment

import android.os.Bundle
import android.widget.RadioGroup
import androidx.viewpager.widget.ViewPager
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.view.adapter.ViewPagerAdapter
import com.flash.worker.module.message.R
import kotlinx.android.synthetic.main.frag_bottom_common_words.*
import java.util.ArrayList


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BottomCommonWordsFragment
 * Author: Victor
 * Date: 2021/7/23 12:05
 * Description: 常用语面板
 * -----------------------------------------------------------------
 */
class BottomCommonWordsFragment: BaseFragment(), ViewPager.OnPageChangeListener,
    RadioGroup.OnCheckedChangeListener {
    companion object {

        fun newInstance(): BottomCommonWordsFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): BottomCommonWordsFragment {
            val fragment = BottomCommonWordsFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    var mViewPagerAdapter: ViewPagerAdapter? = null
    var fragmentList: MutableList<BaseFragment> = ArrayList()

    override fun getLayoutResource() = R.layout.frag_bottom_common_words

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    fun initialize () {
        fragmentList.add(CommonWordsFragment.newInstance(false))
        fragmentList.add(CommonWordsFragment.newInstance(true))

        mViewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        mViewPagerAdapter?.fragmetList = fragmentList
        mVpCommonWords.adapter = mViewPagerAdapter

        mVpCommonWords.addOnPageChangeListener(this)
        mRgCommonWords.setOnCheckedChangeListener(this)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                mRbTalent.isChecked = true
                mRbEmployer.isChecked = false
            }
            1 -> {
                mRbTalent.isChecked = false
                mRbEmployer.isChecked = true
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbTalent -> {
                mVpCommonWords.currentItem = 0
            }
            R.id.mRbEmployer -> {
                mVpCommonWords.currentItem = 1
            }
        }
    }
}