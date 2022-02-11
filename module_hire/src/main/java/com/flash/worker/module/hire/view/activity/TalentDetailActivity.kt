package com.flash.worker.module.hire.view.activity

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
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.event.TalentEvent
import com.flash.worker.lib.common.interfaces.OnBitmapLoadListener
import com.flash.worker.lib.common.interfaces.OnNameSettingListener
import com.flash.worker.lib.common.interfaces.OnShareListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViolationReportActivity
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.dialog.CallDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.NameSettingDialog
import com.flash.worker.lib.common.view.dialog.ShareDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ShareData
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.HomeTalentDetailReq
import com.flash.worker.lib.coremodel.data.req.TalentCommentStatisticsReq
import com.flash.worker.lib.coremodel.data.req.TalentLastCommentReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.im.NimMessageUtil
import com.flash.worker.lib.livedatabus.action.*
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.adapter.HireQualificationAdapter
import com.flash.worker.module.hire.view.adapter.HireWorkExperienceAdapter
import com.flash.worker.module.hire.view.adapter.HireWorkPicAdapter
import com.flash.worker.module.hire.view.adapter.TalentCommentAdapter
import com.google.android.material.appbar.AppBarLayout
import com.library.flowlayout.FlowLayoutManager
import com.netease.nimlib.sdk.auth.LoginInfo
import kotlinx.android.synthetic.main.activity_talent_detail.*
import kotlinx.android.synthetic.main.rv_hire_qualification_cell.view.*
import kotlinx.android.synthetic.main.talent_detail_content.*
import kotlinx.android.synthetic.main.talent_detail_evaluation.*
import kotlinx.android.synthetic.main.talent_detail_header.*
import kotlinx.android.synthetic.main.talent_detail_resume.*

