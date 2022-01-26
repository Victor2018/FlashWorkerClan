package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.parm.EmployerConfirmAttendanceParm
import com.flash.worker.lib.coremodel.data.bean.EmployerEmployingInfo
import com.flash.worker.lib.coremodel.data.bean.EmployerJobFinishInfo
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.EmployerAttendanceParm
import com.flash.worker.lib.coremodel.data.req.AttendanceDateReq
import com.flash.worker.lib.coremodel.data.req.EmployerAttendanceReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AttendanceVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnAttendanceDateSelectListener
import com.flash.worker.module.business.view.adapter.EmployerAttendanceAdapter
import com.flash.worker.module.business.view.dialog.AttendanceDateDialog
import kotlinx.android.synthetic.main.activity_attendance_records.*

class AttendanceRecordsActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener,
        OnAttendanceDateSelectListener {

    var mLoadingDialog: LoadingDialog? = null
    var mEmployerEmployingInfo: EmployerEmployingInfo? = null
    var mEmployerWaitCommentInfo: EmployerWaitCommentInfo? = null
    var mEmployerJobFinishInfo: EmployerJobFinishInfo? = null
    var mEmployerAttendanceAdapter: EmployerAttendanceAdapter? = null
    var currentPage: Int = 1
    var currentPosition: Int = -1

    var mAttendanceDate: String? = null
    var mAttendanceDateReq: AttendanceDateReq? = null

    private val attendanceVM: AttendanceVM by viewModels {
        InjectorUtils.provideAttendanceVM(this)
    }


    companion object {
        fun  intentStart (activity: AppCompatActivity,employingData: EmployerEmployingInfo?) {
            var intent = Intent(activity, AttendanceRecordsActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }

        fun  intentStart (activity: AppCompatActivity,data: EmployerWaitCommentInfo?) {
            var intent = Intent(activity, AttendanceRecordsActivity::class.java)
            intent.putExtra(Constant.WAIT_COMMENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
        fun  intentStart (activity: AppCompatActivity,data: EmployerJobFinishInfo?) {
            var intent = Intent(activity, AttendanceRecordsActivity::class.java)
            intent.putExtra(Constant.JOB_FINISH_DATA_KEY,data)
            activity.startActivity(intent)
        }

    }

    override fun getLayoutResource() = R.layout.activity_attendance_records

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mEmployerAttendanceAdapter = EmployerAttendanceAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mEmployerAttendanceAdapter,mRvAttendanceRecords)

        mRvAttendanceRecords.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvDate.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mEmployerEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as EmployerEmployingInfo?
        mEmployerWaitCommentInfo = intent?.getSerializableExtra(Constant.WAIT_COMMENT_DATA_KEY)
                as EmployerWaitCommentInfo?
        mEmployerJobFinishInfo = intent?.getSerializableExtra(Constant.JOB_FINISH_DATA_KEY)
                as EmployerJobFinishInfo?

        if (mEmployerEmployingInfo != null) {
            mEmployerAttendanceAdapter?.showConfirmStatus = true
            var identity = ""
            if (mEmployerEmployingInfo?.identity == 1) {
                identity = "企业"
            } else if (mEmployerEmployingInfo?.identity == 2) {
                identity = "商户"
            } else if (mEmployerEmployingInfo?.identity == 3) {
                identity = "个人"
            }
            mTvCompany.text = "${mEmployerEmployingInfo?.employerName}(${identity})"

            var licenceAuth = mEmployerEmployingInfo?.licenceAuth ?: false
            if (licenceAuth) {
                mIvCompanyVerified.visibility = View.VISIBLE
            } else {
                mIvCompanyVerified.visibility = View.GONE
            }

            mTvTitle.text = mEmployerEmployingInfo?.title

            var jobStartTime = DateUtil.transDate(mEmployerEmployingInfo?.jobStartTime,
                "yyyy.MM.dd","MM.dd")
            var jobEndTime = DateUtil.transDate(mEmployerEmployingInfo?.jobEndTime,
                "yyyy.MM.dd","MM.dd")

            mTvWorkDate.text = "$jobStartTime-$jobEndTime(${mEmployerEmployingInfo?.totalDays}天)" +
                    "(${mEmployerEmployingInfo?.paidHour}小时/天)"

            mTvStartTime.text = mEmployerEmployingInfo?.startTime
            mTvEndTime.text = mEmployerEmployingInfo?.endTime
        }

        if (mEmployerWaitCommentInfo != null) {
            mEmployerAttendanceAdapter?.showConfirmStatus = false
            var identity = ""
            if (mEmployerWaitCommentInfo?.identity == 1) {
                identity = "企业"
            } else if (mEmployerWaitCommentInfo?.identity == 2) {
                identity = "商户"
            } else if (mEmployerWaitCommentInfo?.identity == 3) {
                identity = "个人"
            }
            mTvCompany.text = "${mEmployerWaitCommentInfo?.employerName}($identity)"

            var licenceAuth = mEmployerWaitCommentInfo?.licenceAuth ?: false
            if (licenceAuth) {
                mIvCompanyVerified.visibility = View.VISIBLE
            } else {
                mIvCompanyVerified.visibility = View.GONE
            }

            mTvTitle.text = mEmployerWaitCommentInfo?.title

            var jobStartTime = DateUtil.transDate(mEmployerWaitCommentInfo?.jobStartTime,
                "yyyy.MM.dd","MM.dd")
            var jobEndTime = DateUtil.transDate(mEmployerWaitCommentInfo?.jobEndTime,
                "yyyy.MM.dd","MM.dd")

            mTvWorkDate.text = "$jobStartTime-$jobEndTime(${mEmployerWaitCommentInfo?.totalDays}天)" +
                    "(${mEmployerWaitCommentInfo?.paidHour}小时/天)"

            mTvStartTime.text = mEmployerWaitCommentInfo?.startTime
            mTvEndTime.text = mEmployerWaitCommentInfo?.endTime
        }

        if (mEmployerJobFinishInfo != null) {
            mEmployerAttendanceAdapter?.showConfirmStatus = false
            var identity = ""
            if (mEmployerJobFinishInfo?.identity == 1) {
                identity = "企业"
            } else if (mEmployerJobFinishInfo?.identity == 2) {
                identity = "商户"
            } else if (mEmployerJobFinishInfo?.identity == 3) {
                identity = "个人"
            }
            mTvCompany.text = "${mEmployerJobFinishInfo?.employerName}($identity)"

            var licenceAuth = mEmployerJobFinishInfo?.licenceAuth ?: false
            if (licenceAuth) {
                mIvCompanyVerified.visibility = View.VISIBLE
            } else {
                mIvCompanyVerified.visibility = View.GONE
            }

            mTvTitle.text = mEmployerJobFinishInfo?.title

            var jobStartTime = DateUtil.transDate(mEmployerJobFinishInfo?.jobStartTime,
                "yyyy.MM.dd","MM.dd")
            var jobEndTime = DateUtil.transDate(mEmployerJobFinishInfo?.jobEndTime,
                "yyyy.MM.dd","MM.dd")

            mTvWorkDate.text = "$jobStartTime-$jobEndTime(${mEmployerJobFinishInfo?.totalDays}天)" +
                    "(${mEmployerJobFinishInfo?.paidHour}小时/天)"

            mTvStartTime.text = mEmployerJobFinishInfo?.startTime
            mTvEndTime.text = mEmployerJobFinishInfo?.endTime
        }

        sendAttendanceDateRequest()
    }

    fun subscribeUi() {
        attendanceVM.attendanceDateData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showAttendanceDateData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        attendanceVM.employerAttendanceData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showAttendanceData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        attendanceVM.employerConfirmAttendanceData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    sendEmployerAttendanceRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendAttendanceDateRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var employerReleaseId: String? = null
        if (mEmployerEmployingInfo != null) {
            employerReleaseId = mEmployerEmployingInfo?.employerReleaseId
        }
        if (mEmployerWaitCommentInfo != null) {
            employerReleaseId = mEmployerWaitCommentInfo?.employerReleaseId
        }
        if (mEmployerJobFinishInfo != null) {
            employerReleaseId = mEmployerJobFinishInfo?.employerReleaseId
        }

        attendanceVM.fetchAttendanceDate(token,employerReleaseId)
    }

    fun sendEmployerAttendanceRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token


        var body = EmployerAttendanceParm()
        body.pageNum = currentPage

        if (mEmployerEmployingInfo != null) {
            body.employerReleaseId = mEmployerEmployingInfo?.employerReleaseId
        }
        if (mEmployerWaitCommentInfo != null) {
            body.employerReleaseId = mEmployerWaitCommentInfo?.employerReleaseId
        }
        if (mEmployerJobFinishInfo != null) {
            body.employerReleaseId = mEmployerJobFinishInfo?.employerReleaseId
        }
        body.attendanceDate = mAttendanceDate

        attendanceVM.fetchEmployerAttendance(token,body)
    }

    fun sendEmployerAttendanceRequest (attendanceId: String?,attendanceType: Int) {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = EmployerConfirmAttendanceParm()
        body.attendanceId = attendanceId
        body.attendanceType = attendanceType

        attendanceVM.employerConfirmAttendance(token,body)
    }

    fun showAttendanceDateData (data: AttendanceDateReq) {
        mAttendanceDateReq = data
        var formater = "yyyy.MM.dd"
        var today = DateUtil.getTodayDate(formater)
        if (data.data == null || data.data?.size == 0) {
            //没有结算日期默认当天日期
            mAttendanceDate = today
        } else {
            mAttendanceDate = data.data?.get(0)
        }

        mTvDate.text = mAttendanceDate

        sendEmployerAttendanceRequest()

    }

    fun showAttendanceData (datas: EmployerAttendanceReq) {
        mEmployerAttendanceAdapter?.showData(datas.data,mTvNoData,mRvAttendanceRecords,currentPage)
    }

    fun showDisagreeAttendanceDlg (attendanceId: String?,attendanceType: Int) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您是否不认同人才当前的打卡，\n" +
                "如有异议请及时反馈，\n" +
                "发生争议时可作为参考依据"
        commonTipDialog.mOkText = "是"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendEmployerAttendanceRequest(attendanceId,attendanceType)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvDate -> {
                getAttendanceDateDialog().show()
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mEmployerAttendanceAdapter?.clear()
        mEmployerAttendanceAdapter?.setFooterVisible(false)
        mEmployerAttendanceAdapter?.notifyDataSetChanged()

        mRvAttendanceRecords.setHasMore(false)

        sendEmployerAttendanceRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendEmployerAttendanceRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when(view?.id) {
            R.id.mIvOnDutyConfirmStatus -> {
                if (mEmployerAttendanceAdapter?.getItem(position)?.onDutyStatus == 3
                        || mEmployerAttendanceAdapter?.getItem(position)?.onDutyStatus == 4) {
                    if (mEmployerAttendanceAdapter?.getItem(position)?.onDutyConfirmStatus == 1) {
                        var attendanceId = mEmployerAttendanceAdapter?.getItem(position)?.attendanceId
                        showDisagreeAttendanceDlg(attendanceId,1)
                    }
                }
            }
            R.id.mIvOffDutyConfirmStatus -> {
                if (mEmployerAttendanceAdapter?.getItem(position)?.offDutyStatus == 3
                        || mEmployerAttendanceAdapter?.getItem(position)?.offDutyStatus == 4) {
                    if (mEmployerAttendanceAdapter?.getItem(position)?.offDutyConfirmStatus == 1) {
                        var attendanceId = mEmployerAttendanceAdapter?.getItem(position)?.attendanceId
                        showDisagreeAttendanceDlg(attendanceId,2)
                    }
                }
            }
        }
    }


    fun getAttendanceDateDialog(): AttendanceDateDialog {

        var mSettlementDateDialog = AttendanceDateDialog(this)
        mSettlementDateDialog?.attendanceDate = mAttendanceDateReq?.data
        mSettlementDateDialog?.mOnAttendanceDateSelectListener = this
        return mSettlementDateDialog
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun OnAttendanceDateSelect(date: String?) {
        mAttendanceDate = date
        mTvDate.text = date

        currentPage = 1
        mEmployerAttendanceAdapter?.clear()
        mEmployerAttendanceAdapter?.setFooterVisible(false)
        mEmployerAttendanceAdapter?.notifyDataSetChanged()

        mRvAttendanceRecords.setHasMore(false)

        sendEmployerAttendanceRequest()
    }


}