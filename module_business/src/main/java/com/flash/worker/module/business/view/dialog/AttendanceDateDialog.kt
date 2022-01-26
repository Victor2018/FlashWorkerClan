package com.flash.worker.module.business.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.flash.worker.lib.common.view.dialog.AbsBottomDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnAttendanceDateSelectListener
import com.flash.worker.module.business.view.adapter.AttendanceDateAdapter
import kotlinx.android.synthetic.main.dlg_attendance_date.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AttendanceDateDialog
 * Author: Victor
 * Date: 2020/12/23 16:20
 * Description: 打卡记录日期选择
 * -----------------------------------------------------------------
 */
class AttendanceDateDialog(context: Context) : AbsBottomDialog(context),AdapterView.OnItemClickListener {

    var attendanceDate: List<String>? = null
    var mAttendanceDateAdapter: AttendanceDateAdapter? = null
    var mOnAttendanceDateSelectListener: OnAttendanceDateSelectListener? = null

    override fun bindContentView() = R.layout.dlg_attendance_date

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
        mAttendanceDateAdapter = AttendanceDateAdapter(context,this)
        mRvDlgDate.adapter = mAttendanceDateAdapter

    }

    fun initData () {
        mAttendanceDateAdapter?.clear()
        mAttendanceDateAdapter?.add(attendanceDate)
        mAttendanceDateAdapter?.notifyDataSetChanged()
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mOnAttendanceDateSelectListener?.OnAttendanceDateSelect(mAttendanceDateAdapter?.getItem(position))
        dismiss()
    }

}