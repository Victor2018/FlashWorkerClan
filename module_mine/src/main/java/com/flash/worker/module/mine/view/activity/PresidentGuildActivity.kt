package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnBitmapLoadListener
import com.flash.worker.lib.common.interfaces.OnShareListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.ShareDialog
import com.flash.worker.lib.coremodel.data.bean.GuildRedEnvelopeInfoData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.MyGuildData
import com.flash.worker.lib.coremodel.data.bean.ShareData
import com.flash.worker.lib.coremodel.data.parm.ShareGuidInfoParm
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildRedEnvelopeVM
import com.flash.worker.lib.coremodel.viewmodel.ShareVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.dialog.RewardReceiveDialog
import com.flash.worker.module.mine.view.interfaces.OnRewardReceiveListener
import kotlinx.android.synthetic.main.act_president_guild_header.*
import kotlinx.android.synthetic.main.activity_president_guild.*

@Route(path = ARouterPath.PresidentGuildAct)
class PresidentGuildActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
    OnRewardReceiveListener, OnShareListener,IShareListener, OnBitmapLoadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: MyGuildData?) {
            var intent = Intent(activity, PresidentGuildActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
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

    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX

    override fun getLayoutResource() = R.layout.activity_president_guild

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(this)
        mShareController = ShareController()
        mShareController?.setShareListener(this)

        subscribeUi()
        subscribeEvent()

        mIvBack.setOnClickListener(this)
        mTvGuildRegulation.setOnClickListener(this)
        mCivAvatar.setOnClickListener(this)
        mIvShare.setOnClickListener(this)
        mTvIntroduction.setOnClickListener(this)
        mTvGuildNews.setOnClickListener(this)
        mTvMonthlyIncome.setOnClickListener(this)
        mTvGuildReward.setOnClickListener(this)
        mTvNewsManagement.setOnClickListener(this)
        mTvMemberManagement.setOnClickListener(this)
        mTvMemberIncome.setOnClickListener(this)
        mTvManagementNotice.setOnClickListener(this)
        mTvViewMessage.setOnClickListener(this)
        mBtnGuildHall.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mMyGuildData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as MyGuildData?

        mTvTitle.text =mMyGuildData?.guildName

        ImageUtils.instance.loadImage(this,mCivAvatar,mMyGuildData?.headpic,R.mipmap.ic_president_avatar)

        var peopleNum = mMyGuildData?.peopleNum ?: 0
        if (peopleNum < 100) {
            mTvPeopleCount.text = "人数：100以内"
        } else {
            mTvPeopleCount.text = "人数:$peopleNum"
        }

        mTvCity.text = "所在城市:${mMyGuildData?.guildCity}"

        mTvEstablishTime.text = "成立时间:${mMyGuildData?.establishTime}"

        mTvIntroduction.text = "公会简介:${mMyGuildData?.guildProfile}"

        sendRedEnvelopeInfoRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(MineActions.UPDATE_GUILD_AVATAR_SUCCESS)
            .observe(this, Observer {
                mMyGuildData?.headpic = it.toString()
                ImageUtils.instance.loadImage(this,mCivAvatar,it.toString())
            })
    }

    fun subscribeUi() {
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
        body.guildId = mMyGuildData?.guildId

        shareVM.fetchShareGuildInfo(token,body)
    }

    fun showRewardReceiveDlg (data: GuildRedEnvelopeInfoData?) {
        if (data?.status == 1) {
            mTvRewardTip.visibility = View.VISIBLE

            var mRewardReceiveDialog = RewardReceiveDialog(this)
            mRewardReceiveDialog.mOnRewardReceiveListener = this
            mRewardReceiveDialog.mGuildRedEnvelopeInfoData = data

            mRewardReceiveDialog.show()
        } else {
            mTvRewardTip.visibility = View.GONE
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
            R.id.mTvGuildRegulation -> {
                GuildRegulationActivity.intentStart(this, mMyGuildData?.guildRules)
            }
            R.id.mCivAvatar -> {
                EditGuildAvatarActivity.intentStart(this,mMyGuildData?.guildId,mMyGuildData?.headpic)
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mTvIntroduction -> {
                GuildIntroductionActivity.intentStart(this,mMyGuildData)
            }
            R.id.mTvGuildNews -> {
                GuildNewsViewActivity.intentStart(this,mMyGuildData?.guildId)
                UMengEventModule.report(this, MineEvent.view_guild_news)
            }
            R.id.mTvMonthlyIncome -> {
                MonthlyIncomeActivity.intentStart(this,mMyGuildData?.guildId)
                UMengEventModule.report(this, MineEvent.view_guild_monthly_income)
            }
            R.id.mTvGuildReward -> {
                GuildRewardActivity.intentStart(this)
            }
            R.id.mTvNewsManagement -> {
                GuildNewsManageActivity.intentStart(this,0,mMyGuildData?.guildId)
            }
            R.id.mTvMemberManagement -> {
                MemberManagementActivity.intentStart(this,mMyGuildData?.guildId)
            }
            R.id.mTvMemberIncome -> {
                MemberIncomeActivity.intentStart(this,mMyGuildData?.guildId)
                UMengEventModule.report(this, MineEvent.view_member_income_statistics)
            }
            R.id.mTvManagementNotice -> {
                ManagementNoticeActivity.intentStart(this)
                UMengEventModule.report(this, MineEvent.view_mamagement_notes)
            }
            R.id.mTvViewMessage -> {
                NavigationUtils.goHomeActivity(this,3)
                onBackPressed()
            }
            R.id.mBtnGuildHall -> {
                GuildHallActivity.intentStart(this,1)
                UMengEventModule.report(this, MineEvent.enter_guild_hall)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun OnRewardReceive() {
        sendReceiveRedEnvelopeInfoRequest()
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