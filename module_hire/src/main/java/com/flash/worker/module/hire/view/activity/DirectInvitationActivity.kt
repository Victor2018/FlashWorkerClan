package com.flash.worker.module.hire.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.SettlementInfo
import com.flash.worker.lib.common.interfaces.OnDatePickListener
import com.flash.worker.lib.common.interfaces.OnTimePickerListener
import com.flash.worker.lib.common.util.DateUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.DatePickerDialog
import com.flash.worker.lib.common.view.dialog.TimePickerDialog
import com.flash.worker.module.hire.R
import com.flash.worker.lib.common.view.dialog.SettlementMethodDialog
import com.flash.worker.lib.common.interfaces.OnSettlementMethodSelectListener
import kotlinx.android.synthetic.main.activity_direct_invitation.*

class DirectInvitationActivity: BaseActivity(), View.OnClickListener, OnDatePickListener,
    OnTimePickerListener,RadioGroup.OnCheckedChangeListener, OnSettlementMethodSelectListener {

    companion object {
        fun intentStart(activity: AppCompatActivity) {
            var intent = Intent(activity, DirectInvitationActivity::class.java)
            activity.startActivity(intent)
        }
    }
    var selectStartDate: Boolean = true
    var selectStartTime: Boolean = true

    override fun getLayoutResource() = R.layout.activity_direct_invitation

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialzie()
    }

    fun initialzie () {
        mIvBack.setOnClickListener(this)
        mTvStartDate.setOnClickListener(this)
        mTvEndDate.setOnClickListener(this)
        mTvStartTime.setOnClickListener(this)
        mTvEndTime.setOnClickListener(this)
        mTvSettlementMethod.setOnClickListener(this)

        mRgSalary.setOnCheckedChangeListener(this)
    }

    fun getDatePickerDialog (title: String): DatePickerDialog {
        var mDatePickerDialog = DatePickerDialog(this)
        mDatePickerDialog.mDatePickerTitle = title
        mDatePickerDialog.mOnDatePickListener = this
        return mDatePickerDialog
    }

    fun getTimePickerDialog (title: String): TimePickerDialog {
        var mTimePickerDialog = TimePickerDialog(this)
        mTimePickerDialog.mTimePickerTitle = title
        mTimePickerDialog.mOnTimePickerListener = this
        return mTimePickerDialog
    }

    fun getSettlementMethodDialog (): SettlementMethodDialog? {
        var mSettlementMethodDialog = SettlementMethodDialog(this)
        mSettlementMethodDialog?.settlementMethods = getSettlementMethods()
        mSettlementMethodDialog?.mOnSettlementMethodSelectListener = this
        return mSettlementMethodDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvStartDate -> {
                selectStartDate = true
                getDatePickerDialog("开始日期").show()
            }
            R.id.mTvEndDate -> {
                selectStartDate = false
                getDatePickerDialog("结束日期").show()
            }
            R.id.mTvStartTime -> {
                selectStartTime = true
                getTimePickerDialog("开始时间").show()
            }
            R.id.mTvEndTime -> {
                selectStartTime = false
                getTimePickerDialog("结束时间").show()
            }
            R.id.mTvSettlementMethod -> {
                getSettlementMethodDialog()?.show()
            }
        }
    }

    override fun OnDatePick(date: String) {
        Loger.e(TAG,"OnDatePick-date = $date")
        if (selectStartDate) {
            var endDate = mTvEndDate.text.toString()
            if (!TextUtils.isEmpty(endDate)) {
                if (DateUtil.isBeforeStartDate(date,endDate,"yyyy.MM.dd")) {
                    ToastUtils.show("开始日期不能在结束日期之后，请重新选择")
                    return
                }
            }
            mTvStartDate.text = date
        } else {
            var startDate = mTvStartDate.text.toString()
            if (!TextUtils.isEmpty(startDate)) {
                if (DateUtil.isBeforeStartDate(startDate,date,"yyyy.MM.dd")) {
                    ToastUtils.show("结束日期不能在开始日期之前，请重新选择")
                    return
                }
            }
            mTvEndDate.text = date
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbHourlySalary -> {
                mLlHourlySalary.visibility = View.VISIBLE
                mLlPieceSalary.visibility = View.GONE
            }
            R.id.mRbPieceSalary -> {
                mLlPieceSalary.visibility = View.VISIBLE
                mLlHourlySalary.visibility = View.GONE
            }
        }
    }

    override fun OnSettlementMethodSelect(settlementMethod: SettlementInfo?) {
        when (settlementMethod?.settlementMethod) {
            1 -> {//日结
                mTvSettlementMethod.text = "日结"
                mLlSettlementAmount.visibility = View.VISIBLE
                mLlSettlementQuantity.visibility = View.GONE
            }
            2 -> {//周结
                mTvSettlementMethod.text = "周结(7天)"
                mLlSettlementAmount.visibility = View.VISIBLE
                mLlSettlementQuantity.visibility = View.GONE
            }
            3 -> {//整单结
                mTvSettlementMethod.text = "整单结"
                mLlSettlementAmount.visibility = View.GONE
                mLlSettlementQuantity.visibility = View.VISIBLE
            }
        }
    }

    fun getSettlementMethods (): ArrayList<SettlementInfo> {
        var settlementMethods = ArrayList<SettlementInfo>()
        //如果是时薪
        if (mRbHourlySalary.isChecked) {
            var startDate = mTvStartDate.text.toString()
            var endDate = mTvEndDate.text.toString()
            if (TextUtils.isEmpty(startDate)) {
                ToastUtils.show("请选择开始日期")
                return settlementMethods
            }
            if (TextUtils.isEmpty(endDate)) {
                ToastUtils.show("请选择结束日期")
                return settlementMethods
            }

            var item = SettlementInfo()
            item.settlementMethod = 1
            item.settlementName = "日结"
            settlementMethods.add(item)

            var diffDays = DateUtil.getDiffDay(startDate,endDate)
            if (diffDays >= 7) {
                var item = SettlementInfo()
                item.settlementMethod = 2
                item.settlementName = "周结"
                settlementMethods.add(item)
            }

        } else {
            var item = SettlementInfo()
            item.settlementMethod = 3
            item.settlementName = "整单结"
            settlementMethods.add(item)
        }
        return settlementMethods
    }

    override fun OnTimePicker(time: String) {
        Loger.e(TAG,"OnTimePicker-time = $time")

        var todayDate = DateUtil.getTodayDate("yyyy.MM.dd")
        var selectTime = todayDate + "." + time
        if (selectStartTime) {
            var endTime = mTvEndTime.text.toString()

            if (!TextUtils.isEmpty(endTime)) {
                if (DateUtil.isBeforeStartDate(selectTime,todayDate +"." + endTime,"yyyy.MM.dd.HH:mm")) {
                    ToastUtils.show("开始时间不能在结束时间之后，请重新选择")
                    return
                }
            }
            mTvStartTime.text = time

            mTvDiffTime.text = DateUtil.getDiffHour(time,endTime)
        } else {
            var startTime = mTvStartTime.text.toString()

            if (!TextUtils.isEmpty(startTime)) {
                if (DateUtil.isBeforeStartDate( "${todayDate}.${startTime}",selectTime,"yyyy.MM.dd.HH:mm")) {
                    ToastUtils.show("结束时间不能在开始时间之前，请重新选择")
                    return
                }
            }
            mTvEndTime.text = time

            mTvDiffTime.text = DateUtil.getDiffHour(startTime,time)

        }
    }
}