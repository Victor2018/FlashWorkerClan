package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.coremodel.data.bean.ComplaintConfirmDetailData
import com.flash.worker.lib.coremodel.data.bean.ReportConfirmDetailData
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportAppealData
import kotlinx.android.synthetic.main.activity_report_appeal.*

class ReportAppealActivity : BaseActivity(),View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,
                          datas: ArrayList<ReportAppealData>?,
                          data: ReportConfirmDetailData?) {
            var intent = Intent(activity, ReportAppealActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.COMPLAINT_CONFIRM_DETAIL_KEY,data)
            activity.startActivity(intent)
        }
    }

    var appealList: ArrayList<ReportAppealData>? = null
    var mReportConfirmDetailData: ReportConfirmDetailData? = null

    override fun getLayoutResource() = R.layout.activity_report_appeal

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {

        mIvBack.setOnClickListener(this)
        mTvConfim.setOnClickListener(this)

        mChkRequireMarginPayment.setOnCheckedChangeListener(this)
        mChkRequireSettlementSalary.setOnCheckedChangeListener(this)

    }

    fun initData (intent: Intent?) {
        appealList = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as ArrayList<ReportAppealData>?
        mReportConfirmDetailData = intent?.getSerializableExtra(Constant.COMPLAINT_CONFIRM_DETAIL_KEY)
                as ReportConfirmDetailData?

        appealList?.forEach {
            when (it.id) {
                1 -> {
                    mChkRequireMarginPayment.isChecked = true
                }
                2 -> {
                    mChkRequireSettlementSalary.isChecked = true
                    mEtPrepaidSalary.setText(AmountUtil.addCommaDots(it.compensationPrepaidAmount))
                }
            }
        }

        var compensationCreditAmount = mReportConfirmDetailData?.compensationCreditAmount ?: 0.0
        if (compensationCreditAmount > 0) {
            mChkRequireMarginPayment.visibility = View.VISIBLE
            line_require_margin_payment.visibility = View.VISIBLE
        } else {
            mChkRequireMarginPayment.visibility = View.GONE
            line_require_margin_payment.visibility = View.GONE
        }

        var compensationPrepaidAmount = mReportConfirmDetailData?.compensationPrepaidAmount ?: 0.0
        if (compensationPrepaidAmount > 0) {
            mChkRequireSettlementSalary.visibility = View.VISIBLE

            var hint = "请输入1.00 ~ " +
                    "${AmountUtil.addCommaDots(compensationPrepaidAmount)}范围内金额"
            EditTextUtil.setEditTextHintSize(mEtPrepaidSalary,hint,ResUtils.getDimenPixRes(R.dimen.dp_12))

            var filter = MoneyInputFilter()
            filter.setMaxValue(compensationPrepaidAmount)
            mEtPrepaidSalary.filters = arrayOf(filter)
        } else {
            mChkRequireSettlementSalary.visibility = View.GONE
        }

        var taskType = mReportConfirmDetailData?.taskType ?: 0
        if (taskType == 1) {
            mChkRequireMarginPayment.show()
            line_require_margin_payment.show()
        } else if (taskType == 2) {
            mChkRequireMarginPayment.hide()
            line_require_margin_payment.hide()
        }
    }

    fun getReportAppleal (): ArrayList<ReportAppealData> {
        var appeal = ArrayList<ReportAppealData>()
        if (mChkRequireMarginPayment.isChecked) {
            var item = ReportAppealData()
            item.id = 1
            var compensationCreditAmount = mReportConfirmDetailData?.compensationCreditAmount
            item.name = "要求信用金赔付:${AmountUtil.addCommaDots(compensationCreditAmount)}元"
            var taskType = mReportConfirmDetailData?.taskType ?: 0
            if (taskType == 2) {
                item.name = "要求结算雇主已预付报酬:${AmountUtil.addCommaDots(compensationCreditAmount)}元"
            }
            item.compensationCreditAmount = compensationCreditAmount ?: 0.0
            appeal.add(item)
        }
        if (mChkRequireSettlementSalary.isChecked) {
            var prepaidSalary = mEtPrepaidSalary.text.toString()
            if (TextUtils.isEmpty(prepaidSalary)) {
                ToastUtils.show("请输入要求结算报酬金额")
                return appeal
            }
            if (prepaidSalary.toDouble() < 1) {
                ToastUtils.show("结算报酬金额最小为￥1.00")
                return appeal
            }
            var item = ReportAppealData()
            item.id = 2
            var compensationPrepaidAmount = AmountUtil.getRoundUpDouble(prepaidSalary.toDouble(),2)
            item.name = "要求结算雇主已预付报酬:${compensationPrepaidAmount}元"
            item.compensationPrepaidAmount = compensationPrepaidAmount
            appeal.add(item)
        }
        return appeal
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfim -> {
                var prepaidSalary = mEtPrepaidSalary.text.toString()
                if (mChkRequireSettlementSalary.isChecked) {
                    if (TextUtils.isEmpty(prepaidSalary)) {
                        ToastUtils.show("请输入要求结算雇主已预付报酬金额")
                        return
                    }
                }

                LiveDataBus.send(BusinessActions.ADD_REPORT_APPEAL,getReportAppleal())
                onBackPressed()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkRequireMarginPayment -> {

            }
            R.id.mChkRequireSettlementSalary -> {
                if (isChecked) {
                    tv_prepaid_salary.visibility = View.VISIBLE
                    mEtPrepaidSalary.visibility = View.VISIBLE
                    line_prepaid_salary.visibility = View.VISIBLE
                } else {
                    tv_prepaid_salary.visibility = View.GONE
                    mEtPrepaidSalary.visibility = View.GONE
                    line_prepaid_salary.visibility = View.GONE
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}