package com.flash.worker.module.job.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.event.JobEvent
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.module.SmsCountDownTimer
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.activity.ViolationReportActivity
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.JobWorkPicAdapter
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.EmployerCommentStatisticsReq
import com.flash.worker.lib.coremodel.data.req.EmployerLastCommentReq
import com.flash.worker.lib.coremodel.data.req.HomeEmployerDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.NimMessageUtil
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.job.R
import com.flash.worker.lib.common.view.adapter.EmployerCommentAdapter
import com.flash.worker.lib.livedatabus.action.*
import com.google.android.material.appbar.AppBarLayout
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.activity_job_detail.*
import kotlinx.android.synthetic.main.employer_detail_evaluation.*
import kotlinx.android.synthetic.main.job_detail_content.*
import kotlinx.android.synthetic.main.job_detail_count_down.*
import kotlinx.android.synthetic.main.job_detail_header.*

@Route(path = ARouterPath.JobDetailAct)
class JobDetailActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, OnShareListener,
    OnResumeSelectListener, OnCountDownTimerListener,RadioGroup.OnCheckedChangeListener,
    IShareListener, OnBitmapLoadListener,OnNameSettingListener {

    companion object {
        fun  intentStart (
                activity: AppCompatActivity, releaseId: String?,
                talentReleaseId: String?, resumeId: String?,action: Int) {
            var intent = Intent(activity, JobDetailActivity::class.java)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            intent.putExtra(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            intent.putExtra(Constant.RESUME_ID_KEY,resumeId)
            intent.putExtra(Constant.INTENT_ACTION_KEY,action)
            activity.startActivity(intent)
        }
    }

    var releaseId: String? = null//发布ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//人才简历ID
    var action: Int = 0//0,正常进入；1，预览进入；2，接受邀请进入

    var mLoadingDialog: LoadingDialog? = null
    var mShareDialog: ShareDialog? = null
    var mNameSettingDialog: NameSettingDialog? = null

    var mJobWorkPicAdapter: JobWorkPicAdapter? = null
    var mHomeEmployerDetailData: HomeEmployerDetailData? = null
    var mEmployerCommentAdapter: EmployerCommentAdapter? = null
    var mMyResumeInfo: MyResumeInfo? = null

    var mSmsCountDownTimer: SmsCountDownTimer? = null
    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX

    var currentPage = 1
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置
    var settingUserName: String? = null

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val employmentVM: EmploymentVM by viewModels {
        InjectorUtils.provideEmploymentVMFactory(this)
    }

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    private val talentFavReleaseVM: TalentFavReleaseVM by viewModels {
        InjectorUtils.provideTalentFavReleaseVMFactory(this)
    }

    private val shareVM: ShareVM by viewModels {
        InjectorUtils.provideShareVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_job_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)
        mShareController = ShareController()
        mShareController?.setShareListener(this)

        mJobWorkPicAdapter = JobWorkPicAdapter(this, this)
        mRvWorksPic.adapter = mJobWorkPicAdapter

        mEmployerCommentAdapter = EmployerCommentAdapter(this,this)
        mRvEvaluation.adapter = mEmployerCommentAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mIvFav.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mIvReport.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        mTvAllEvaluation.setOnClickListener(this)
        mTvCall.setOnClickListener(this)
        mTvChat.setOnClickListener(this)
        mTvSignUp.setOnClickListener(this)
        mTvCompany.setOnClickListener(this)
        mCivAvatar.setOnClickListener(this)
        mTvUserName.setOnClickListener(this)
        mTvEmployerCreditScore.setOnClickListener(this)

        appbar.addOnOffsetChangedListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder = mRvWorksPic
                                .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvJobPic)
                    }
                })

    }

    fun initData (intent: Intent?) {
        releaseId = intent?.getStringExtra(Constant.RELEASE_ID_KEY)
        talentReleaseId = intent?.getStringExtra(Constant.TALENT_RELEASE_ID_KEY)
        resumeId = intent?.getStringExtra(Constant.RESUME_ID_KEY)
        action = intent?.getIntExtra(Constant.INTENT_ACTION_KEY,0) ?: 0

        Loger.e(TAG,"initData()...releaseId = $releaseId")
        Loger.e(TAG,"initData()...talentReleaseId = $talentReleaseId")
        Loger.e(TAG,"initData()...resumeId = $resumeId")
        Loger.e(TAG,"initData()...action = $action")
        if (action == JobDetailAction.PREVIEW) {
            mTvCall.visibility = View.GONE
            mTvChat.visibility = View.GONE
            mTvSignUp.visibility = View.GONE
        } else if (action == JobDetailAction.NORMAL) {
            mTvSignUp.text = "报名"
        } else if (action == JobDetailAction.ACCEPT_INVITATION) {
            mTvSignUp.text = "接受邀请"
        }

        sendHomeEmployerDetailRequest()

        if (!NimMessageManager.instance.hasLogin()) {
            val userId = App.get().getLoginReq()?.data?.userId
            sendImLoginInfoRequest(userId)
        }

    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.BACK_HOME)
            .observe(this, Observer {
                finish()
            })

        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
                .observeForever(this, Observer {
                    currentWorkImagePositon = it as Int
                })

        LiveDataBus.with(JobActions.REFRESH_JOB_DETAIL)
                .observeForever(this, Observer {
                    sendHomeEmployerDetailRequest()
                    showNameSettingDlg()
                })
    }

    fun subscribeUi() {
        homeVM.homeEmployerDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showEmployerDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        resumeVM.userResumesData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getResumeDialog(it.value.data).show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.checkTalentBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NavigationUtils.goNewResumeActivity(this)
                    } else {
                        NavigationUtils.goNewTalentBasicActivity(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employmentVM.checkSignUpData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (!status) {
                        showIneligibleTipDlg(it.value.data?.msg)
                        return@Observer
                    }

                    if (action == JobDetailAction.NORMAL) {
                        SignUpActivity.intentStart(this,releaseId,talentReleaseId,mMyResumeInfo?.id)
                    } else if (action == JobDetailAction.ACCEPT_INVITATION) {
                        SignUpActivity.intentStart(this,releaseId,talentReleaseId,resumeId)
                    }
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("001050",it.code)) {
                        showReleaseOffshelfTipDlg(it.message.toString())
                        return@Observer
                    }
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.employerCommentStatisticsData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showCommentStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.employerLastCommentData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showLastCommentData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentFavReleaseVM.addFavData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mHomeEmployerDetailData?.favoriteStatus = true
                    ToastUtils.show("收藏成功")
                    mIvFav.setImageResource(R.mipmap.ic_fav_focus)

                    UMengEventModule.report(this, JobEvent.collect_employer_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentFavReleaseVM.cancelFavData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mHomeEmployerDetailData?.favoriteStatus = false
                    ToastUtils.show("取消收藏成功")
                    mIvFav.setImageResource(R.mipmap.ic_fav_normal)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        shareVM.shareInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mShareData = it.value.data
                    shareAction()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.imLoginInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (!NimMessageManager.instance.hasLogin()) {
                        var loginInfo = LoginInfo(it.value.data?.imAccid,it.value.data?.imToken)
                        NimMessageManager.instance.login(loginInfo)
                        return@Observer
                    }

                    NimMessageUtil.sendJobMessage(it.value.data?.imAccid,
                        JsonUtils.toJSONString(mHomeEmployerDetailData),"[岗位信息]")
                    NavigationUtils.goChatActivity(this,it.value.data?.imAccid)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.updateUserInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("设置成功")

                    var userInfo = App.get().getUserInfo()
                    userInfo?.username = settingUserName
                    App.get().setUserInfo(userInfo)

                    LiveDataBus.send(MineActions.REFRESH_USER_INFO)
                    LiveDataBus.send(TalentActions.CHECK_GUILD_RED_ENVELOPE)
                    LiveDataBus.send(HomeActions.SET_USER_NAME_SUCCESS)
                }
                is HttpResult.Error -> {
                    showNameSettingDlg()
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun setEmployerReleaseStatus (status: Int) {
        when (status) {
            2 -> {//发布中
                mIvStatus.visibility = View.GONE
            }
            3 -> {//已下架
                mIvStatus.visibility = View.VISIBLE
                mIvStatus.setImageResource(R.mipmap.ic_off_shelf)

                mIvFav.visibility = View.GONE
                mIvReport.visibility = View.GONE
                mIvShare.visibility = View.GONE

                mTvCall.visibility = View.GONE
                mTvChat.visibility = View.GONE
                mTvSignUp.visibility = View.GONE
            }
            5 -> {//已关闭
                mIvStatus.visibility = View.VISIBLE
                mIvStatus.setImageResource(R.mipmap.ic_closed)

                mIvFav.visibility = View.GONE
                mIvReport.visibility = View.GONE
                mIvShare.visibility = View.GONE

                mTvCall.visibility = View.GONE
                mTvChat.visibility = View.GONE
                mTvSignUp.visibility = View.GONE
            }
        }
    }

    fun setAppBarLayoutHeight (countDownTime: Long) {
        val lp: CoordinatorLayout.LayoutParams = appbar.layoutParams as CoordinatorLayout.LayoutParams
        lp.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
        if (countDownTime > 0) {
            mIvHeaderImage.setImageResource(R.mipmap.img_job_detail_count_down_header)
            lp.height = ResUtils.getDimenPixRes(R.dimen.dp_553)

            mClCountDown.visibility = View.VISIBLE
            mSmsCountDownTimer?.cancel()
            mSmsCountDownTimer = SmsCountDownTimer(countDownTime * 1000, 1000, this)
            mSmsCountDownTimer?.start()
        } else {
            mIvHeaderImage.setImageResource(R.mipmap.img_job_detail_header)
            lp.height = ResUtils.getDimenPixRes(R.dimen.dp_420)

            mClCountDown.visibility = View.GONE
        }
        appbar.layoutParams = lp
    }

    private fun setSpannable(identity: Int,companyTxt: String) {
        if (identity == 0) return
        if (TextUtils.isEmpty(companyTxt)) return

        var tip = ""
        if (identity == 1) {
            tip = "$companyTxt(企业)"
        } else if (identity == 2) {
            tip = "$companyTxt(商户)"
        } else if (identity == 3) {
            tip = "$companyTxt(个人)"
        }

        SpannableUtil.setSpannableUnderline(mTvCompany,tip,tip)
    }

    fun sendHomeEmployerDetailRequest () {
        if (!App.get().hasLogin()) {
            mSrlRefresh.isRefreshing = false
            LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        homeVM.fetchHomeEmployerDetail(token,releaseId)
    }

    fun sendUserResumeRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        resumeVM.fetchUserResumes(token)
    }

    fun sendCheckTalentBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkTalentBaseInfo(token)
    }

    fun sendEmployerCommentStatisticsRequest () {
        if (!App.get().hasLogin()) {
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerCommentStatisticsParm()
        body.employerId = mHomeEmployerDetailData?.employerId

        commentVM.fetchEmployerCommentStatistics(token,body)
    }

    fun sendEmployerLastCommentRequest () {
        if (!App.get().hasLogin()) {
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerLastCommentParm()
        body.employerId = mHomeEmployerDetailData?.employerId

        commentVM.fetchEmployerLastComment(token,body)
    }

    fun sendAddFavReleaseRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        var releaseId =  mHomeEmployerDetailData?.id
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentAddFavReleaseParm()
        body.employerReleaseId = releaseId

        talentFavReleaseVM.addFavRelease(token,body)
    }

    fun sendCancelFavReleaseRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        var releaseId =  mHomeEmployerDetailData?.id
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentCancelFavReleaseParm()
        body.employerReleaseId = releaseId

        talentFavReleaseVM.cancelFavRelease(token,body)
    }

    fun sendShareInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        var releaseId = mHomeEmployerDetailData?.id
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ShareInfoParm()
        body.releaseId = releaseId
        body.intentType = 0
        body.type = 2

        shareVM.fetchShareInfo(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(userId)) return
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun sendUpdateUserInfoRequest (username: String?,inviterUserId: String?) {
        settingUserName = username
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        var body = UpdateUserInfoParm()
        body.username = username
        if (!TextUtils.isEmpty(inviterUserId)) {
            body.inviterUserId = inviterUserId
        }

        userVM.updateUserInfo(token,body)
    }

    fun showEmployerDetailData (data: HomeEmployerDetailReq) {
        mHomeEmployerDetailData = data.data

        var isOpenContactPhone = data?.data?.isOpenContactPhone ?: false
        var contactPhone = data?.data?.contactPhone

        var favoriteStatus = data?.data?.favoriteStatus ?: false
        if (favoriteStatus) {
            mIvFav.setImageResource(R.mipmap.ic_fav_focus)
        } else {
            mIvFav.setImageResource(R.mipmap.ic_fav_normal)
        }

        var deadline = DateUtil.transDate(data?.data?.deadline,"yyyy.MM.dd HH:mm","yyyy年MM月dd日\t\tHH:mm\t\t录取截止")
        mTvDeadline.text = deadline

        mTvTitle.text = "${data?.data?.title}(${data?.data?.peopleCount}人)"

        if (data.data?.settlementMethod == 1) {
            mTvSettlementMethod.text = "日结"
        } else  if (data.data?.settlementMethod == 2) {
            mTvSettlementMethod.text = "周结"
        } else  if (data.data?.settlementMethod == 3) {
            mTvSettlementMethod.text = "整单结"
        }

        if (TextUtils.isEmpty(data.data?.eduRequirement)) {
            line_edu_requirement.visibility = View.GONE
            mTvEduRequirement.visibility = View.GONE
        } else {
            line_edu_requirement.visibility = View.VISIBLE
            mTvEduRequirement.visibility = View.VISIBLE
            mTvEduRequirement.text = data.data?.eduRequirement
        }

        if (data.data?.identityRequirement == 2) {
            line_student_only.visibility = View.VISIBLE
            mTvOnlyStudent.visibility = View.VISIBLE
        } else {
            line_student_only.visibility = View.GONE
            mTvOnlyStudent.visibility = View.GONE
        }
        var isAtHome = data.data?.isAtHome ?: false
        if (isAtHome) {
            line_do_at_home.visibility = View.VISIBLE
            mTvDoAtHome.visibility = View.VISIBLE
        } else {
            line_do_at_home.visibility = View.GONE
            mTvDoAtHome.visibility = View.GONE
        }

        if (data.data?.sexRequirement == 0) {
            mTvSex.text = "女"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (data.data?.sexRequirement == 1) {
            mTvSex.text = "男"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (data.data?.sexRequirement == 2) {
            mTvSex.text = "不限"
            line_sex.visibility = View.GONE
            mTvSex.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data.data?.ageRequirement)) {
            line_age.visibility = View.GONE
            mTvAge.visibility = View.GONE
        } else {
            line_age.visibility = View.VISIBLE
            mTvAge.visibility = View.VISIBLE
            mTvAge.text = data.data?.ageRequirement + "岁"
        }

        ImageUtils.instance.loadImage(this,mCivAvatar,data.data?.headpic)

        var identity = data.data?.identity ?: 0
        var employerName = data.data?.employerName ?: ""
        setSpannable(identity,employerName)

        mTvUserName.text = data.data?.username
        mTvWorkDate.text = "${data.data?.jobStartTime}-${data.data?.jobEndTime}(${data.data?.totalDays}天)"

        var isBeforeStartTime = DateUtil.isNextDayDate(data.data?.startTime,data.data?.endTime,"HH:mm")
        if (isBeforeStartTime) {
            mTvWorkTime.text = "${data.data?.startTime}-次日${data.data?.endTime}"
        } else {
            mTvWorkTime.text = "${data.data?.startTime}-${data.data?.endTime}"
        }

        mTvPaidHour.text = AmountUtil.getRoundUp(data.data?.paidHour ?: 0.0,1) + "小时"

        var totalAmount = AmountUtil.addCommaDots(data.data?.totalAmount)
        var unitPrice = AmountUtil.addCommaDots(data.data?.price)

        if (data.data?.payrollMethod == 1) {
            mTvTotalRemuneration.text = "${totalAmount}元(${unitPrice}元/小时)"
        } else if (data.data?.payrollMethod == 2) {
            mTvTotalRemuneration.text = "${totalAmount}元(${unitPrice}元/单)"
        }

        var workProvince = data.data?.workProvince ?: ""
        var workCity = data.data?.workCity ?: ""
        var workDistrict = data.data?.workDistrict ?: ""
        var address = data.data?.address ?: ""
        var workAddr = workProvince + workCity + workDistrict + address

        if (isAtHome && TextUtils.isEmpty(address)) {
            mTvWorkAddr.text = "线上"
        } else {
            mTvWorkAddr.text = workAddr
        }

        var workPicsCount = data.data?.pics?.size ?: 0
        if (workPicsCount > 0) {
            tv_work_pic.visibility = View.VISIBLE
            mRvWorksPic.visibility = View.VISIBLE
        } else {
            tv_work_pic.visibility = View.GONE
            mRvWorksPic.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data.data?.workDescription)) {
            tv_work_description.visibility = View.GONE
            mTvWorkDescription.visibility = View.GONE
        } else {
            tv_work_description.visibility = View.VISIBLE
            mTvWorkDescription.visibility = View.VISIBLE
        }

        mJobWorkPicAdapter?.clear()
        mJobWorkPicAdapter?.add(getWorkPics(data.data?.pics))
        mJobWorkPicAdapter?.notifyDataSetChanged()

        mTvWorkDescription.text = data.data?.workDescription

        mTvUserId.text = "ID:${data.data?.userId}"
        mTvEmployerCreditScore.text = "信用分: ${data.data?.employerCreditScore}"

        var licenceAuth = data.data?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        var countDownTime = data.data?.countDownTime ?: 0
        var checkSignUpStatus = data.data?.checkSignup?.status ?: false

        setAppBarLayoutHeight(countDownTime)
        setEmployerReleaseStatus(data.data?.status ?: 0)

        if (checkSignUpStatus) {
            mTvSignUp.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
            if (isOpenContactPhone && !TextUtils.isEmpty(contactPhone)) {
                mTvCall.visibility = View.VISIBLE
            } else {
                mTvCall.visibility = View.GONE
            }
        } else {
            mTvCall.visibility = View.GONE
            mTvChat.visibility = View.GONE
            mTvSignUp.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
            mTvSignUp.text = data.data?.checkSignup?.msg
        }

        sendEmployerCommentStatisticsRequest()
        sendEmployerLastCommentRequest()
    }

    fun showCommentStatisticsData (data: EmployerCommentStatisticsReq) {
        tv_evaluation.text = "评价(${AmountUtil.getEvaluationCount(data.data?.totalCommentNum ?: 0)})"
        mRbVeryGood.text = AmountUtil.getEvaluationCount(data.data?.goodCommentNum ?: 0)
        mRbGeneral.text = AmountUtil.getEvaluationCount(data.data?.generalCommentNum ?: 0)
        mRbVeryBad.text = AmountUtil.getEvaluationCount(data.data?.badCommentNum ?: 0)
    }

    fun showLastCommentData (datas: EmployerLastCommentReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mEmployerCommentAdapter?.clear()
            mEmployerCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mEmployerCommentAdapter?.clear()
            mEmployerCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data?.size == 0) {
            if (currentPage == 1) {
                mTvNoData.visibility = View.VISIBLE
                mRvEvaluation.visibility = View.GONE
                mEmployerCommentAdapter?.clear()
                mEmployerCommentAdapter?.notifyDataSetChanged()
                mRvEvaluation.setHasMore(false)
                return
            }
        }
        mTvNoData.visibility = View.GONE
        mRvEvaluation.visibility = View.VISIBLE
        if (currentPage == 1) {
            mEmployerCommentAdapter?.clear()
        }
        mEmployerCommentAdapter?.add(datas?.data)

        var count = datas?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvEvaluation.setHasMore(false)
            mEmployerCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING_END)
        } else {
            mRvEvaluation.setHasMore(true)
            mEmployerCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING)
        }
        mEmployerCommentAdapter?.notifyDataSetChanged()
    }

    fun sendCheckSignUpRequest (resumeId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CheckSignUpParm()
        body.employerReleaseId = releaseId
        body.resumeId = resumeId

        if (action == JobDetailAction.NORMAL) {
            body.source = 1
        } else if (action == JobDetailAction.ACCEPT_INVITATION) {
            body.source = 2
            body.talentReleaseId = talentReleaseId
        }

        employmentVM.checkSignUp(token,body)
    }

    fun getWorkPics (pics: List<String>?): List<WorkPicInfo>? {
        var datas = ArrayList<WorkPicInfo>()

        if (pics == null) return datas
        if (pics.size == 0) return datas

        for (url in pics) {
            var data = WorkPicInfo()
            data.pic = url
            datas.add(data)
        }
        return datas
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvFav -> {
                var favoriteStatus = mHomeEmployerDetailData?.favoriteStatus ?: false
                if (favoriteStatus) {
                    sendCancelFavReleaseRequest()
                } else {
                    sendAddFavReleaseRequest()
                }
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mIvReport -> {
                ViolationReportActivity.intentStart(this,false,releaseId)
            }
            R.id.mTvCall -> {
                if (!isPermissionGranted(Manifest.permission.CALL_PHONE)) {
                    requestPermission(arrayOf(Manifest.permission.CALL_PHONE))
                    return
                }
                var contactPhone = mHomeEmployerDetailData?.contactPhone
                showCallDlg(contactPhone)
            }
            R.id.mTvChat -> {
                val userId = mHomeEmployerDetailData?.userId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvSignUp -> {
                if (mHomeEmployerDetailData?.checkSignup == null) {
                    ToastUtils.show("数据错误")
                    return
                }
                var status = mHomeEmployerDetailData?.checkSignup?.status ?: false
                if (!status) return

                if (action == JobDetailAction.NORMAL) {
                    if (mHomeEmployerDetailData?.checkSignup?.actionType == 1) {
                        showAuthTipDlg()
                    } else if (mHomeEmployerDetailData?.checkSignup?.actionType == 2) {
                        showNewResumeDlg()
                    } else if (mHomeEmployerDetailData?.checkSignup?.actionType == 3) {
                        sendUserResumeRequest()
                    }
                } else if (action == JobDetailAction.ACCEPT_INVITATION) {
                    sendCheckSignUpRequest(resumeId)
                }

            }
            R.id.mTvUserId -> {
                val userId = mHomeEmployerDetailData?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvAllEvaluation -> {
                val employerId = mHomeEmployerDetailData?.employerId
                EmployerAllEvaluationActivity.intentStart(this,employerId,0)
                UMengEventModule.report(this, JobEvent.view_employer_all_evaluation)
            }
            R.id.mTvCompany -> {
                HirerDetailActivity.intentStart(this,mHomeEmployerDetailData?.employerId)
            }
            R.id.mCivAvatar -> {
                EmployerDetailActivity.intentStart(this,mHomeEmployerDetailData,null,null)
            }
            R.id.mTvUserName -> {
                EmployerDetailActivity.intentStart(this,mHomeEmployerDetailData,null,null)
            }
            R.id.mTvEmployerCreditScore -> {
                EmployerDetailActivity.intentStart(this,mHomeEmployerDetailData,null,null)
            }
        }
    }

    fun getShareDialog (): ShareDialog? {
        if (mShareDialog == null) {
            mShareDialog = ShareDialog(this)
            mShareDialog?.mOnShareListener = this
        }
        return mShareDialog
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "根据我国《网络安全法》相关规定\n您需要进行" +
                "身份认证后才能使用报名功能"
        commonTipDialog.mCancelText = "暂不认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@JobDetailActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showNewResumeDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还没有简历哟，\n" +
                "请先新增简历后再报名吧！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "新增简历"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendCheckTalentBaseInfoRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getResumeDialog (data: List<MyResumeInfo>?): MyResumeDialog {
        var mMyResumeDialog = MyResumeDialog(this)
        mMyResumeDialog?.resumeList = data
        mMyResumeDialog?.mOnResumeSelectListener = this

        return mMyResumeDialog
    }

    /**
     * 显示不符合录用条件弹窗
     */
    fun showIneligibleTipDlg (error: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = error
        commonTipDialog.mOkText = "返回"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }
    /**
     * 显示工单下架弹窗
     */
    fun showReleaseOffshelfTipDlg (error: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = error
        commonTipDialog.mOkText = "继续浏览"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.show()
    }

    fun showAddResumeTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您的简历数已达上限可删除后再新增！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCallDlg (tel: String?) {
        var mCallDialog = CallDialog(this)
        mCallDialog.tel = tel
        mCallDialog.show()
    }

    fun shareAction () {
        mLoadingDialog?.show()
        ImageUtils.instance.getBitmap(this,mShareData?.imageUrl,this)
    }

    fun showNameSettingDlg () {
        if (!TextUtils.isEmpty(App.get().getUserInfo()?.username)) return

        if (mNameSettingDialog == null) {
            mNameSettingDialog = NameSettingDialog(this)
            mNameSettingDialog?.mOnNameSettingListener = this
        }
        mNameSettingDialog?.show()
    }

    override fun OnNameSetting(name: String?, inviteId: String?) {
        sendUpdateUserInfoRequest(name,inviteId)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentWorkImagePositon = position
        when (view?.id) {
            R.id.mIvJobPic -> {
                ViewImageActivity.intentStart(this,
                    getViewImageUrls(mJobWorkPicAdapter?.getDatas()),
                    position,
                    view?.findViewById(R.id.mIvJobPic),
                    ResUtils.getStringRes(R.string.img_transition_name))
            }
            else -> {

            }
        }

    }

    override fun onRefresh() {
        sendHomeEmployerDetailRequest()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

    override fun OnResumeSelect(data: MyResumeInfo?, resumeCount: Int) {
        if (data == null) {
            if (resumeCount >= 5) {
                showAddResumeTipDlg()
                return
            }
            sendCheckTalentBaseInfoRequest()
            return
        }
        mMyResumeInfo = data

        sendCheckSignUpRequest(data?.id)
    }

    override fun onTick(millisUntilFinished: Long) {
        //防止计时过程中重复点击
        if (isFinishing()) return
        var data = DateUtil.ms2SysCreateTime(millisUntilFinished)
        mTvDay.text = data.day.toString()
        mTvHour.text = data.hour.toString()
        mTvMinute.text = data.minute.toString()
        mTvSecond.text = data.second.toString()
    }

    override fun onFinish() {
        //防止计时过程中重复点击
        if (isFinishing) return
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val employerId = mHomeEmployerDetailData?.employerId
        when (checkedId) {
            R.id.mRbVeryGood -> {
                EmployerAllEvaluationActivity.intentStart(this,employerId,1)
            }
            R.id.mRbGeneral -> {
                EmployerAllEvaluationActivity.intentStart(this,employerId,2)
            }
            R.id.mRbVeryBad -> {
                EmployerAllEvaluationActivity.intentStart(this,employerId,3)
            }
        }
    }

    fun getViewImageUrls(urls: List<WorkPicInfo>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                it.pic?.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }

    override fun OnShareFriend() {
        mShareType = IShareType.SHARE_WX
        sendShareInfoRequest()
    }

    override fun OnShareFriendCircle() {
        mShareType = IShareType.SHARE_FRIENDS
        sendShareInfoRequest()
    }

    override fun OnShareQQ() {
        mShareType = IShareType.SHARE_QQ
        sendShareInfoRequest()
    }

    override fun OnShareQZone() {
        mShareType = IShareType.SHARE_QZONE
        sendShareInfoRequest()
    }

    override fun getAttachActivity() = this

    override fun onShareSuccess(shareType: Int) {
    }

    override fun onShareCancel(shareType: Int, error: String?) {
        ToastUtils.show(error)
    }

    override fun onShareError(shareType: Int, error: String?) {
        ToastUtils.show(error)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mShareController?.onActivityResult(requestCode, resultCode, data)
    }

    override fun OnBitmapLoad(bitmap: Bitmap?) {
        mLoadingDialog?.dismiss()
        var bean = ShareInfo()
        bean.cover = mShareData?.imageUrl
        bean.title = mShareData?.title
        bean.summary = mShareData?.description
        bean.url = mShareData?.shareUrl

        mShareController?.setShareInfo(bean)
        var mShareImage = ShareImage()
        mShareImage.setImage(bitmap)
        mShareController?.setShareImage(mShareImage)

        mShareController?.invokeShare(this, mShareType)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSmsCountDownTimer?.onFinish()
        mSmsCountDownTimer = null

        mShareController?.onDestroy()
        mShareController = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}