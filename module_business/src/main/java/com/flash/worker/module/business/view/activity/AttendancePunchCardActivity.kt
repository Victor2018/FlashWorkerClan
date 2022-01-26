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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TalentEmployingInfo
import com.flash.worker.lib.coremodel.data.parm.TalentOndutyParm
import com.flash.worker.lib.coremodel.data.parm.TalentAttendanceParm
import com.flash.worker.lib.coremodel.data.req.TalentAttendanceReq
import com.flash.worker.lib.coremodel.data.parm.TalentOffdutyParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.AttendanceVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.TalentAttendanceAdapter
import kotlinx.android.synthetic.main.activity_attendance_punch_card.*

class AttendancePunchCardActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener {

    var mLoadingDialog: LoadingDialog? = null
    var mTalentEmployingInfo: TalentEmployingInfo? = null
    var mTalentAttendanceAdapter: TalentAttendanceAdapter? = null
    var currentPage: Int = 1
    
    private val attendanceVM: AttendanceVM by viewModels {
        InjectorUtils.provideAttendanceVM(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,employingData: TalentEmployingInfo?) {
            var intent = Intent(activity, AttendancePunchCardActivity::class.java)
            intent.putExtra(Constant.EMPLOYING_DATA_KEY,employingData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_attendance_punch_card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent)
        initialize()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mTalentAttendanceAdapter = TalentAttendanceAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mTalentAttendanceAdapter,mRvAttendance)

        mRvAttendance.adapter = animatorAdapter
        
        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

    }

    fun initData (intent: Intent?) {
        mTalentEmployingInfo = intent?.getSerializableExtra(Constant.EMPLOYING_DATA_KEY)
                as TalentEmployingInfo?

        var identity = ""
        if (mTalentEmployingInfo?.identity == 1) {
            identity = "企业"
        } else if (mTalentEmployingInfo?.identity == 2) {
            identity = "商户"
        } else if (mTalentEmployingInfo?.identity == 3) {
            identity = "个人"
        }
        mTvCompany.text = "${mTalentEmployingInfo?.employerName}($identity)"

        var licenceAuth = mTalentEmployingInfo?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        mTvTitle.text = mTalentEmployingInfo?.title

        var jobStartTime = DateUtil.transDate(mTalentEmployingInfo?.jobStartTime,
                "yyyy.MM.dd","MM.dd")
        var jobEndTime = DateUtil.transDate(mTalentEmployingInfo?.jobEndTime,
                "yyyy.MM.dd","MM.dd")

        mTvWorkDate.text = "$jobStartTime-$jobEndTime(${mTalentEmployingInfo?.totalDays}天)" +
                "(${mTalentEmployingInfo?.paidHour}小时/天)"


        mTvStartTime.text = mTalentEmployingInfo?.startTime
        mTvEndTime.text = mTalentEmployingInfo?.endTime

        sendTalentAttendanceRequest()
    }

    fun subscribeUi() {
        attendanceVM.talentAttendanceData.observe(this, Observer {
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

        attendanceVM.talentOnDutyData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("上班打卡成功")
                    currentPage = 1
                    mTalentAttendanceAdapter?.clear()
                    mTalentAttendanceAdapter?.setFooterVisible(false)
                    mTalentAttendanceAdapter?.notifyDataSetChanged()

                    mRvAttendance.setHasMore(false)

                    sendTalentAttendanceRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        attendanceVM.talentOffDutyData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    currentPage = 1
                    mTalentAttendanceAdapter?.clear()
                    mTalentAttendanceAdapter?.setFooterVisible(false)
                    mTalentAttendanceAdapter?.notifyDataSetChanged()

                    mRvAttendance.setHasMore(false)

                    sendTalentAttendanceRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTalentAttendanceRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token


        var body = TalentAttendanceParm()
        body.pageNum = currentPage
        body.jobOrderId = mTalentEmployingInfo?.id

        attendanceVM.fetchTalentAttendance(token,body)
    }

    fun sendOndutyRequest (attendanceId: String?) {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentOndutyParm()
        body.attendanceId = attendanceId

        attendanceVM.talentOnDuty(token,body)
    }

    fun sendOffdutyRequest (attendanceId: String?) {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = TalentOffdutyParm()
        body.attendanceId = attendanceId

        attendanceVM.talentOffDuty(token,body)
    }

    fun showAttendanceData (datas: TalentAttendanceReq) {
        mTalentAttendanceAdapter?.showData(datas.data,mTvNoData,mRvAttendance,currentPage)
    }

    fun showAbnormalAttendanceDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才：\n" +
                "\t\t\t\t雇主不认可您当前的打卡，\n" +
                "为避免雇主投诉，请尽快和雇\n" +
                "主协商，并注意取证！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "知道了"
        commonTipDialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mTalentAttendanceAdapter?.clear()
        mTalentAttendanceAdapter?.setFooterVisible(false)
        mTalentAttendanceAdapter?.notifyDataSetChanged()

        mRvAttendance.setHasMore(false)

        sendTalentAttendanceRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendTalentAttendanceRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(view?.id) {
            R.id.mTvOnDuty -> {
                sendOndutyRequest(mTalentAttendanceAdapter?.getItem(position)?.attendanceId)
            }
            R.id.mTvOffDuty -> {
                sendOffdutyRequest(mTalentAttendanceAdapter?.getItem(position)?.attendanceId)
            }
            R.id.mIvOnDutyConfirmStatus -> {
                showAbnormalAttendanceDlg()
            }
            R.id.mIvOffDutyConfirmStatus -> {
                showAbnormalAttendanceDlg()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}