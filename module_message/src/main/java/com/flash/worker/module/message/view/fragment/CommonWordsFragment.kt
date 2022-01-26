package com.flash.worker.module.message.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.flash.worker.module.message.view.adapter.CommonWordsAdapter
import kotlinx.android.synthetic.main.frag_common_words.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CommonWordsFragment
 * Author: Victor
 * Date: 2021/7/23 17:58
 * Description: 
 * -----------------------------------------------------------------
 */
class CommonWordsFragment: BaseFragment(),AdapterView.OnItemClickListener {
    companion object {

        fun newInstance(isEmployer: Boolean): CommonWordsFragment {
            return newInstance(0,isEmployer)
        }
        fun newInstance(id: Int,isEmployer: Boolean): CommonWordsFragment {
            val fragment = CommonWordsFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            bundle.putBoolean(Constant.INTENT_DATA_KEY, isEmployer)
            fragment.arguments = bundle
            return fragment
        }
    }

    var mCommonWordsAdapter: CommonWordsAdapter? = null
    var isEmployer: Boolean = false

    override fun getLayoutResource() = R.layout.frag_common_words

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mCommonWordsAdapter = CommonWordsAdapter(activity!!,this)
        mRvCommonWords.adapter = mCommonWordsAdapter
    }

    fun initData () {
        isEmployer = arguments?.getBoolean(Constant.INTENT_DATA_KEY,false) ?: false
        var commonWords = ResUtils.getStringArrayRes(R.array.message_talent_common_words)
        if (isEmployer) {
            commonWords = ResUtils.getStringArrayRes(R.array.message_employer_common_words)
        }
        mCommonWordsAdapter?.clear()
        mCommonWordsAdapter?.add(commonWords?.toList())
        mCommonWordsAdapter?.notifyDataSetChanged()
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var commonWord = mCommonWordsAdapter?.getItem(position)
        LiveDataBus.send(IMActions.SEND_COMMON_WORDS_MESSAGE,commonWord)
    }
}