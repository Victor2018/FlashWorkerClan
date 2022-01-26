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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.data.ReportMatterData
import kotlinx.android.synthetic.main.activity_complaint_matter.*

class ComplaintMatterActivity : BaseActivity(),View.OnClickListener, TextWatcher,
        CompoundButton.OnCheckedChangeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,datas: ArrayList<ReportMatterData>?,taskType: Int) {
            var intent = Intent(activity, ComplaintMatterActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.TASK_TYPE_KEY,taskType)
            activity.startActivity(intent)
        }
    }

    var matterList: ArrayList<ReportMatterData>? = null
    var taskType: Int = 0//1,工单；2，任务

    override fun getLayoutResource() = R.layout.activity_complaint_matter

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
        mChkNotArrived.setOnCheckedChangeListener(this)
        mChkOther.setOnCheckedChangeListener(this)

        mEtReportContent.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        matterList = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as ArrayList<ReportMatterData> ?
        taskType = intent?.getIntExtra(Constant.TASK_TYPE_KEY,0) ?: 0

        if (taskType == 1) {
            mChkFakeInfo.show()
            line_fake_info.show()
            mChkNotArrived.show()
            line_not_arrived.show()

            mChkTalentIllegalOperation.hide()
            line_talent_illegal_operation.hide()
        } else if (taskType == 2) {
            mChkFakeInfo.hide()
            line_fake_info.hide()
            mChkNotArrived.hide()
            line_not_arrived.hide()

            mChkTalentIllegalOperation.show()
            line_talent_illegal_operation.show()
        }

        matterList?.forEach {
            when (it.id) {
                1 -> {
                    mChkFakeInfo.isChecked = true
                }
                2 -> {
                    mChkNotArrived.isChecked = true
                }
                3 -> {
                    mChkOther.isChecked = true
                    mEtReportContent.visibility = View.VISIBLE
                    mTvReportContentCount.visibility = View.VISIBLE
                    mEtReportContent.setText(it.name)
                }
                4 -> {
                    mChkTalentIllegalOperation.isChecked = true
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

                LiveDataBus.send(BusinessActions.ADD_COMPLAINT_MATTER,getReportMatters())
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
        if (mChkFakeInfo.isChecked) {
            var fakeInfoItem = ReportMatterData()
            fakeInfoItem.id = 1
            fakeInfoItem.name = mChkFakeInfo.text.toString()
            matters.add(fakeInfoItem)
        }
        if (mChkTalentIllegalOperation.isChecked) {
            var illegalOperationItem = ReportMatterData()
            illegalOperationItem.id = 4
            illegalOperationItem.name = mChkTalentIllegalOperation.text.toString()
            matters.add(illegalOperationItem)
        }
        if (mChkNotArrived.isChecked) {
            var notProvideJobItem = ReportMatterData()
            notProvideJobItem.id = 2
            notProvideJobItem.name = mChkNotArrived.text.toString()
            matters.add(notProvideJobItem)
        }
        if (mChkOther.isChecked) {
            var otherItem = ReportMatterData()
            otherItem.id = 3
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