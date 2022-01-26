package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.ResumeWorkExperienceInfo
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.dialog.DatePickerDialog
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.view.dialog.WorkExperienceDatePickerDialog
import kotlinx.android.synthetic.main.activity_add_work_experience.*
import kotlinx.android.synthetic.main.activity_add_work_experience.mTvConfirm


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AddWorkExperienceActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class AddWorkExperienceActivity: BaseActivity(),View.OnClickListener,
    OnDatePickListener {
    var selectStartDate: Boolean = true
    var mResumeWorkExperienceInfo: ResumeWorkExperienceInfo? = null

    companion object {

        fun  intentStart (activity: AppCompatActivity,data: ResumeWorkExperienceInfo?) {
            var intent = Intent(activity, AddWorkExperienceActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }


    override fun getLayoutResource() = R.layout.activity_add_work_experience

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
        mTvStartTime.setOnClickListener(this)
        mTvEndTime.setOnClickListener(this)
        mTvDelete.setOnClickListener(this)
    }


    fun initData (intent: Intent?) {
        val data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data != null) {
            mResumeWorkExperienceInfo = (data as ResumeWorkExperienceInfo)
        }

        if (mResumeWorkExperienceInfo !=  null) {
            mTvDelete.visibility = View.VISIBLE
        } else {
            mTvDelete.visibility = View.GONE
        }

        mEtCompany.setText(mResumeWorkExperienceInfo?.companyName)
        mEtPosition.setText(mResumeWorkExperienceInfo?.positionName)
        mTvStartTime.setText(mResumeWorkExperienceInfo?.startTime)
        mTvEndTime.setText(mResumeWorkExperienceInfo?.endTime)

    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
            mTvConfirm -> {
                addWorkExperienceAction()
            }
            mTvStartTime -> {
                selectStartDate = true
                getWorkExperienceDatePickerDialog("开始日期").show()
            }
            mTvEndTime -> {
                selectStartDate = false
                getWorkExperienceDatePickerDialog("结束日期").show()
            }
            mTvDelete -> {
                LiveDataBus.send(BusinessActions.DELETE_WORK_EXPERIENCE,mResumeWorkExperienceInfo)
                onBackPressed()
            }
        }
    }

    fun addWorkExperienceAction () {
        var company = mEtCompany.text.toString()
        var position = mEtPosition.text.toString()
        var startTime = mTvStartTime.text.toString()
        var endTime = mTvEndTime.text.toString()
        if (TextUtils.isEmpty(company)) {
            ToastUtils.show("请输入公司名称")
            return
        }
        if (TextUtils.isEmpty(position)) {
            ToastUtils.show("请输入岗位名称")
            return
        }
        if (TextUtils.isEmpty(startTime)) {
            ToastUtils.show("请选择开始时间")
            return
        }
        if (TextUtils.isEmpty(endTime)) {
            ToastUtils.show("请选择结束时间")
            return
        }

        var data = ResumeWorkExperienceInfo()
        data.companyName = company
        data.positionName = position
        data.startTime = startTime
        data.endTime = endTime

        if (mResumeWorkExperienceInfo == null) {
            LiveDataBus.send(BusinessActions.ADD_WORK_EXPERIENCE,data)
        } else {
            LiveDataBus.send(BusinessActions.UPDATE_WORK_EXPERIENCE,data)
        }

        onBackPressed()
    }

    fun getWorkExperienceDatePickerDialog (title: String): WorkExperienceDatePickerDialog {
        var mWorkExperienceDatePickerDialog = WorkExperienceDatePickerDialog(this)
        mWorkExperienceDatePickerDialog.mOnDatePickListener = this
        mWorkExperienceDatePickerDialog.mDatePickerTitle = title
        return mWorkExperienceDatePickerDialog
    }

    override fun OnDatePick(date: String) {
        Loger.e(TAG,"OnDatePick-date = $date")
        if (selectStartDate) {
            var endDate = mTvEndTime.text.toString()
            if (!TextUtils.isEmpty(endDate)) {
                if (DateUtil.isBeforeStartDate(date,endDate,"yyyy.MM.dd")) {
                    ToastUtils.show("开始时间不能在结束时间之后，请重新选择")
                    return
                }
            }
            mTvStartTime.text = date
        } else {
            var startDate = mTvStartTime.text.toString()
            if (!TextUtils.isEmpty(startDate)) {
                if (DateUtil.isBeforeStartDate(startDate,date,"yyyy.MM.dd")) {
                    ToastUtils.show("结束时间不能在开始时间之前，请重新选择")
                    return
                }
            }
            mTvEndTime.text = date
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}