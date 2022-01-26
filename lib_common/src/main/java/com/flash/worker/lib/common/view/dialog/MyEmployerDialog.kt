package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.flash.worker.lib.common.R
import com.flash.worker.lib.coremodel.data.bean.EmployerInfo
import com.flash.worker.lib.common.interfaces.OnEmployerSelectListener
import com.flash.worker.lib.common.view.adapter.MyEmployerAdapter
import kotlinx.android.synthetic.main.dlg_my_employer.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MyEmployerDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 
 * -----------------------------------------------------------------
 */
class MyEmployerDialog(context: Context) : AbsBottomDialog(context),AdapterView.OnItemClickListener,
        View.OnClickListener {

    var employerList: List<EmployerInfo>? = null
    var mMyEmployerAdapter: MyEmployerAdapter? = null
    var mOnEmployerSelectListener: OnEmployerSelectListener? = null

    override fun bindContentView() = R.layout.dlg_my_employer

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        super.handleLayoutParams(wlp)
//        wlp?.height = ((DensityUtil.getDisplayHeight() * 0.8).toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mMyEmployerAdapter = MyEmployerAdapter(context,this)
        mRvDlgEmployer.adapter = mMyEmployerAdapter

        mTvDlgAddEmployer.setOnClickListener(this)
    }

    fun initData () {
        mMyEmployerAdapter?.clear()
        mMyEmployerAdapter?.add(employerList)
        mMyEmployerAdapter?.notifyDataSetChanged()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mOnEmployerSelectListener?.OnEmployerSelect(mMyEmployerAdapter?.getItem(position),
            mMyEmployerAdapter?.itemCount!!)
        dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvDlgAddEmployer -> {
                mOnEmployerSelectListener?.OnEmployerSelect(null,mMyEmployerAdapter?.itemCount!!)
                dismiss()
            }
        }
    }
}