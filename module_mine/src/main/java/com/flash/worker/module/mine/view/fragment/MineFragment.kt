package com.flash.worker.module.mine.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.util.ConfigLocal
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.push.TagAliasOperatorHelper
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.GuildRedEnvelopeTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.dialog.ShareDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ShareData
import com.flash.worker.lib.coremodel.data.bean.UserInfo
import com.flash.worker.lib.coremodel.data.parm.ShareInfoParm
import com.flash.worker.lib.coremodel.data.req.WaitReceiveCountReq
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.coremodel.viewmodel.factory.*
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.module.mine.R
import com.google.android.material.appbar.AppBarLayout
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.share.data.ShareImage
import com.flash.worker.lib.share.data.ShareInfo
import com.flash.worker.lib.share.interfaces.IShareListener
import com.flash.worker.lib.share.interfaces.IShareType
import com.flash.worker.lib.share.module.ShareController
import com.flash.worker.module.mine.view.activity.*
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.fragment_mine_header.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MineFragment
 * Author: Victor
 * Date: 2020/11/27 16:33
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.MineFgt)
class MineFragment: BaseFragment(), SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
        AdapterView.OnItemClickListener, AppBarLayout.OnOffsetChangedListener,
        OnJoinGuildListener, OnLocationListener, OnShareListener, IShareListener, OnBitmapLoadListener {

    private lateinit var authVM: AuthVM
    private lateinit var userVM: UserVM
    private lateinit var fileVM: FileVM
    private lateinit var accountVM: AccountVM
    private lateinit var guildVM: GuildVM
    private lateinit var talentRedEnvelopeVM: TalentRedEnvelopeVM
    private lateinit var guildRedEnvelopeVM: GuildRedEnvelopeVM
    private lateinit var shareVM: ShareVM

    var mLoadingDialog: LoadingDialog? = null

    var mUserInfo: UserInfo? = null

    var mGuildRedEnvelopeTipDialog: GuildRedEnvelopeTipDialog? = null
    var mShareDialog: ShareDialog? = null

    var mShareController: ShareController? = null
    var mShareData: ShareData? = null
    var mShareType = IShareType.SHARE_WX

    companion object {

        fun newInstance(): MineFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): MineFragment {
            val fragment = MineFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_mine
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        authVM = ViewModelProvider(this, AuthVMFactory(this))
                .get(AuthVM::class.java)

        userVM = ViewModelProvider(this, UserVMFactory(this))
                .get(UserVM::class.java)

        accountVM = ViewModelProvider(this, AccountVMFactory(this))
                .get(AccountVM::class.java)

        fileVM = ViewModelProvider(this, FileVMFactory(this))
                .get(FileVM::class.java)

        guildVM = ViewModelProvider(this, GuildVMFactory(this))
                .get(GuildVM::class.java)

        talentRedEnvelopeVM = ViewModelProvider(this, TalentRedEnvelopeVMFactory(this))
                .get(TalentRedEnvelopeVM::class.java)

        guildRedEnvelopeVM = ViewModelProvider(this, GuildRedEnvelopeVMFactory(this))
                .get(GuildRedEnvelopeVM::class.java)

        shareVM = ViewModelProvider(this, ShareVMFactory(this))
                .get(ShareVM::class.java)

        subscribeEvent()
        subscribeUi()

        mLoadingDialog = LoadingDialog(activity!!)

        mShareController = ShareController()
        mShareController?.setShareListener(this)

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        appbar.addOnOffsetChangedListener(this)
        mCivAvatar.setOnClickListener(this)
        mIvEditProfile.setOnClickListener(this)
        mIvShare.setOnClickListener(this)

        mCvWallet.setOnClickListener(this)
        mTvRedEnvelope.setOnClickListener(this)
        mTvMyGuild.setOnClickListener(this)
        mCvEmploymentReward.setOnClickListener(this)
        mCvCoupon.setOnClickListener(this)
        mTvCustomerService.setOnClickListener(this)
        mTvViolationNotice.setOnClickListener(this)
        mTvSetting.setOnClickListener(this)
        mCvAbout.setOnClickListener(this)
        mCvLogOut.setOnClickListener(this)
    }

    fun initData () {
        mUserInfo = App.get().getUserInfo()

        if (App.get().hasLogin()) {
            mTvLogout.text = "退出登录"
        } else {
            mTvLogout.text = "登录"
            mTvAccountBalance.setNumberString("0.00")
            mIvGuildRedEnvelope.visibility = View.GONE
        }

        sendUserInfoRequest()
        sendAccountInfoRequest()
        sendWaitReceiveCountRequest()
        sendGuildRedEnvelopeTipsRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(MineActions.REFRESH_RED_ENVELOPE_TIP)
            .observe(this, Observer {
                sendWaitReceiveCountRequest()
            })

        LiveDataBus.with(MineActions.REFRESH_USER_INFO)
            .observe(this, Observer {
                showUserInfoData(App.get().getUserInfo())
                sendGuildRedEnvelopeTipsRequest()
            })

        LiveDataBus.with(MineActions.REFRESH_EMPLOYMENT_REWARD_TIP)
            .observe(this, Observer {
                sendWaitReceiveCountRequest()
            })

        LiveDataBus.with(MineActions.REFRESH_LOGIN_STATUS)
            .observe(this, Observer {
                if (it is Boolean) {
                    var hasLogin: Boolean = it
                    if (hasLogin) {
                        mTvLogout.text = "退出登录"
                    } else {
                        mTvLogout.text = "登录"
                    }
                    showUserInfoData(App.get().getUserInfo())
                }
            })
    }

    fun subscribeUi() {
        authVM.logOutData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().resetLoginData()
                    mTvAccountBalance.setNumberString("0.00")
                    NimMessageManager.instance.logout()
                    TagAliasOperatorHelper.instance.deleteAlias()

                    ToastUtils.show("成功退出")
                    initData()

                    UMengEventModule.report(activity, MineEvent.logout)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.userInfoData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(it.value.data)
                    showUserInfoData(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.updateAvatarData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ImageUtils.instance.loadImage(activity!!,mCivAvatar,mUserInfo?.headpic,R.mipmap.ic_avatar)
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("头像修改成功")
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        accountVM.accountInfoData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    mTvAccountBalance.setNumberString(AmountUtil.addCommaDots(it.value.data?.totalBalance).toString())
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildVM.myGuildInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (TextUtils.isEmpty(it.value.data?.guildId)) {
                        JoinGuildActivity.intentStart(activity as AppCompatActivity)
                    } else {
                        if (it.value.data?.memberType == 1) {
                            PresidentGuildActivity.intentStart(activity as AppCompatActivity,it.value.data)
                        } else  if (it.value.data?.memberType == 2) {
                            MyGuildActivity.intentStart(activity as AppCompatActivity,it.value.data)
                        }
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        talentRedEnvelopeVM.waitReceiveCountData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showWaitReceiveCountData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildRedEnvelopeVM.guildRedEnvelopeTipsData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    val isShowIcon = it.value.data?.isShowIcon ?: false
                    if (isShowIcon) {
                        mIvGuildRedEnvelope.visibility = View.VISIBLE
                        ImageUtils.instance.loadImage(activity!!,mIvGuildRedEnvelope,R.mipmap.gif_guild_red_envelope)
                    } else {
                        mIvGuildRedEnvelope.visibility = View.GONE
                    }

                    var isShowPopup = it.value.data?.isShowPopup ?: false
                    if (isShowPopup && !App.get().hasLocation()) {
                        AmapLocationUtil.instance.getLocation(activity!!,this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        shareVM.shareInfoData.observe(viewLifecycleOwner, Observer {
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

    fun showGuildRedEnvelopeTipDialog() {
        if (mGuildRedEnvelopeTipDialog == null) {
            mGuildRedEnvelopeTipDialog = GuildRedEnvelopeTipDialog(activity!!)
            mGuildRedEnvelopeTipDialog?.mOnJoinGuildListener = this
        }
        mGuildRedEnvelopeTipDialog?.show()
    }

    fun sendLogOutRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        authVM.logOut(token)
    }

    fun sendUserInfoRequest () {
        if (!App.get().hasLogin()) return

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.fetchUserInfo(token)
    }

    fun sendMyGuildInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        guildVM.fetchMyGuildInfo(token)
    }

    fun sendAccountInfoRequest () {
        if (!App.get().hasLogin()) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        accountVM.fetchAccountInfo(token)
    }

    fun sendWaitReceiveCountRequest () {
        if (!App.get().hasLogin()) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentRedEnvelopeVM.fetchWaitReceiveCount(token)
    }

    fun sendGuildRedEnvelopeTipsRequest () {
        Loger.e(TAG,"sendGuildRedEnvelopeTipsRequest()......")
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(App.get().getUserInfo()?.username)) {
            Loger.e(TAG,"sendGuildRedEnvelopeTipsRequest()......username is empty")
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        guildRedEnvelopeVM.fetchGuildRedEnvelopeTips(token)
    }

    fun sendShareInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ShareInfoParm()
        body.intentType = 0
        body.type = 3

        shareVM.fetchShareInfo(token,body)
    }

    fun shareAction () {
        mLoadingDialog?.show()
        ImageUtils.instance.getBitmap(activity!!,mShareData?.imageUrl,this)
    }

    fun showUserInfoData (data: UserInfo?) {
        ImageUtils.instance.loadImage(activity!!,mCivAvatar,data?.headpic,R.mipmap.ic_avatar)

        if (data?.sex == 0) {
            mIvSex.setImageResource(R.mipmap.ic_mine_female)
        } else if (data?.sex == 1) {
            mIvSex.setImageResource(R.mipmap.ic_mine_male)
        } else {
            mIvSex.setImageResource(0)
        }
        mTvUserName.text = data?.username ?: ""
        mTvUserId.text = "ID:${data?.userId ?: ""}"

        mTvTalentCreditScore.text = "信用分: ${data?.talentCreditScore}"
        mTvEmployerCreditScore.text = "信用分: ${data?.employerCreditScore}"
    }

    fun showWaitReceiveCountData (data: WaitReceiveCountReq) {
        //刷新首页我的红点
        LiveDataBus.send(MineActions.REFRESH_WAIT_RECEIVE_COUNT,data.data)

        var talentRedPacketCount = data.data?.talentRedPacketCount ?: 0
        var employmentRewardCount = data.data?.employmentRewardCount ?: 0

        if (talentRedPacketCount > 0) {
            mViewRedEnvelopeTip.visibility = View.VISIBLE
        } else {
            mViewRedEnvelopeTip.visibility = View.GONE
        }
        if (employmentRewardCount > 0) {
            mViewEmploymentRewardTip.visibility = View.VISIBLE
        } else {
            mViewEmploymentRewardTip.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mShareController?.onActivityResult(requestCode, resultCode, data)
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        if (!App.get().hasLogin()) {
            mSrlRefresh.isRefreshing = false
            ToastUtils.show("请先登录")
            return
        }
        sendUserInfoRequest()
        sendAccountInfoRequest()
        sendWaitReceiveCountRequest()
        sendGuildRedEnvelopeTipsRequest()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mCivAvatar -> {
                if (!App.get().hasLogin()) {
                    ToastUtils.show("请先登录")
                    return
                }
                EditProfileActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mIvEditProfile -> {
                if (!App.get().hasLogin()) {
                    ToastUtils.show("请先登录")
                    return
                }
                EditProfileActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mIvShare -> {
                getShareDialog()?.show()
            }
            R.id.mCvWallet -> {
                if (!App.get().hasLogin()) {
                    ToastUtils.show("请先登录")
                    return
                }
                WalletActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvRedEnvelope -> {
                RedEnvelopeActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, MineEvent.view_red_envelope)
            }
            R.id.mTvMyGuild -> {
                sendMyGuildInfoRequest()
            }
            R.id.mCvEmploymentReward -> {
                EmploymentRewardActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, MineEvent.view_employment_reward)
            }
            R.id.mCvCoupon -> {
                CouponActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvCustomerService -> {
                CustomerServiceActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvViolationNotice -> {
                ViolationNoticeActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, MineEvent.view_violation_notice)
            }
            R.id.mTvSetting -> {
                SettingActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mCvAbout -> {
                AboutActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mCvLogOut -> {
                if (App.get().hasLogin()) {
                    sendLogOutRequest()
                    return
                }
                LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
            }
        }
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

    override fun OnLocation(location: AMapLocation?,errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......${location?.city}")

        //手动选择不更新app中的城市
        if (!App.get().isSelectCity) {
            App.get().setCity(location?.city)
        }

        //只有定位城市是是深圳并且没有提示过才展示公会红包弹窗
        if (AmapLocationUtil.instance.mLocation != null) {
            val locationCity = AmapLocationUtil.instance.mLocation?.city
            if (TextUtils.equals("深圳市",locationCity)) {
                val userId = App.get().getUserInfo()?.userId
                val needShowGuildRedEnvelopeTip = ConfigLocal.needShowGuildRedEnvelopeGuide(userId)
                //如果没有设置闪工名则不弹出加入工会领红包的弹窗
                if (TextUtils.isEmpty(App.get().getUserInfo()?.username)) return

                if (needShowGuildRedEnvelopeTip) {
                    showGuildRedEnvelopeTipDialog()
                    ConfigLocal.updateShowGuildRedEnvelopeGuide(userId,false)
                }
            }
        }
    }

    fun getShareDialog (): ShareDialog? {
        if (mShareDialog == null) {
            mShareDialog = ShareDialog(activity!!)
            mShareDialog?.mOnShareListener = this
        }
        return mShareDialog
    }

    override fun OnJoinGuild() {
        NavigationUtils.goJoinGuildActivity(activity as AppCompatActivity)
    }

    override fun getAttachActivity(): AppCompatActivity? {
        return activity as AppCompatActivity
    }

    override fun onShareSuccess(shareType: Int) {
    }

    override fun onShareCancel(shareType: Int, error: String?) {
        ToastUtils.show(error)
    }

    override fun onShareError(shareType: Int, error: String?) {
        ToastUtils.show(error)
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

    override fun onDestroyView() {
        super.onDestroyView()
        OssUploadModule.instance.onDestroy()
        AmapLocationUtil.instance.removeLocationListener(this)
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

        mShareController?.invokeShare(activity, mShareType)
    }

}