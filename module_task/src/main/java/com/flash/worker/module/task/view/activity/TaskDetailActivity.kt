package com.flash.worker.module.task.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.interfaces.OnBitmapLoadListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnResumeSelectListener
import com.flash.worker.lib.common.interfaces.OnShareListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.activity.ViolationReportActivity
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.EmployerCommentAdapter
import com.flash.worker.lib.common.view.adapter.JobWorkPicAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.MyResumeDialog
import com.flash.worker.lib.common.view.dialog.ShareDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.EmployerCommentStatisticsReq
import com.flash.worker.lib.coremodel.data.req.EmployerLastCommentReq
import com.flash.worker.lib.coremodel.data.req.TaskDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.NimMessageUtil
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.TaskActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.task.R
import com.flash.worker.module.task.interfaces.OnTaskCountInputListener
import com.flash.worker.module.task.view.dialog.TaskCountInputDialog
import com.google.android.material.appbar.AppBarLayout
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.activity_task_detail.*
import kotlinx.android.synthetic.main.task_detail_content.*
import kotlinx.android.synthetic.main.task_detail_evaluation.*
import kotlinx.android.synthetic.main.task_detail_header.*
import kotlin.math.max

@Route(path = ARouterPath.TaskDetailAct)
class TaskDetailActivity : BaseActivity(),View.OnClickListener,AppBarLayout.OnOffsetChangedListener,
    SwipeRefreshLayout.OnRefreshListener, OnTaskCountInputListener, AdapterView.OnItemClickListener,
    OnResumeSelectListener,RadioGroup.OnCheckedChangeListener, OnShareListener, IShareListener,
    OnBitmapLoadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, releaseId: String?,
                          talentReleaseId: String?, resumeId: String?,action: Int) {
            var intent = Intent(activity, TaskDetailActivity::class.java)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            intent.putExtra(Constant.TALENT_RELEASE_ID_KEY,talentReleaseId)
            intent.putExtra(Constant.RESUME_ID_KEY,resumeId)
            intent.putExtra(Constant.INTENT_ACTION_KEY,action)
            activity.startActivity(intent)
        }
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    private val shareVM: ShareVM by viewModels {
        InjectorUtils.provideShareVMFactory(this)
    }

    var releaseId: String? = null//发布ID
    var talentReleaseId: String? = null//人才发布ID
    var resumeId: String? = null//人才简历ID
    var action: Int = 0//0,正常进入；1，预览进入

    var mLoadingDialog: LoadingDialog? = null
    var mShareDialog: ShareDialog? = null
    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX
    var mTaskDetailData: TaskDetailData? = null
    var mJobWorkPicAdapter: JobWorkPicAdapter? = null
    var mEmployerCommentAdapter: EmployerCommentAdapter? = null
    var currentPage = 1
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置
    var mMyResumeInfo: MyResumeInfo? = null

    override fun getLayoutResource() = R.layout.activity_task_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
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
        mIvReport.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mTvChat.setOnClickListener(this)
        mTvLeadTask.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        mTvAllEvaluation.setOnClickListener(this)
        mTvCompany.setOnClickListener(this)
        mCivAvatar.setOnClickListener(this)
        mTvUserName.setOnClickListener(this)
        mTvEmployerCreditScore.setOnClickListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)

        appbar.addOnOffsetChangedListener(this)

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

        if (action == TaskDetailAction.PREVIEW) {
            mTvChat.visibility = View.GONE
            line_chat.visibility = View.GONE
            mTvLeadTask.visibility = View.GONE
        } else if (action == TaskDetailAction.NORMAL) {
            mTvLeadTask.text = "我要领任务"
        } else if (action == TaskDetailAction.ACCEPT_INVITATION) {
            mTvLeadTask.text = "接受邀请"
        }

        sendTaskDetailRequest()

        if (!NimMessageManager.instance.hasLogin()) {
            val userId = App.get().getLoginReq()?.data?.userId
            sendImLoginInfoRequest(userId)
        }
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
            .observeForever(this, Observer {
                currentWorkImagePositon = it as Int
            })

        LiveDataBus.with(TaskActions.EXIT_TASK_DETAIL)
            .observeForever(this, Observer {
                finish()
            })
    }

    fun subscribeUi() {
        homeVM.taskDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                   showTaskDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
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

        userVM.imLoginInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (!NimMessageManager.instance.hasLogin()) {
                        var loginInfo = LoginInfo(it.value.data?.imAccid,it.value.data?.imToken)
                        NimMessageManager.instance.login(loginInfo)
                        return@Observer
                    }

                    NimMessageUtil.sendTaskMessage(it.value.data?.imAccid,
                        JsonUtils.toJSONString(mTaskDetailData),"[任务信息]")
                    NavigationUtils.goChatActivity(this,it.value.data?.imAccid)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
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

        shareVM.shareTaskInfoData.observe(this, Observer {
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

    }

    fun setEmployerReleaseStatus (status: Int) {
        when (status) {
            2 -> {//发布中
                mIvStatus.visibility = View.GONE
            }
            3 -> {//已下架
                mIvStatus.visibility = View.VISIBLE
                mIvStatus.setImageResource(R.mipmap.ic_off_shelf)

                mIvReport.visibility = View.GONE

                mTvChat.visibility = View.GONE
                line_chat.visibility = View.GONE
                mTvLeadTask.visibility = View.GONE
            }
            5 -> {//已关闭
                mIvStatus.visibility = View.VISIBLE
                mIvStatus.setImageResource(R.mipmap.ic_closed)

                mIvReport.visibility = View.GONE

                mTvChat.visibility = View.GONE
                line_chat.visibility = View.GONE
                mTvLeadTask.visibility = View.GONE
            }
        }
    }

    fun sendTaskDetailRequest () {
        if (!App.get().hasLogin()) return
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        homeVM.fetchTaskDetail(token,releaseId)
    }

    fun sendEmployerCommentStatisticsRequest () {
        if (!App.get().hasLogin()) {
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerCommentStatisticsParm()
        body.employerId = mTaskDetailData?.employerId

        commentVM.fetchEmployerCommentStatistics(token,body)
    }

    fun sendEmployerLastCommentRequest () {
        if (!App.get().hasLogin()) {
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerLastCommentParm()
        body.employerId = mTaskDetailData?.employerId

        commentVM.fetchEmployerLastComment(token,body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(userId)) return
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
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

    fun sendShareInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        var releaseId = mTaskDetailData?.releaseId
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ShareTaskInfoParm()
        body.releaseId = releaseId

        shareVM.fetchShareTaskInfo(token,body)
    }

    fun showTaskDetailData (data: TaskDetailReq) {
        mTaskDetailData = data.data

        mTvTitle.text = data.data?.title
        mTvTaskCount.text = "${data.data?.taskQty}件"

        var timesLimit = data.data?.timesLimit ?: 0
        if (timesLimit == 1) {
            mTvTimesLimit.text = "一人一件"
        } else if (timesLimit == 2) {
            mTvTimesLimit.text = "一人多件"
        }

        var sexRequirement = data.data?.sexRequirement ?: 0
        if (sexRequirement == 0) {
            mTvSex.text = "女"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (sexRequirement == 1) {
            mTvSex.text = "男"
            line_sex.visibility = View.VISIBLE
            mTvSex.visibility = View.VISIBLE
        } else if (sexRequirement == 2) {
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
            mTvAge.text = "${data.data?.ageRequirement}岁"
        }

        var identity = data.data?.identity ?: 0
        var employerName = data.data?.employerName ?: ""
        setSpannable(identity,employerName)

        var licenceAuth = data.data?.licenceAuth ?: false
        if (licenceAuth) {
            mIvCompanyVerified.visibility = View.VISIBLE
        } else {
            mIvCompanyVerified.visibility = View.GONE
        }

        ImageUtils.instance.loadImage(this,mCivAvatar,data.data?.headpic)

        mTvUserName.text = data.data?.username
        mTvUserId.text = "ID:${data.data?.userId}"
        mTvEmployerCreditScore.text = "信用分: ${data.data?.employerCreditScore}"

        mTvSettlementTime.text = "${data.data?.settlementTimeLimit}小时内"

        var finishTimeLimitUnit = data.data?.finishTimeLimitUnit ?: 0
        if (finishTimeLimitUnit == 1) {
            mTvFinishTimeLimit.text = "限${data.data?.finishTimeLimit}小时完成"
        } else if (finishTimeLimitUnit == 2) {
            mTvFinishTimeLimit.text = "限${data.data?.finishTimeLimit}天完成"
        }
        mTvPrice.text = "${AmountUtil.addCommaDots(data.data?.price)}元/件"

        mTvWorkDescription.text = data.data?.workDescription

        mFlSubmit.removeAllViews()
        var submitLabel = data.data?.submitLabel ?: ""
        var inflater =  LayoutInflater.from(this)
        if (!TextUtils.isEmpty(submitLabel)) {
            if (submitLabel?.contains(",")) {
                var labels = submitLabel.split(",")
                labels.forEach {
                    val mTvLabel = inflater.inflate(R.layout.fl_task_submit_label_cell, null) as TextView
                    mTvLabel.text = it
                    mFlSubmit.addView(mTvLabel)
                }
            } else {
                val mTvLabel = inflater.inflate(R.layout.fl_task_submit_label_cell, null) as TextView
                mTvLabel.text = submitLabel
                mFlSubmit.addView(mTvLabel)
            }
        }

        var picCount = data.data?.pics?.size ?: 0
        if (picCount > 0) {
            tv_pic.visibility = View.VISIBLE
        } else {
            tv_pic.visibility = View.GONE
        }
        mJobWorkPicAdapter?.clear()
        mJobWorkPicAdapter?.add(getWorkPics(data.data?.pics))
        mJobWorkPicAdapter?.notifyDataSetChanged()

        var checkSignUpStatus = data.data?.requirementInfo?.status ?: false
        if (checkSignUpStatus) {
            mTvLeadTask.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))
        } else {
            mTvChat.visibility = View.GONE
            line_chat.visibility = View.GONE
            mTvLeadTask.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
            mTvLeadTask.text = data.data?.requirementInfo?.msg
        }

        setEmployerReleaseStatus(data.data?.status ?: 0)

        sendEmployerCommentStatisticsRequest()
        sendEmployerLastCommentRequest()
    }

    fun shareAction () {
        mLoadingDialog?.show()
        ImageUtils.instance.getBitmap(this,mShareData?.imageUrl,this)
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
            R.id.mIvReport -> {
                ViolationReportActivity.intentStart(this,false,releaseId)
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mTvChat -> {
                val userId = mTaskDetailData?.userId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvLeadTask -> {
                if (mTaskDetailData?.requirementInfo == null) {
                    ToastUtils.show("数据错误")
                    return
                }
                var status = mTaskDetailData?.requirementInfo?.status ?: false
                if (!status) return

                if (action == TaskDetailAction.NORMAL) {
                    if (mTaskDetailData?.requirementInfo?.actionType == 1) {
                        showAuthTipDlg()
                    } else if (mTaskDetailData?.requirementInfo?.actionType == 2) {
                        showNewResumeDlg()
                    } else if (mTaskDetailData?.requirementInfo?.actionType == 3) {
                        sendUserResumeRequest()
                    }
                } else if (action == TaskDetailAction.ACCEPT_INVITATION) {
                    showTaskCountInputDlg()
                }
            }
            R.id.mTvUserId -> {
                val userId = mTaskDetailData?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvAllEvaluation -> {
                val employerId = mTaskDetailData?.employerId
                NavigationUtils.goEmployerAllEvaluationActivity(this,employerId,0)
            }
            R.id.mTvCompany -> {
                val employerId = mTaskDetailData?.employerId
                NavigationUtils.goHirerDetailActivity(this,employerId)
            }
            R.id.mCivAvatar -> {
                NavigationUtils.goEmployerDetailActivity(this,mTaskDetailData)
            }
            R.id.mTvUserName -> {
                NavigationUtils.goEmployerDetailActivity(this,mTaskDetailData)
            }
            R.id.mTvEmployerCreditScore -> {
                NavigationUtils.goEmployerDetailActivity(this,mTaskDetailData)
            }
        }
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

    fun leadTaskAction (body: ReceiveTaskDetailParm?) {
        if (action == TaskDetailAction.ACCEPT_INVITATION) {
            LeadTaskActivity.intentStart(this,body,resumeId,talentReleaseId)
        } else {
            LeadTaskActivity.intentStart(this,body,mMyResumeInfo?.id,null)
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

    override fun onRefresh() {
        sendTaskDetailRequest()
    }
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val employerId = mTaskDetailData?.employerId
        when (checkedId) {
            R.id.mRbVeryGood -> {
                NavigationUtils.goEmployerAllEvaluationActivity(this,employerId,1)
            }
            R.id.mRbGeneral -> {
                NavigationUtils.goEmployerAllEvaluationActivity(this,employerId,2)
            }
            R.id.mRbVeryBad -> {
                NavigationUtils.goEmployerAllEvaluationActivity(this,employerId,3)
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

    fun showTaskCountInputDlg() {
        var timesLimit = mTaskDetailData?.timesLimit ?: 0
        if (timesLimit == 1) {
            var body = ReceiveTaskDetailParm()
            body.employerReleaseId = mTaskDetailData?.releaseId
            body.taskReceiveQty = 1

            leadTaskAction(body)
            return
        }
        var taskQty = mTaskDetailData?.taskQty ?: 0
        var taskReceiveQty = mTaskDetailData?.taskReceiveQty ?: 0
        var maxCount = taskQty - taskReceiveQty
        if (maxCount <= 0) {
            showTaskReceiveFinishTipDlg()
            return
        }
        if (maxCount == 1) {
            var body = ReceiveTaskDetailParm()
            body.employerReleaseId = mTaskDetailData?.releaseId
            body.taskReceiveQty = 1

            leadTaskAction(body)
            return
        }
        var taskCountInputDialog = TaskCountInputDialog(this)
        taskCountInputDialog.maxCount = maxCount
        taskCountInputDialog.mTitleTxt = "剩余件数：$maxCount"
        taskCountInputDialog.mOnTaskCountInputListener = this
        taskCountInputDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "根据我国《网络安全法》相关规定\n您需要进行" +
                "身份认证后才能使用领取功能"
        commonTipDialog.mCancelText = "暂不认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@TaskDetailActivity)
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
                "请先新增简历后再领取吧！"
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

    /**
     * 显示任务被领完弹窗
     */
    fun showTaskReceiveFinishTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "很抱歉，该任务已被领完，请返回领取其他任务吧！"
        commonTipDialog.mOkText = "回任务列表"
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

        showTaskCountInputDlg()
    }

    override fun OnTaskCountInput(count: Int) {
        var body = ReceiveTaskDetailParm()
        body.employerReleaseId = mTaskDetailData?.releaseId
        body.taskReceiveQty = count

        leadTaskAction(body)
    }

    override fun onDestroy() {
        super.onDestroy()
        mShareController?.onDestroy()
        mShareController = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}