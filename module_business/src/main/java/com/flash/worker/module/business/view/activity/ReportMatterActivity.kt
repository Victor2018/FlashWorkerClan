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
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.coremodel.data.bean.ReportConfirmDetailData
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportMatterData
import kotlinx.android.synthetic.main.activity_report_matter.*

class ReportMatterActivity : BaseActivity(),View.OnClickListener, TextWatcher,
        CompoundButton.OnCheckedChangeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,
                          datas: ArrayList<ReportMatterData>?,
                          data: ReportConfirmDetailData?) {
            var intent = Intent(activity, ReportMatterActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.REPORT_MATTER_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var matterList: ArrayList<ReportMatterData>? = null
    var mReportConfirmDetailData: ReportConfirmDetailData? = null

    override fun getLayoutResource() = R.layout.activity_report_matter

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {

        mIvBack.setOnClickListener(this)
        mTvConfim.setOnClickListener(this)

        mChkFakeInfo.setOnCheckedChangeListener(this)
        mChkNotProvideJob.setOnCheckedChangeListener(this)
        mChkOther.setOnCheckedChangeListener(this)

        mEtReportContent.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        matterList = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as ArrayList<ReportMatterData>?

        mReportConfirmDetailData = intent?.getSerializableExtra(Constant.REPORT_MATTER_DATA_KEY)
                as ReportConfirmDetailData?

        var taskType = mReportConfirmDetailData?.taskType ?: 0
        Loger.e(TAG,"initData-taskType = $taskType")

        var compensationPrepaidAmount = mReportConfirmDetailData?.compensationPrepaidAmount ?: 0.0
        if (taskType == 1) {
            if (compensationPrepaidAmount > 0) {
                mChkUnsettledSalary.visibility = View.VISIBLE
                line_unsettled_dalary.visibility = View.VISIBLE
                mChkFakeInfo.visibility = View.GONE
                line_fake_info.visibility = View.GONE
                mChkNotProvideJob.visibility = View.GONE
                line_not_provide_job.visibility = View.GONE
            } else {
                mChkUnsettledSalary.visibility = View.GONE
                line_unsettled_dalary.visibility = View.GONE
                mChkFakeInfo.visibility = View.VISIBLE
                line_fake_info.visibility = View.VISIBLE
                mChkNotProvideJob.visibility = View.VISIBLE
                line_not_provide_job.visibility = View.VISIBLE
            }
        } else if (taskType == 2) {
            mChkTaskFakeInfo.visibility = View.VISIBLE
            line_task_fake_info.visibility = View.VISIBLE
            mChkUnsettledSalary.visibility = View.VISIBLE
            line_unsettled_dalary.visibility = View.VISIBLE

            mChkFakeInfo.visibility = View.GONE
            line_fake_info.visibility = View.GONE

            mChkNotProvideJob.visibility = View.GONE
            line_not_provide_job.visibility = View.GONE
        }

        matterList?.forEach {
            when (it.id) {
                1 -> {
                    mChkUnsettledSalary.isChecked = true
                }
                2 -> {
                    if (taskType == 1) {
                        mChkFakeInfo.isChecked = true
                    } else if (taskType == 2) {
                        mChkTaskFakeInfo.isChecked = true
                    }
                }
                3 -> {
                    mChkNotProvideJob.isChecked = true
                }
                4 -> {
                    mChkOther.isChecked = true
                    mEtReportContent.visibility = View.VISIBLE
                    mTvReportContentCount.visibility = View.VISIBLE
                    mEtReportContent.setText(it.name)
                }
                else -> {
                    mEtReportContent.visibility = View.GONE
                    mTvReportContentCount.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfim -> {
                var other = mEtReportContent.text.toString()
                if (mChkOther.isChecked) {
                    if (TextUtils.isEmpty(other)) {
                        ToastUtils.show("请输入其他事项理由~")
                        return
                    }
                }

                LiveDataBus.send(BusinessActions.ADD_REPORT_MATTER,getReportMatters())
                onBackPressed()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvReportContentCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkFakeInfo -> {

            }
            R.id.mChkNotProvideJob -> {

            }
            R.id.mChkOther -> {
                if (isChecked) {
                    mEtReportContent.visibility = View.VISIBLE
                    mTvReportContentCount.visibility = View.VISIBLE
                } else {
                    mEtReportContent.visibility = View.GONE
                    mTvReportContentCount.visibility = View.GONE
                }
            }
        }
    }

    fun getReportMatters(): List<ReportMatterData> {
        var matters = ArrayList<ReportMatterData>()
        if (mChkUnsettledSalary.isChecked) {
            var fakeInfoItem = ReportMatterData()
            fakeInfoItem.id = 1
            fakeInfoItem.name = mChkUnsettledSalary.text.toString()
            matters.add(fakeInfoItem)
        }
        if (mChkFakeInfo.isChecked) {
            var fakeInfoItem = ReportMatterData()
            fakeInfoItem.id = 2
            fakeInfoItem.name = mChkFakeInfo.text.toString()
            matters.add(fakeInfoItem)
        }
        if (mChkTaskFakeInfo.isChecked) {
            var taskFakeInfoItem = ReportMatterData()
            taskFakeInfoItem.id = 2
            taskFakeInfoItem.name = mChkTaskFakeInfo.text.toString()
            matters.add(taskFakeInfoItem)
        }
        if (mChkNotProvideJob.isChecked) {
            var notProvideJobItem = ReportMatterData()
            notProvideJobItem.id = 3
            notProvideJobItem.name = mChkNotProvideJob.text.toString()
            matters.add(notProvideJobItem)
        }
        if (mChkOther.isChecked) {
            var otherItem = ReportMatterData()
            otherItem.id = 4
            otherItem.name = "其他:" + mEtReportContent.text.toString()
            matters.add(otherItem)
        }
        return matters
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}