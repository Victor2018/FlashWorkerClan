package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnBitmapLoadListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnShareListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.ShareDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.QuitGuildParm
import com.flash.worker.lib.coremodel.data.parm.ShareGuidInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildRedEnvelopeVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.ShareVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.im.NimMessageUtil
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.dialog.RewardReceiveDialog
import com.flash.worker.module.mine.view.interfaces.OnGuildMoreListener
import com.flash.worker.module.mine.view.interfaces.OnRewardReceiveListener
import kotlinx.android.synthetic.main.act_my_guild_header.*
import kotlinx.android.synthetic.main.activity_my_guild.*

@Route(path = ARouterPath.MyGuildAct)
class MyGuildActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
        OnRewardReceiveListener, OnShareListener, IShareListener, OnBitmapLoadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: MyGuildData?) {
            var intent = Intent(activity, MyGuildActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
        fun  intentStart (activity: AppCompatActivity,data: GuildDetailData?) {
            var intent = Intent(activity, MyGuildActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val guildRedEnvelopeVM: GuildRedEnvelopeVM by viewModels {
        InjectorUtils.provideGuildRedEnvelopeVMFactory(this)
    }

    private val shareVM: ShareVM by viewModels {
        InjectorUtils.provideShareVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mShareDialog: ShareDialog? = null
    var mMyGuildData: MyGuildData? = null
    var mGuildDetailData: GuildDetailData? = null

    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX

    override fun getLayoutResource() = R.layout.activity_my_guild

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mShareController = ShareController()
        mShareController?.setShareListener(this)

        mIvBack.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mTvGuildRegulation.setOnClickListener(this)
        mTvIntroduction.setOnClickListener(this)
        mTvGuildNews.setOnClickListener(this)
        mTvMonthlyIncome.setOnClickListener(this)
        mTvGuildReward.setOnClickListener(this)
        mTvWechatGroup.setOnClickListener(this)
        mFabMore.setOnClickListener(this)
        scrim.setOnClickListener(this)
        mTvGuildHall.setOnClickListener(this)
        mTvExitGuild.setOnClickListener(this)

        val fabMarginEnd = resources.getDimensionPixelSize(R.dimen.dp_44)
        val fabMarginBottom = resources.getDimensionPixelSize(R.dimen.dp_50)
        ViewCompat.setOnApplyWindowInsetsListener(mClRoot) { _, insets ->
            mFabMore.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                rightMargin = fabMarginEnd + insets.systemWindowInsetRight
                bottomMargin = fabMarginBottom + insets.systemWindowInsetBottom
            }
            sheet.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                rightMargin = fabMarginEnd + insets.systemWindowInsetRight
                bottomMargin = fabMarginBottom + insets.systemWindowInsetBottom
            }
            insets
        }
    }

    fun initData (intent: Intent?) {
        var data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data is MyGuildData) {
            mMyGuildData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as MyGuildData?
            mTvTitle.text =mMyGuildData?.guildName

            ImageUtils.instance.loadImage(this,mCivAvatar,mMyGuildData?.headpic,R.mipmap.ic_president_avatar)

            mTvPresidentName.text = "会长:${mMyGuildData?.ownerUsername}"

            var peopleNum = mMyGuildData?.peopleNum ?: 0
            if (peopleNum < 100) {
                mTvPeopleCount.text = "人数：100以内"
            } else {
                mTvPeopleCount.text = "人数:$peopleNum"
            }

            mTvCity.text = "所在城市:${mMyGuildData?.guildCity}"
            mTvEstablishTime.text = "成立时间:${mMyGuildData?.establishTime}"

            if (mMyGuildData?.memberType == 1) {
                mTvMemberType.text = "我的职务: 会长"
            } else if (mMyGuildData?.memberType == 2) {
                mTvMemberType.text = "我的职务: 成员"
            }

            mTvIntroduction.text = "公会简介:${mMyGuildData?.guildProfile}"
        } else if (data is GuildDetailData) {
            mGuildDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as GuildDetailData?
            mTvTitle.text =mGuildDetailData?.guildName

            ImageUtils.instance.loadImage(this,mCivAvatar,mGuildDetailData?.headpic)
            mTvPresidentName.text = "会长:${mGuildDetailData?.ownerUsername}"

            var peopleNum = mGuildDetailData?.peopleNum ?: 0
            if (peopleNum < 100) {
                mTvPeopleCount.text = "人数：100以内"
            } else {
                mTvPeopleCount.text = "人数:$peopleNum"
            }

            mTvCity.text = "所在城市:${mGuildDetailData?.guildCity}"
            mTvEstablishTime.text = "成立时间:${mGuildDetailData?.establishTime}"
            mTvMemberType.text = "我的职务: 成员"

            mTvIntroduction.text = "公会简介:${mGuildDetailData?.guildProfile}"
        }

        sendRedEnvelopeInfoRequest()
    }

    fun subscribeUi() {
        guildVM.quitGuildData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("您已成功退出公会！")
                    onBackPressed()
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
                    NimMessageUtil.sendTxtMessage(it.value.data?.imAccid,"申请加入公会微信群")
                    showSendJoinWechatGroupMessageTipDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildRedEnvelopeVM.redEnvelopeInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showRewardReceiveDlg(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildRedEnvelopeVM.receiveRedEnvelopeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("领取成功")
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        shareVM.shareGuildInfoData.observe(this, Observer {
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

    fun sendQuitRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        val body = QuitGuildParm()
        body.guildId = mMyGuildData?.guildId

        guildVM.quitGuild(token,body)
    }

    fun sendImLoginInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        if (mMyGuildData != null) {
            userVM.fetchImLoginInfo(token,mMyGuildData?.ownerUserId)
        }
        if (mGuildDetailData != null) {
            userVM.fetchImLoginInfo(token,mGuildDetailData?.ownerUserId)
        }

    }

    fun sendRedEnvelopeInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        guildRedEnvelopeVM.fetchRedEnvelopeInfo(token)
    }

    fun sendReceiveRedEnvelopeInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        guildRedEnvelopeVM.receiveRedEnvelope(token)
    }

    fun sendShareGuildInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ShareGuidInfoParm()
        if (mMyGuildData != null) {
            body.guildId = mMyGuildData?.guildId
        }
        if (mGuildDetailData != null) {
            body.guildId = mGuildDetailData?.guildId
        }

        shareVM.fetchShareGuildInfo(token,body)
    }

    fun showQuitGuildTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才:\n\t\t\t\t您确认要退出${mMyGuildData?.guildName}吗？\n" +
                "\t\t\t\t退出后，每月1号才能加入新公会！"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确认退出"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendQuitRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showSendJoinWechatGroupMessageTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才:\n\t\t\t\t您已成功发送进群申请，稍后" +
                "可在消息页查看回复扫码进群。"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "查看消息"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goHomeActivity(this@MyGuildActivity,3)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showRewardReceiveDlg (data: GuildRedEnvelopeInfoData?) {
        if (data?.status == 1) {
            mViewGuildRewardTip.visibility = View.VISIBLE

            var mRewardReceiveDialog = RewardReceiveDialog(this)
            mRewardReceiveDialog.mOnRewardReceiveListener = this
            mRewardReceiveDialog.mGuildRedEnvelopeInfoData = data
            mRewardReceiveDialog.show()
        } else {
            mViewGuildRewardTip.visibility = View.GONE
        }

    }

    fun getShareDialog (): ShareDialog? {
        if (mShareDialog == null) {
            mShareDialog = ShareDialog(this)
            mShareDialog?.mOnShareListener = this
        }
        return mShareDialog
    }

    fun shareAction () {
        mLoadingDialog?.show()
        ImageUtils.instance.getBitmap(this,mShareData?.imageUrl,this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mTvGuildRegulation -> {
                if (mMyGuildData != null) {
                    GuildRegulationActivity.intentStart(this, mMyGuildData?.guildRules)
                }
                if (mGuildDetailData != null) {
                    GuildRegulationActivity.intentStart(this, mGuildDetailData?.guildRules)
                }
            }
            R.id.mTvIntroduction -> {
                if (mMyGuildData != null) {
                    GuildIntroductionActivity.intentStart(this,mMyGuildData)
                }

                if (mGuildDetailData != null) {
                    GuildIntroductionActivity.intentStart(this,mGuildDetailData)
                }
            }
            R.id.mTvGuildNews -> {
                if (mMyGuildData != null) {
                    GuildNewsViewActivity.intentStart(this,mMyGuildData?.guildId)
                }
                if (mGuildDetailData != null) {
                    GuildNewsViewActivity.intentStart(this,mGuildDetailData?.guildId)
                }

                UMengEventModule.report(this, MineEvent.view_guild_news)
            }
            R.id.mTvMonthlyIncome -> {
                if (mMyGuildData != null) {
                    MonthlyIncomeActivity.intentStart(this,mMyGuildData?.guildId)
                }
                if (mGuildDetailData != null) {
                    MonthlyIncomeActivity.intentStart(this,mGuildDetailData?.guildId)
                }

                UMengEventModule.report(this, MineEvent.view_guild_monthly_income)
            }
            R.id.mTvGuildReward -> {
                GuildRewardActivity.intentStart(this)
            }
            R.id.mTvWechatGroup -> {
                sendImLoginInfoRequest()
            }
            R.id.mFabMore -> {
                mFabMore.isExpanded = true
            }
            R.id.scrim -> {
                mFabMore.isExpanded = false
            }
            R.id.mTvGuildHall -> {
                mFabMore.isExpanded = false
                GuildHallActivity.intentStart(this,2)
                UMengEventModule.report(this, MineEvent.enter_guild_hall)
            }
            R.id.mTvExitGuild -> {
                mFabMore.isExpanded = false
                showQuitGuildTipDlg()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun OnRewardReceive() {
        sendReceiveRedEnvelopeInfoRequest()
    }

    override fun onBackPressed() {
        if (mFabMore.isExpanded) {
            mFabMore.isExpanded = false
        } else {
            super.onBackPressed()
        }
    }

    override fun OnShareFriend() {
        mShareType = IShareType.SHARE_WX
        sendShareGuildInfoRequest()
    }

    override fun OnShareFriendCircle() {
        mShareType = IShareType.SHARE_FRIENDS
        sendShareGuildInfoRequest()
    }

    override fun OnShareQQ() {
        mShareType = IShareType.SHARE_QQ
        sendShareGuildInfoRequest()
    }

    override fun OnShareQZone() {
        mShareType = IShareType.SHARE_QZONE
        sendShareGuildInfoRequest()
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

    override fun getAttachActivity() = this

    override fun onShareSuccess(shareType: Int) {
    }

    override fun onShareCancel(shareType: Int, error: String?) {
        ToastUtils.show(error)
    }

    override fun onShareError(shareType: Int, error: String?) {
        ToastUtils.show(error)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}