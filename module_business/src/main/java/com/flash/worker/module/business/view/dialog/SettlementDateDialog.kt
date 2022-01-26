package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.lib.coremodel.data.bean.SettlementDateInfo
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnSettlementDateSelectListener
import com.flash.worker.module.business.view.adapter.SettlementDateAdapter
import kotlinx.android.synthetic.main.dlg_settlement_date.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SettlementDateDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 结算日期选择
 * -----------------------------------------------------------------
 */
class SettlementDateDialog(context: Context) : AbsBottomDialog(context),AdapterView.OnItemClickListener {

    var settlementMethod: Int? = 0//结算方式（1-日结；2-周结；3-整单结）
    var settleDateList: List<SettlementDateInfo>? = null
    var mSettlementDateAdapter: SettlementDateAdapter? = null
    var mOnSettlementDateSelectListener: OnSettlementDateSelectListener? = null

    override fun bindContentView() = R.layout.dlg_settlement_date

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
        mSettlementDateAdapter = SettlementDateAdapter(context,this)
        mSettlementDateAdapter?.settlementMethod = settlementMethod
        mRvDlgDate.adapter = mSettlementDateAdapter

    }

    fun initData () {
        mSettlementDateAdapter?.clear()
        mSettlementDateAdapter?.add(settleDateList)
        mSettlementDateAdapter?.notifyDataSetChanged()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mOnSettlementDateSelectListener?.OnSettlementDate(mSettlementDateAdapter?.getItem(position))
        dismiss()
    }

}