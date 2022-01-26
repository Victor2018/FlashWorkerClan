package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.interfaces.OnResumeSelectListener
import com.flash.worker.lib.common.view.adapter.MyDlgResumeAdapter
import com.flash.worker.lib.coremodel.data.bean.MyResumeInfo
import kotlinx.android.synthetic.main.dlg_my_resume.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyResumeDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class MyResumeDialog(context: Context) : AbsBottomDialog(context),AdapterView.OnItemClickListener,
        View.OnClickListener {

    var resumeList: List<MyResumeInfo>? = null
    var mMyDlgResumeAdapter: MyDlgResumeAdapter? = null
    var mOnResumeSelectListener: OnResumeSelectListener? = null

    override fun bindContentView() = R.layout.dlg_my_resume

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
//        wlp?.height = ((DeviceUtils.getDisplayMetrics().heightPixels * 0.45).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mMyDlgResumeAdapter = MyDlgResumeAdapter(context,this)
        mRvDlgResume.adapter = mMyDlgResumeAdapter

        mTvDlgAddResume.setOnClickListener(this)
    }

    fun initData () {
        mMyDlgResumeAdapter?.clear()
        mMyDlgResumeAdapter?.add(resumeList)
        mMyDlgResumeAdapter?.notifyDataSetChanged()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mOnResumeSelectListener?.OnResumeSelect(mMyDlgResumeAdapter?.getItem(position),
                mMyDlgResumeAdapter?.itemCount!!)
        dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDlgAddResume -> {
                mOnResumeSelectListener?.OnResumeSelect(null,mMyDlgResumeAdapter?.getContentItemCount()!!)
                dismiss()
            }
        }
    }
}