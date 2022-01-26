package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.ComplaintConfirmDetailData
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportAppealData
import com.flash.worker.module.business.data.ReportMatterData
import kotlinx.android.synthetic.main.activity_complaint_appeal.*
import kotlinx.android.synthetic.main.dlg_employer_complaint_appeal.*

class ComplaintAppealActivity : BaseActivity(),View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,
                          datas: ArrayList<ReportAppealData>?,
                          data: ComplaintConfirmDetailData?) {
            var intent = Intent(activity, ComplaintAppealActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.COMPLAINT_CONFIRM_DETAIL_KEY,data)
            activity.startActivity(intent)
        }
    }

    var appealList: ArrayList<ReportAppealData>? = null
    var mComplaintConfirmDetailData: ComplaintConfirmDetailData? = null

    override fun getLayoutResource() = R.layout.activity_complaint_appeal

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
        mChkRefundPrepaidSalary.setOnCheckedChangeListener(this)

    }

    fun initData (intent: Intent?) {
        appealList = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as ArrayList<ReportAppealData>?
        mComplaintConfirmDetailData = intent?.getSerializableExtra(Constant.COMPLAINT_CONFIRM_DETAIL_KEY)
                as ComplaintConfirmDetailData?

        appealList?.forEach {
            when (it.id) {
                1 -> {
                    mChkRequireMarginPayment.isChecked = true
                }
                2 -> {
                    mChkRefundPrepaidSalary.isChecked = true
                    mEtPrepaidSalary.setText(AmountUtil.addCommaDots(it.compensationPrepaidAmount))
                }
            }
        }

        //处理没有预付不显示退回预付报酬选项
        var compensationPrepaidAmount = mComplaintConfirmDetailData?.compensationPrepaidAmount ?: 0.0
        if (compensationPrepaidAmount > 0) {
            mChkRefundPrepaidSalary.visibility = View.VISIBLE
            var hint = "请输入1.00 ~ " +
                    "${AmountUtil.addCommaDots(compensationPrepaidAmount)}范围内金额"
            EditTextUtil.setEditTextHintSize(mEtPrepaidSalary,hint,ResUtils.getDimenPixRes(R.dimen.dp_12))
            var filter = MoneyInputFilter()
            filter.setMaxValue(compensationPrepaidAmount)
            mEtPrepaidSalary.filters = arrayOf(filter)
        } else {
            mChkRefundPrepaidSalary.visibility = View.GONE
        }
    }

    fun getComplaintAppleal (): ArrayList<ReportAppealData> {
        var appeal = ArrayList<ReportAppealData>()
        if (mChkRequireMarginPayment.isChecked) {
            var item = ReportAppealData()
            item.id = 1
            var compensationCreditAmount = mComplaintConfirmDetailData?.compensationCreditAmount
            item.name = "要求信用金赔付:${AmountUtil.addCommaDots(compensationCreditAmount)}元"
            item.compensationCreditAmount = compensationCreditAmount ?: 0.0
            appeal.add(item)
        }
        if (mChkRefundPrepaidSalary.isChecked) {
            var prepaidSalary = mEtPrepaidSalary.text.toString()
            if (TextUtils.isEmpty(prepaidSalary)) {
                ToastUtils.show("请输入要求退回预付报酬金额")
                return appeal
            }
            if (prepaidSalary.toDouble() < 1) {
                ToastUtils.show("退回预付报酬金额最小为￥1.00")
                return appeal
            }
            var item = ReportAppealData()
            item.id = 2
            var compensationPrepaidAmount = AmountUtil.getRoundUpDouble(prepaidSalary.toDouble(),2)
            item.name = "要求解冻退回已预付报酬:${compensationPrepaidAmount}元"
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
                if (mChkRefundPrepaidSalary.isChecked) {
                    if (TextUtils.isEmpty(prepaidSalary)) {
                        ToastUtils.show("请输入解冻退回已预付报酬金额")
                        return
                    }
                }

                LiveDataBus.send(BusinessActions.ADD_COMPLAINT_APPEAL,getComplaintAppleal())
                onBackPressed()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkRequireMarginPayment -> {

            }
            R.id.mChkRefundPrepaidSalary -> {
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