@Route(path = ARouterPath.TalentDetailAct)
class TalentDetailActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,AppBarLayout.OnOffsetChangedListener, OnShareListener,
    RadioGroup.OnCheckedChangeListener, IShareListener, OnBitmapLoadListener, OnNameSettingListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,releaseId: String?,action: Int) {
            var intent = Intent(activity, TalentDetailActivity::class.java)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            intent.putExtra(Constant.INTENT_ACTION_KEY,action)
            activity.startActivity(intent)
        }
    }

    var mHireWorkExperienceAdapter: HireWorkExperienceAdapter? = null
    var mHireQualificationAdapter: HireQualificationAdapter? = null
    var mHireWorkPicAdapter: HireWorkPicAdapter? = null
    var mTalentCommentAdapter: TalentCommentAdapter? = null
    var mShareDialog: ShareDialog? = null
    var mLoadingDialog: LoadingDialog? = null
    var mNameSettingDialog: NameSettingDialog? = null

    var mTalentDetailReq: HomeTalentDetailReq? = null
    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX

    var releaseId: String? = null//发布ID
    var action: Int = 0//0,正常进入；1，普通预览进入；2，发布预览进入

    var currentPage = 1
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    var settingUserName: String? = null

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    private val employerFavReleaseVM: EmployerFavReleaseVM by viewModels {
        InjectorUtils.provideEmployerFavReleaseVMFactory(this)
    }

    private val shareVM: ShareVM by viewModels {
        InjectorUtils.provideShareVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_talent_detail

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

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mHireWorkExperienceAdapter = HireWorkExperienceAdapter(this,this)
        mRvWorkExperience.adapter = mHireWorkExperienceAdapter

        var flowLayoutManager = FlowLayoutManager()
        mRvQualification.layoutManager = flowLayoutManager

        mHireQualificationAdapter = HireQualificationAdapter(this,this)
        mRvQualification.adapter = mHireQualificationAdapter

        mHireWorkPicAdapter = HireWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mHireWorkPicAdapter

        mTalentCommentAdapter = TalentCommentAdapter(this,this)
        mRvEvaluation.adapter = mTalentCommentAdapter

        mIvBack.setOnClickListener(this)
        mIvFav.setOnClickListener(this)
        mIvReport.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mTvUserId.setOnClickListener(this)
        mTvAllEvaluation.setOnClickListener(this)
        mTvCall.setOnClickListener(this)
        mTvChat.setOnClickListener(this)
        mTvInviteTalent.setOnClickListener(this)

        appbar_layout.addOnOffsetChangedListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder = mRvWorksPic
                                .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvWorkPic)
                    }
                })

    }

    fun initData (intent: Intent?) {
        releaseId = intent?.getStringExtra(Constant.RELEASE_ID_KEY)
        action = intent?.getIntExtra(Constant.INTENT_ACTION_KEY,0) ?: 0

        if (action == TalentDetailAction.NORMAL) {
            mIvFav.visibility = View.VISIBLE
            mIvShare.visibility = View.VISIBLE
            mIvReport.visibility = View.VISIBLE
            mTvInviteTalent.visibility = View.VISIBLE
            mTvToolBarTitle.text = "人才详情"
        } else if (action == TalentDetailAction.PREVIEW) {
            mIvFav.visibility = View.GONE
            mIvShare.visibility = View.INVISIBLE
            mIvReport.visibility = View.GONE
            mTvInviteTalent.visibility = View.GONE
            mTvToolBarTitle.text = "人才详情"
        } else if (action == TalentDetailAction.RELEASE_PREVIEW) {
            mIvFav.visibility = View.GONE
            mIvShare.visibility = View.VISIBLE
            mIvReport.visibility = View.GONE
            mTvInviteTalent.visibility = View.GONE
            mTvToolBarTitle.text = "发布详情"
        }
        sendHomeTalentDetailRequest()
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

        LiveDataBus.with(TalentActions.REFRESH_TALENT_DETAIL)
            .observeForever(this, Observer {
                sendHomeTalentDetailRequest()
                showNameSettingDlg()
            })
    }

    fun subscribeUi() {
        homeVM.homeTalentDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.talentCommentStatisticsData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showCommentStatisticsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.talentLastCommentData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showLastCommentData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerFavReleaseVM.addFavData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mTalentDetailReq?.data?.favoriteStatus = true
                    ToastUtils.show("收藏成功")
                    mIvFav.setImageResource(R.mipmap.ic_fav_focus)

                    UMengEventModule.report(this, TalentEvent.collect_talent_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerFavReleaseVM.cancelFavData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mTalentDetailReq?.data?.favoriteStatus = false
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

                    NimMessageUtil.sendTalentReleaseMessage(it.value.data?.imAccid,
                        JsonUtils.toJSONString(mTalentDetailReq?.data))

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

    fun sendHomeTalentDetailRequest () {
        if (!App.get().hasLogin()) {
            LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        homeVM.fetchHomeTalentDetail(token,releaseId)
    }

    fun sendTalentCommentStatisticsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentCommentStatisticsParm()
        body.userId = mTalentDetailReq?.data?.userInfo?.userId

        commentVM.fetchTalentCommentStatistics(token,body)
    }

    fun sendTalentLastCommentRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = TalentLastCommentParm()
        body.userId = mTalentDetailReq?.data?.userInfo?.userId

        commentVM.fetchTalentLastComment(token,body)
    }

    fun sendAddFavReleaseRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        var releaseId =  mTalentDetailReq?.data?.talentReleaseInfo?.releaseId
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerAddFavReleaseParm()
        body.talentReleaseId = releaseId

        employerFavReleaseVM.addFavRelease(token,body)
    }

    fun sendCancelFavReleaseRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        var releaseId =  mTalentDetailReq?.data?.talentReleaseInfo?.releaseId
        if (TextUtils.isEmpty(releaseId)) {
            ToastUtils.show("数据错误")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = EmployerCancelFavReleaseParm()
        body.talentReleaseId = releaseId

        employerFavReleaseVM.cancelFavRelease(token,body)
    }

    fun sendShareInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        var releaseId = mTalentDetailReq?.data?.talentReleaseInfo?.releaseId
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
        body.type = 1

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

    fun showTalentDetailData (data: HomeTalentDetailReq?) {
        mTalentDetailReq = data

        var isOpenContactPhone = data?.data?.talentReleaseInfo?.isOpenContactPhone ?: false
        var contactPhone = data?.data?.talentReleaseInfo?.contactPhone

        var favoriteStatus = data?.data?.favoriteStatus ?: false
        if (favoriteStatus) {
            mIvFav.setImageResource(R.mipmap.ic_fav_focus)
        } else {
            mIvFav.setImageResource(R.mipmap.ic_fav_normal)
        }

        if (data?.data?.userInfo == null) {
            showCancelAccountErrorTip()
            return
        }

        ImageUtils.instance.loadImage(this,mCivAvatar,data?.data?.userInfo?.headpic,R.mipmap.ic_avatar)
        mTvUserName.text = data?.data?.userInfo?.username
        mTvUserId.text = "ID:${data?.data?.userInfo?.userId}"
        mTvTalentCreditScore.text = "信用分: ${data?.data?.userInfo?.talentCreditScore}"
        if (data?.data?.userInfo?.sex == 0) {
            mTvSex.text = "女"
        } else if (data?.data?.userInfo?.sex == 1) {
            mTvSex.text = "男"
        } else {
            mTvSex.text = "其他"
        }
        mTvAge.text = "${data?.data?.userInfo?.age}岁"
        mTvHeight.text = "${data?.data?.userInfo?.height}cm"
        mTvWeight.text = "${data?.data?.userInfo?.weight}kg"

        var height = data?.data?.userInfo?.height ?: 0
        var weight = data?.data?.userInfo?.weight ?: 0
        if (height > 0) {
            line_identity.visibility = View.VISIBLE
            mTvHeight.visibility = View.VISIBLE
        } else {
            line_identity.visibility = View.GONE
            mTvHeight.visibility = View.GONE
        }
        if (weight > 0) {
            line_height.visibility = View.VISIBLE
            mTvWeight.visibility = View.VISIBLE
        } else {
            line_height.visibility = View.GONE
            mTvWeight.visibility = View.GONE
        }

        mTvLiveCity.text = data?.data?.userInfo?.liveCity + data?.data?.userInfo?.liveDistrict
        mTvWorkYears.text = data?.data?.userInfo?.workYears
        mTvTitle.text = data?.data?.talentReleaseInfo?.title

        if (data?.data?.talentReleaseInfo?.settlementMethod == 1) {
            mTvUnitPrice.text = "${data?.data?.talentReleaseInfo?.price}元/小时"
        } else {
            mTvUnitPrice.text = "${data?.data?.talentReleaseInfo?.price}元/单"
        }

        var isAtHome = data?.data?.talentReleaseInfo?.isAtHome ?: false
        if (isAtHome) {
            mTvServiceArea.text = "全国"
        } else {
            if (TextUtils.isEmpty(data?.data?.talentReleaseInfo?.workDistrict)) {
                mTvServiceArea.text = data?.data?.talentReleaseInfo?.workCity
            } else {
                mTvServiceArea.text = data?.data?.talentReleaseInfo?.workDistrict?.replace(",","、")
            }
        }

        mTvEducation.text = data?.data?.resumeInfo?.highestEducation

        if (data?.data?.userInfo?.identity == 1) {//职场人士
            mTvIdentity.visibility = View.GONE
            line_identity.visibility = View.GONE
        } else if (data?.data?.userInfo?.identity == 2) {//学生
            mTvIdentity.visibility = View.VISIBLE
            line_identity.visibility = View.VISIBLE
        }

        var workExperiencesCount = data?.data?.resumeInfo?.resumeWorkExperiences?.size ?: 0
        if (workExperiencesCount > 0) {
            tv_work_experience.visibility = View.VISIBLE
            mRvWorkExperience.visibility = View.VISIBLE
            line_work_experience.visibility = View.VISIBLE
        } else {
            tv_work_experience.visibility = View.GONE
            mRvWorkExperience.visibility = View.GONE
            line_work_experience.visibility = View.GONE
        }

        var certificatesCount = data?.data?.resumeInfo?.resumeCertificates?.size ?: 0
        if (certificatesCount > 0) {
            tv_qualification.visibility = View.VISIBLE
            mRvQualification.visibility = View.VISIBLE
            line_work_qualification.visibility = View.VISIBLE
        } else {
            tv_qualification.visibility = View.GONE
            mRvQualification.visibility = View.GONE
            line_work_qualification.visibility = View.GONE
        }

        var workPicsCount = data?.data?.resumeInfo?.resumeWorkPics?.size ?: 0
        if (workPicsCount > 0) {
            tv_work_pic.visibility = View.VISIBLE
            mRvWorksPic.visibility = View.VISIBLE
            line_work_pic.visibility = View.VISIBLE
        } else {
            tv_work_pic.visibility = View.GONE
            mRvWorksPic.visibility = View.GONE
            line_work_pic.visibility = View.GONE
        }

        if (TextUtils.isEmpty(data?.data?.resumeInfo?.selfDescription)) {
            tv_introduction.visibility = View.GONE
            mTvIntroduction.visibility = View.GONE
        } else {
            tv_introduction.visibility = View.VISIBLE
            mTvIntroduction.visibility = View.VISIBLE
        }

        mHireWorkExperienceAdapter?.clear()
        mHireWorkExperienceAdapter?.add(data?.data?.resumeInfo?.resumeWorkExperiences)
        mHireWorkExperienceAdapter?.notifyDataSetChanged()

        mHireQualificationAdapter?.clear()
        mHireQualificationAdapter?.add(data?.data?.resumeInfo?.resumeCertificates)
        mHireQualificationAdapter?.notifyDataSetChanged()

        mHireWorkPicAdapter?.clear()
        mHireWorkPicAdapter?.add(data?.data?.resumeInfo?.resumeWorkPics)
        mHireWorkPicAdapter?.notifyDataSetChanged()

        mTvIntroduction.text = data?.data?.resumeInfo?.selfDescription

        if (action == TalentDetailAction.NORMAL) {
            var status = data?.data?.checkSendInviteInfo?.status ?: false
            if (status) {
                mTvInviteTalent.text = "邀请人才"
                mTvInviteTalent.setBackgroundColor(ResUtils.getColorRes(R.color.color_F7E047))

                if (data?.data?.talentReleaseInfo?.status == 1) {//编辑中
                    mIvFav.visibility = View.GONE
                    mIvReport.visibility = View.GONE
                    mIvShare.visibility = View.GONE

                    mTvCall.visibility = View.GONE
                    mTvChat.visibility = View.GONE
                    mTvInviteTalent.visibility = View.GONE

                } else if (data?.data?.talentReleaseInfo?.status == 2) {//发布中
                    mIvFav.visibility = View.VISIBLE
                    mIvReport.visibility = View.VISIBLE
                    mIvShare.visibility = View.VISIBLE

                    if (isOpenContactPhone && !TextUtils.isEmpty(contactPhone)) {
                        mTvCall.visibility = View.VISIBLE
                    } else {
                        mTvCall.visibility = View.GONE
                    }
                    mTvChat.visibility = View.VISIBLE
                    mTvInviteTalent.visibility = View.VISIBLE

                } else if (data?.data?.talentReleaseInfo?.status == 3) {//已下架
                    mIvFav.visibility = View.GONE
                    mIvReport.visibility = View.GONE
                    mIvShare.visibility = View.GONE

                    mTvCall.visibility = View.GONE
                    mTvChat.visibility = View.GONE
                    mTvInviteTalent.visibility = View.GONE
                } else if (data?.data?.talentReleaseInfo?.status == 4) {//已驳回
                    mIvFav.visibility = View.GONE
                    mIvReport.visibility = View.GONE
                    mIvShare.visibility = View.GONE

                    mTvCall.visibility = View.GONE
                    mTvChat.visibility = View.GONE
                    mTvInviteTalent.visibility = View.GONE
                }
            } else {
                mTvCall.visibility = View.GONE
                mTvChat.visibility = View.GONE
                mTvInviteTalent.setBackgroundColor(ResUtils.getColorRes(R.color.color_DDDDDD))
                mTvInviteTalent.text = data?.data?.checkSendInviteInfo?.msg
            }
        } else if (action == TalentDetailAction.PREVIEW) {
            mIvFav.visibility = View.GONE
            mIvReport.visibility = View.GONE
            mIvShare.visibility = View.GONE
            mTvCall.visibility = View.GONE
            mTvChat.visibility = View.GONE
            mTvInviteTalent.visibility = View.GONE
        } else if (action == TalentDetailAction.RELEASE_PREVIEW) {
            mIvFav.visibility = View.GONE
            mIvReport.visibility = View.GONE
            mIvShare.visibility = View.VISIBLE
            mTvCall.visibility = View.GONE
            mTvChat.visibility = View.GONE
            mTvInviteTalent.visibility = View.GONE
        }

        sendTalentCommentStatisticsRequest()
        sendTalentLastCommentRequest()
    }

    fun showCancelAccountErrorTip () {
        mTvTalentDeleted.visibility = View.VISIBLE
        mIvFav.visibility = View.GONE
        mIvReport.visibility = View.GONE
        mIvShare.visibility = View.GONE
        cl_header.visibility = View.GONE
        cl_content.visibility = View.GONE
        cl_resume.visibility = View.GONE
        cl_evaluation.visibility = View.GONE
        mTvCall.visibility = View.GONE
        mTvChat.visibility = View.GONE
        mTvInviteTalent.visibility = View.GONE
    }

    fun showCommentStatisticsData (data: TalentCommentStatisticsReq) {
        var totalCommentNum = AmountUtil.getEvaluationCount(data.data?.totalCommentNum ?: 0)
        tv_evaluation.text = "评价($totalCommentNum)"
        mRbVeryGood.text = AmountUtil.getEvaluationCount(data.data?.goodCommentNum ?: 0)
        mRbGeneral.text = AmountUtil.getEvaluationCount(data.data?.generalCommentNum ?: 0)
        mRbVeryBad.text = AmountUtil.getEvaluationCount(data.data?.badCommentNum ?: 0)
    }

    fun showLastCommentData (datas: TalentLastCommentReq) {
        if (datas == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mTalentCommentAdapter?.clear()
            mTalentCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mRvEvaluation.visibility = View.GONE
            mTalentCommentAdapter?.clear()
            mTalentCommentAdapter?.notifyDataSetChanged()
            mRvEvaluation.setHasMore(false)
            return
        }
        if (datas.data?.size == 0) {
            if (currentPage == 1) {
                mTvNoData.visibility = View.VISIBLE
                mRvEvaluation.visibility = View.GONE
                mTalentCommentAdapter?.clear()
                mTalentCommentAdapter?.notifyDataSetChanged()
                mRvEvaluation.setHasMore(false)
                return
            }
        }
        mTvNoData.visibility = View.GONE
        mRvEvaluation.visibility = View.VISIBLE
        if (currentPage == 1) {
            mTalentCommentAdapter?.clear()
        }
        mTalentCommentAdapter?.add(datas?.data)

        var count = datas?.data?.size ?: 0
        if (count < WebConfig.PAGE_SIZE) {
            mRvEvaluation.setHasMore(false)
            mTalentCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING_END)
        } else {
            mRvEvaluation.setHasMore(true)
            mTalentCommentAdapter?.setLoadState(BaseRecycleAdapter.LOADING)
        }
        mTalentCommentAdapter?.notifyDataSetChanged()
    }

    fun getShareDialog (): ShareDialog? {
        if (mShareDialog == null) {
            mShareDialog = ShareDialog(this)
            mShareDialog?.mOnShareListener = this
        }
        return mShareDialog
    }

    fun showNameSettingDlg () {
        if (!TextUtils.isEmpty(App.get().getUserInfo()?.username)) return

        if (mNameSettingDialog == null) {
            mNameSettingDialog = NameSettingDialog(this)
            mNameSettingDialog?.mOnNameSettingListener = this
        }
        mNameSettingDialog?.show()
    }

    fun showCallDlg (tel: String?) {
        var mCallDialog = CallDialog(this)
        mCallDialog.tel = tel
        mCallDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvFav -> {
                var favoriteStatus = mTalentDetailReq?.data?.favoriteStatus ?: false
                if (favoriteStatus) {
                    sendCancelFavReleaseRequest()
                } else {
                    sendAddFavReleaseRequest()
                }
            }
            R.id.mIvReport -> {
                var releaseId = mTalentDetailReq?.data?.talentReleaseInfo?.releaseId
                ViolationReportActivity.intentStart(this,true,releaseId)
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mTvUserId -> {
                val userId = mTalentDetailReq?.data?.userInfo?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
            R.id.mTvAllEvaluation -> {
                val userId = mTalentDetailReq?.data?.userInfo?.userId
                TalentAllEvaluationActivity.intentStart(this,userId,0)
                UMengEventModule.report(this, TalentEvent.view_talent_all_evaluation)
            }
            R.id.mTvCall -> {
                if (!isPermissionGranted(Manifest.permission.CALL_PHONE)) {
                    requestPermission(arrayOf(Manifest.permission.CALL_PHONE))
                    return
                }
                var contactPhone = mTalentDetailReq?.data?.talentReleaseInfo?.contactPhone
                showCallDlg(contactPhone)
            }
            R.id.mTvChat -> {
                val userId = mTalentDetailReq?.data?.userInfo?.userId
                sendImLoginInfoRequest(userId)
            }
            R.id.mTvInviteTalent -> {
                if (!App.get().hasLogin()) {
                    ToastUtils.show("请先登录")
                    return
                }

                var status = mTalentDetailReq?.data?.checkSendInviteInfo?.status ?: false
                if (status) {
                    var releaseId = mTalentDetailReq?.data?.talentReleaseInfo?.releaseId
                    var username = mTalentDetailReq?.data?.userInfo?.username
                    InviteTalentActivity.intentStart(this, releaseId,username)
                }
            }
        }
    }

    fun shareAction () {
        mLoadingDialog?.show()
        ImageUtils.instance.getBitmap(this,mShareData?.imageUrl,this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mClWorkExperience -> {

            }
            R.id.mClQualification -> {

            }
            R.id.mIvWorkPic -> {
                currentWorkImagePositon = position
                ViewImageActivity.intentStart(this,
                        getViewImageUrls(mHireWorkPicAdapter?.getDatas()),
                        position,
                        view?.findViewById(R.id.mIvWorkPic),
                        ResUtils.getStringRes(R.string.img_transition_name))
            }
        }
    }

    override fun onRefresh() {
        sendHomeTalentDetailRequest()
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val userId = mTalentDetailReq?.data?.userInfo?.userId
        when (checkedId) {
            R.id.mRbVeryGood -> {
                TalentAllEvaluationActivity.intentStart(this,userId,1)
            }
            R.id.mRbGeneral -> {
                TalentAllEvaluationActivity.intentStart(this,userId,2)
            }
            R.id.mRbVeryBad -> {
                TalentAllEvaluationActivity.intentStart(this,userId,3)
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

    override fun OnNameSetting(name: String?, inviteId: String?) {
        sendUpdateUserInfoRequest(name,inviteId)
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