package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnTimePickerListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.TimePickerDialog
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.WorkingHoursData
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_add_qualification.mIvBack
import kotlinx.android.synthetic.main.activity_add_qualification.mTvConfirm
import kotlinx.android.synthetic.main.activity_add_working_hours.mTvDelete
import kotlinx.android.synthetic.main.activity_add_working_hours.mTvEndTime
import kotlinx.android.synthetic.main.activity_add_working_hours.mTvStartTime


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AddWorkingHoursActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class AddWorkingHoursActivity: BaseActivity(),View.OnClickListener, OnTimePickerListener {

    var mLoadingDialog: LoadingDialog? = null

    var selectStartTime: Boolean = true

    var mWorkingHoursData: WorkingHoursData? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: WorkingHoursData?) {
            var intent = Intent(activity, AddWorkingHoursActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_add_working_hours

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
        mTvStartTime.setOnClickListener(this)
        mTvEndTime.setOnClickListener(this)
        mTvDelete.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        val data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data != null) {
            mWorkingHoursData = (data as WorkingHoursData)
            mTvStartTime.setText(mWorkingHoursData?.startTime)
            mTvEndTime.setText(mWorkingHoursData?.endTime)
        }

        if (mWorkingHoursData !=  null) {
            mTvDelete.visibility = View.VISIBLE
        } else {
            mTvDelete.visibility = View.GONE
        }
    }

    fun subscribeUi() {
    }

    fun getTimePickerDialog (title: String): TimePickerDialog {
        var mTimePickerDialog = TimePickerDialog(this)
        mTimePickerDialog.mTimePickerTitle = title
        mTimePickerDialog.mOnTimePickerListener = this
        return mTimePickerDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                addWorkingHoursAction()
            }
            R.id.mTvStartTime -> {
                selectStartTime = true
                getTimePickerDialog("开始时间").show()
            }
            R.id.mTvEndTime -> {
                selectStartTime = false
                getTimePickerDialog("结束时间").show()
            }
            R.id.mTvDelete -> {
                LiveDataBus.send(BusinessActions.DELETE_WORKING_HOURS,mWorkingHoursData)
                onBackPressed()
            }
        }
    }

    fun addWorkingHoursAction () {
        var startTime = mTvStartTime.text.toString()
        if (TextUtils.isEmpty(startTime)) {
            ToastUtils.show("请选择开始时间")
            return
        }
        var endTime = mTvEndTime.text.toString()
        if (TextUtils.isEmpty(endTime)) {
            ToastUtils.show("请选择结束时间")
            return
        }


        var data =  WorkingHoursData()
        data.startTime = startTime
        data.endTime = endTime

        if (mWorkingHoursData == null) {
            LiveDataBus.send(BusinessActions.ADD_WORKING_HOURS,data)
        } else {
            data.index = mWorkingHoursData?.index ?: 0
            LiveDataBus.send(BusinessActions.UPDATE_WORKING_HOURS,data)
        }

        onBackPressed()
    }

    override fun OnTimePicker(time: String) {
        Loger.e(TAG,"OnTimePicker-time = $time")

        var todayDate = DateUtil.getTodayDate("yyyy.MM.dd")
        var selectTime = "${todayDate}.${time}"
        if (selectStartTime) {
            var endTime = mTvEndTime.text.toString()

            if (!TextUtils.isEmpty(endTime)) {
                if (DateUtil.isBeforeStartDate(selectTime,"${todayDate}.${endTime}",
                        "yyyy.MM.dd.HH:mm")) {
                    ToastUtils.show("开始时间不能在结束时间之后，请重新选择")
                    return
                }
            }
            mTvStartTime.text = time
        } else {
            var startTime = mTvStartTime.text.toString()

            if (!TextUtils.isEmpty(startTime)) {
                if (DateUtil.isBeforeStartDate("${todayDate}.${startTime}",selectTime,
                        "yyyy.MM.dd.HH:mm")) {
                    ToastUtils.show("结束时间不能在开始时间之前，请重新选择")
                    return
                }
            }
            mTvEndTime.text = time
        }
    }

}