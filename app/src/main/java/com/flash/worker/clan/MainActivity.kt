package com.flash.worker.clan

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.LoginEvent
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.module.SecurityCheckModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.provider.LoginService
import com.flash.worker.lib.common.push.JPushOpenHelper
import com.flash.worker.lib.common.push.TagAliasOperatorHelper
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.common.view.adapter.ViewPagerAdapter
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.LoginData
import com.flash.worker.lib.coremodel.data.bean.WaitReceiveCountData
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.LatestVersionReq
import com.flash.worker.lib.coremodel.data.req.LoginReq
import com.flash.worker.lib.coremodel.util.*
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.livedatabus.action.*
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.login.interfaces.OnOneKeyLoginListener
import com.flash.worker.module.login.module.OneKeyLoginHelper
import com.flash.worker.module.login.module.WxLoginHelper
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.netease.nimlib.sdk.auth.LoginInfo
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Route(path = ARouterPath.MainAct)
class MainActivity : BaseActivity(),BottomNavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnPageChangeListener,OnOneKeyLoginListener,View.OnClickListener,
        OnAmySportsVoucherReceiveListener, OnTalentReleaseListener,OnEmployerReleaseListener,
    OnNameSettingListener {

    private val authVM: AuthVM by viewModels {
        InjectorUtils.provideAuthVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val updateVM: UpdateVM by viewModels {
        InjectorUtils.provideUpdateVMFactory(this)
    }

    private val talentRedEnvelopeVM: TalentRedEnvelopeVM by viewModels {
        InjectorUtils.provideTalentRedEnvelopeVMFactory(this)
    }

    private val systemNoticeVM: SystemNoticeVM by viewModels {
        InjectorUtils.provideSystemNoticeVMFactory(this)
    }

    private val userCouponVM: UserCouponVM by viewModels {
        InjectorUtils.provideUserCouponVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mOneKeyLoginHelper: OneKeyLoginHelper? = null
    var mWxLoginHelper: WxLoginHelper? = null
    var mViewPagerAdapter: ViewPagerAdapter? = null
    var fragmentList: MutableList<BaseFragment> = ArrayList()
    var navTitles: Array<String> ? = null
    var mGPSNotOpenDlg: CommonTipDialog? = null
    var mAmySportsVoucherReceiveDialog: AmySportsVoucherReceiveDialog? = null
    var mEmployerReleaseDialog: EmployerReleaseDialog? = null
    var mTalentReleaseDialog: TalentReleaseDialog? = null
    var mNameSettingDialog: NameSettingDialog? = null
    var settingUserName: String? = null
    var settingInviteId: String? = null

    var mExitTime: Long = 0

    var systemNoticeUnReadNum: Int = 0

    var locationCity: String? = null

    var pushData: String? = null
    var pagePosition: Int = 0

    @Autowired(name = ARouterPath.loginService)
    @JvmField
    var loginService: LoginService? = null

    companion object {
        fun intentStart (activity: AppCompatActivity,pagerPosition: Int,pushData: String? = null) {
            var intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(Constant.POSITION_KEY,pagerPosition)
            intent.putExtra(Constant.INTENT_DATA_KEY,pushData)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initPushData(intent)

        sendUserInfoRequest(false)
        sendLatestVersionRequest()
        sendUnreadNumRequest()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(this)
        mWxLoginHelper = WxLoginHelper(this)
        mOneKeyLoginHelper = OneKeyLoginHelper(this,this)

        //这里必须判断网络，否则阿里的认证会crash
        if (NetworkUtils.isNetworkAvailable(this)) {
            mOneKeyLoginHelper?.initSdk()
        }

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val hireFrag: BaseFragment = ARouter.getInstance().build(ARouterPath.HireFgt).navigation() as BaseFragment
        val jobFrag: BaseFragment = ARouter.getInstance().build(ARouterPath.JobFgt).navigation() as BaseFragment
        val businessFrag: BaseFragment = ARouter.getInstance().build(ARouterPath.BusinessFgt).navigation() as BaseFragment
        val messageFrag: BaseFragment = ARouter.getInstance().build(ARouterPath.MessageFgt).navigation() as BaseFragment
        val mineFrag: BaseFragment = ARouter.getInstance().build(ARouterPath.MineFgt).navigation() as BaseFragment

        fragmentList.add(hireFrag)
        fragmentList.add(jobFrag)
        fragmentList.add(businessFrag)
        fragmentList.add(messageFrag)
        fragmentList.add(mineFrag)

        mViewPagerAdapter?.fragmetList = fragmentList
        mVpHome.adapter = mViewPagerAdapter

        mBottomNav.setOnNavigationItemSelectedListener(this)

        mVpHome.addOnPageChangeListener(this)

        mTvNetworkStatus.setOnClickListener(this)
        mTvChangeCity.setOnClickListener(this)
        mIvCloseChangeCity.setOnClickListener(this)
        mIvNewRelease.setOnClickListener(this)

        handleNetworkChanges()

        subscribeUi()
        subscribeEvent()
    }

    fun initData () {
        SecurityCheckModule.isDeviceSecurity.observe(this, Observer {isDeviceSecurity ->
            Loger.e(TAG,"initialize-isDeviceSecurity = $isDeviceSecurity")
            if (!isDeviceSecurity) {
                ToastUtils.show("检测到您的设备违规,将限制您的所有功能使用!")
                finish()
            }
        })

        setJPushAlias()
        navTitles = ResUtils.getStringArrayRes(R.array.nav_title)

        refreshUnreadCount()
        sendWaitReceiveCountRequest()
    }

    fun initPushData (intent: Intent?) {
        pushData = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        pagePosition = intent?.getIntExtra(Constant.POSITION_KEY,0) ?: 0

        Loger.e(TAG,"initPushData-pagePosition = $pagePosition")
        mVpHome.setCurrentItem(pagePosition,false)
        parseIntentAction()
    }

    fun parseIntentAction() {
        //处理极光推送跳转
        JPushOpenHelper.parseIntentAction(this,pushData)
        Loger.e(TAG,"initPushData-pushData = $pushData")
    }

    /**
     * 设置极光推送别名必须延时设置否则会设置失败
     */
    fun setJPushAlias() {
        TagAliasOperatorHelper.instance.setJPushAlias()
    }

    fun subscribeEvent() {
        LiveDataBus.with(LoginActions.GO_WECHAT_LOGIN)
            .observeForever(this, Observer {
                mNameSettingDialog?.clearInput()
                if (App.get().hasLogin()) return@Observer
                App.get().resetLoginData()
                mWxLoginHelper?.wxLogin()
            })

        LiveDataBus.with(LoginActions.GO_ONE_KEY_LOGIN)
            .observeForever(this, Observer {
                mNameSettingDialog?.clearInput()
                if (App.get().hasLogin()) return@Observer
                mLoadingDialog?.show()
                App.get().resetLoginData()
                mOneKeyLoginHelper?.oneKeyLogin()
            })

        LiveDataBus.with(LoginActions.TOKEN_INVALID)
            .observeForever(this, Observer {
                mNameSettingDialog?.clearInput()
                mNameSettingDialog?.dismiss()

                mLoadingDialog?.show()
                App.get().resetLoginData()
                TagAliasOperatorHelper.instance.deleteAlias()
                mOneKeyLoginHelper?.oneKeyLogin()
            })

        LiveDataBus.with(IMActions.NEW_MESSAGE)
                .observe(this, Observer {
                    refreshUnreadCount()
                })

        LiveDataBus.with(MineActions.REFRESH_WAIT_RECEIVE_COUNT)
                .observe(this, Observer {
                    refreshWaitReceiveCount(it as WaitReceiveCountData)
                })


        LiveDataBus.with(Constant.Msg.LOGIN_AUTH_WEIXIN)
            .observeForever(this, Observer {
                if (it is SendAuth.Resp) {
                    sendWechatLoginRequest(it.code)
                }
            })

        LiveDataBus.with(LoginActions.LOGIN_SUCCESS)
            .observeForever(this, Observer {
                mNameSettingDialog?.clearInput()
                loginNim()
                showNameSettingDlg()
            })

        LiveDataBus.with(HomeActions.SET_USER_NAME_SUCCESS)
            .observeForever(this, Observer {
                mNameSettingDialog?.dismiss()
            })

        LiveDataBus.with(Constant.Msg.WX_AUTH_ERROR)
            .observe(this, Observer {
                ToastUtils.show("拒绝授权微信登录")
            })

        LiveDataBus.with(IMActions.REFRESH_UNREAD_COUNT)
                .observe(this, Observer {
                    refreshUnreadCount()
                })

        LiveDataBus.with(IMActions.SYSTEM_NOTICE_UNREAD_COUNT)
                .observe(this, Observer {
                    systemNoticeUnReadNum = it as Int
                    refreshUnreadCount()
                })

        LiveDataBus.with(MineActions.VIEW_HIRE)
                .observe(this, Observer {
                    MainHandler.get().postDelayed(Runnable {
                        mVpHome.setCurrentItem(0,false)//切换到人才页面
                    },200)
                })

        LiveDataBus.with(HomeActions.LOCATION_SUCCESSED)
                .observe(this, Observer {
                    locationCity = it.toString()
                    setChangeCityViewVisibility(View.VISIBLE)
                    mTvCity.text = "定位显示您在“$locationCity”"
                })

        LiveDataBus.with(HomeActions.GPS_NOT_OPEN)
                .observe(this, Observer {
                    showGPSNotOpenDlg()
                })

        LiveDataBus.with(HomeActions.SHOW_AMY_SPORT_VOUCHER_RECEIVE_DLG)
                .observe(this, Observer {
                    sendCheckReceiveCouponRequest()
                })

    }

    fun subscribeUi() {
        authVM.oneKeyLoginData.observeForever(Observer{
            if (isFinishing) return@Observer

            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setLoginReq(it.value)
                    ToastUtils.show("登录成功")
                    App.get().resetLocation()
                    SharedPreferencesUtils.setJPushAlias = false

                    loginNim()

                    sendUserInfoRequest(true)

                    UMengEventModule.report(this,LoginEvent.one_key_login)
                }
                is HttpResult.Error -> {
                    if(TextUtils.equals("001065",it.code)) {
                        NavigationUtils.goAccountFrozenActivity(this,it.message)
                        return@Observer
                    }
                    if(TextUtils.equals("001080",it.code)) {
                        NavigationUtils.goAccountCancelledActivity(this,it.message)
                        return@Observer
                    }

                    ToastUtils.show(it.message)
                }
            }
        })

        authVM.wechatLoginData.observeForever(Observer {
            if (isFinishing) return@Observer
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (!TextUtils.isEmpty(it.value.data?.openId)) {
                        NavigationUtils.goBindPhoneActivity(this,it.value.data?.openId)
                        return@Observer
                    }
                    var loginReq = LoginReq()
                    var loginData = LoginData()
                    loginData.token = it.value.data?.token
                    loginData.userId = it.value.data?.userId
                    loginReq.data = loginData
                    App.get().setLoginReq(loginReq)

                    ToastUtils.show("登录成功")
                    App.get().resetLocation()
                    SharedPreferencesUtils.setJPushAlias = false

                    loginNim()
                    sendUserInfoRequest(true)

                    UMengEventModule.report(this,LoginEvent.weichat_login)
                }
                is HttpResult.Error -> {
                    if(TextUtils.equals("001065",it.code)) {
                        NavigationUtils.goAccountFrozenActivity(this,it.message)
                        return@Observer
                    }
                    if(TextUtils.equals("001080",it.code)) {
                        NavigationUtils.goAccountCancelledActivity(this,it.message)
                        return@Observer
                    }

                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.imLoginInfoData.observeForever {
            when(it) {
                is HttpResult.Success -> {
                    var loginInfo = LoginInfo(it.value.data?.imAccid,it.value.data?.imToken)
                    NimMessageManager.instance.login(loginInfo)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        }

        userVM.userInfoData.observeForever {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(it.value.data)
                    setJPushAlias()
                    showNameSettingDlg()
                    LiveDataBus.send(TalentActions.REFRESH_TALENT_DETAIL)
                    LiveDataBus.send(JobActions.REFRESH_JOB_DETAIL)
                    LiveDataBus.send(JobActions.REFRESH_GUILD_DETAIL)
                    LiveDataBus.send(TaskActions.REFRESH_TASK_LIST)
                    LiveDataBus.send(TalentActions.CHECK_GUILD_RED_ENVELOPE)
                    LiveDataBus.send(MineActions.REFRESH_LOGIN_STATUS,true)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        }

        updateVM.latestVersionData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showLatestVersionData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        talentRedEnvelopeVM.waitReceiveCountData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    refreshWaitReceiveCount(it.value.data)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        systemNoticeVM.unReadNumData.observe(this, Observer {
            when(it) {
                is HttpResult.Success -> {
                    systemNoticeUnReadNum = it.value.data?.unreadNum ?: 0
                    refreshUnreadCount()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userCouponVM.checkReceiveCouponData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        showAmySportVoucherReceiveDlg()
                    } else {
                        showReceiveAmySportVoucherFailDlg()
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userCouponVM.receiveCouponData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showReceiveAmySportVoucherSuccessDlg()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("155305",it.code)) {
                        showReceiveAmySportVoucherFailDlg()
                        return@Observer
                    }
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
                    userInfo?.inviterUserId = settingInviteId
                    App.get().setUserInfo(userInfo)

                    LiveDataBus.send(MineActions.REFRESH_USER_INFO)
                    LiveDataBus.send(TalentActions.CHECK_GUILD_RED_ENVELOPE)
                }
                is HttpResult.Error -> {
                    showNameSettingDlg()
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendWaitReceiveCountRequest () {
        if (!App.get().hasLogin()) return

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentRedEnvelopeVM.fetchWaitReceiveCount(token)
    }

    fun sendUnreadNumRequest () {
        if (!App.get().hasLogin()) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        systemNoticeVM.fetchUnreadNum(token)
    }

    fun sendLatestVersionRequest () {
        var body = LatestVersionParm()
        body.versionCode = AppUtil.getAppVersionCode(this)

        updateVM.fetchLatestVersion(body)
    }

    fun sendCheckReceiveCouponRequest () {
        if (!App.get().hasLogin()) {
            mLoadingDialog?.show()
            App.get().resetLoginData()
            mOneKeyLoginHelper?.oneKeyLogin()
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CheckReceiveCouponParm()
        body.type = 10

        userCouponVM.checkReceiveCoupon(token,body)
    }

    fun sendReceiveCouponRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        var body = ReceiveCouponParm()
        body.type = 10

        userCouponVM.receiveCoupon(token,body)
    }

    fun sendUpdateUserInfoRequest (username: String?,inviterUserId: String?) {
        settingUserName = username
        settingInviteId = inviterUserId
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

    fun showLatestVersionData (data: LatestVersionReq) {
        var isUpdate = data?.data?.isUpdate ?: false
        if (isUpdate) {
            var appUpdateDialog = AppUpdateDialog(this)
            appUpdateDialog.mLatestVersionData = data?.data

            appUpdateDialog.show()
        }
    }

    fun refreshUnreadCount() {
        var unreadCount: Int = NimMessageManager.instance.getUnreadCount()
        unreadCount += systemNoticeUnReadNum
        setBadgeCount(R.id.navigation_message, unreadCount)
    }

    fun refreshWaitReceiveCount(data: WaitReceiveCountData?) {
        var talentRedPacketCount = data?.talentRedPacketCount ?: 0
        var employmentRewardCount = data?.employmentRewardCount ?: 0
        setBadgeCount(R.id.navigation_mine, talentRedPacketCount.coerceAtLeast(employmentRewardCount))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Loger.e(TAG,"onNavigationItemSelected......")
        when (item.itemId) {
            R.id.navigation_hire -> {
                mVpHome.setCurrentItem(0,false)
                return true
            }
            R.id.navigation_job -> {
                mVpHome.setCurrentItem(1,false)
                return true
            }
            R.id.navigation_business -> {
                if (notLogin()) return true
                mVpHome.setCurrentItem(2,false)
                return true
            }
            R.id.navigation_message -> {
                if (notLogin()) return true
                mVpHome.setCurrentItem(3,false)
                return true
            }
            R.id.navigation_mine -> {
                if (notLogin()) return true
                mVpHome.setCurrentItem(4,false)
                return true
            }
        }
        return false
    }

    fun notLogin (): Boolean {
        Loger.e(TAG,"notLogin()......")
        if (!App.get().hasLogin()) {
            mBottomNav.post {
                mLoadingDialog?.show()
                mBottomNav.selectedItemId = mBottomNav.menu.getItem(0).itemId
                App.get().resetLoginData()
                mOneKeyLoginHelper?.oneKeyLogin()
            }
            return true
        }
        return false
    }

    fun setBadgeCount(menuItemId: Int, count: Int) {
        if (isFinishing) return
        if (!App.get().hasLogin()) return
        Loger.e(TAG,"setBadgeCount-count = $count")
        mBottomNav.getOrCreateBadge(menuItemId) // Show badge
        //        navView.removeBadge(R.id.navigation_notifications) // Remove badge
        val badgeDrawable: BadgeDrawable? = mBottomNav.getBadge(menuItemId) // Get badge
        if (badgeDrawable != null) {
            if (count == 0) {
                mBottomNav.removeBadge(menuItemId)
                return
            }
            badgeDrawable.badgeGravity = BadgeDrawable.TOP_END
            badgeDrawable.backgroundColor = Color.RED
            badgeDrawable.badgeTextColor = Color.WHITE
            badgeDrawable.horizontalOffset = -12
            badgeDrawable.verticalOffset = 12
            badgeDrawable.maxCharacterCount = 3

            if (menuItemId == R.id.navigation_message) {
                badgeDrawable.number = count
            } else {
                badgeDrawable.clearNumber()//变成小圆点
            }
        }
    }

    fun loginNim () {
        Loger.e(TAG,"loginNim()......")
        if (!NimMessageManager.instance.hasLogin()) {
            var loginData = App.get().getLoginReq()?.data
            sendImLoginInfoRequest(loginData?.userId)
        }
    }

    /**
     * Observe network changes i.e. Internet Connectivity
     */
    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(
            this, Observer { isConnected ->
                if (!isConnected) {
                    mTvNetworkStatus.text = ResUtils.getStringRes(R.string.network_error)
                    mTvNetworkStatus.apply {
                        show()
                        setBackgroundColor(ResUtils.getColorRes(R.color.color_D32F2F))
                        TextViewBoundsUtil.setTvDrawableRight(this@MainActivity,this,R.mipmap.ic_right)
                    }
                } else {
                    loginNim()
                    mOneKeyLoginHelper?.initSdk()

                    mTvNetworkStatus.text = ResUtils.getStringRes(R.string.connectivity)
                    mTvNetworkStatus.apply {
                        setBackgroundColor(ResUtils.getColorRes(R.color.color_43A047))
                        TextViewBoundsUtil.setTvDrawableRight(this@MainActivity,this,0)

                        animate()
                            .alpha(1f)
                            .setStartDelay(1000)
                            .setDuration(1000)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    hide()
                                }
                            })
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mOneKeyLoginHelper?.onActivityResult(requestCode,resultCode,data)
    }

    override fun OnOneKeyLogin(success: Boolean, message: String?) {
        Loger.e(TAG,"OnOneKeyLogin......")
        mLoadingDialog?.dismiss()
        if (!success) {
            ToastUtils.show(message)
            NavigationUtils.goCodeLoginActivity(this,false)
            return
        }
        sendOneKeyLoginRequest(message)
    }

    fun sendOneKeyLoginRequest (accessToken: String?) {
        if (TextUtils.isEmpty(accessToken)) {
            return
        }
        mLoadingDialog?.show()

        var body = OneKeyLoginParm()
        body.accessToken = accessToken

        authVM.oneKeyLogin(body)
    }

    fun sendWechatLoginRequest (code: String?) {
        mLoadingDialog?.show()

        var body = WechatLoginParm()
        body.code = code

        authVM.wechatLogin(body)
    }

    fun sendImLoginInfoRequest (userId: String?) {
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(userId)) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun sendUserInfoRequest (showLoading: Boolean) {
        if (!App.get().hasLogin()) return

        if (showLoading) {
            mLoadingDialog?.show()
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.fetchUserInfo(token)
    }

    /*override fun onBackPressed() {
//        super.onBackPressed()//这句话一定要注掉,不然会调用默认的back处理方式了
        //让返回键和home键一样效果不退出app
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }*/

    override fun onBackPressed() {
        var currentItem = mVpHome?.currentItem ?: 0
        var fragHandleBackEvent = fragmentList?.get(currentItem).handleBackEvent()
        if (fragHandleBackEvent) {
            return
        }
        if (System.currentTimeMillis() - mExitTime < 2000) {
            App.get().finishAllActivity()
            super.onBackPressed()
        } else {
            mExitTime = System.currentTimeMillis()
            ToastUtils.show("再按一次退出")
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        Loger.e(TAG,"onPageSelected......position = $position")
        mBottomNav.selectedItemId = mBottomNav.menu.getItem(position).itemId
        if (position > 1) {
            setChangeCityViewVisibility(View.GONE)
            mIvNewRelease.visibility = View.GONE
        } else {
            mIvNewRelease.visibility = View.VISIBLE
        }
    }

    fun setChangeCityViewVisibility(visibility: Int) {
        mLlChangeCity.visibility = visibility
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvNetworkStatus -> {
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            R.id.mTvChangeCity -> {
                App.get().setCity(locationCity)
                setChangeCityViewVisibility(View.GONE)
                LiveDataBus.send(HireActions.HIRE_CITY_CHANGED)
                LiveDataBus.send(JobActions.JOB_CITY_CHANGED)
            }
            R.id.mIvCloseChangeCity -> {
                App.get().hasShowChangeCity = true
                setChangeCityViewVisibility(View.GONE)
            }
            R.id.mIvNewRelease -> {
                showHomeNewReleaseDlg()
            }
        }
    }

    fun showGPSNotOpenDlg () {
        if (mVpHome.currentItem > 1) return
        if (App.get().hasShowGPSNotOpenTip) return
        App.get().hasShowGPSNotOpenTip = true
        if (mGPSNotOpenDlg == null) {
            mGPSNotOpenDlg = CommonTipDialog(this)
        }
        mGPSNotOpenDlg?.mTitle = "温馨提示"
        mGPSNotOpenDlg?.mContent = "定位服务未开启，确定要前往设置？"
        mGPSNotOpenDlg?.mCancelText = "取消"
        mGPSNotOpenDlg?.mOkText = "设置"
        mGPSNotOpenDlg?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                AmapLocationUtil.instance.openGpsService(this@MainActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mGPSNotOpenDlg?.show()
    }

    fun showAmySportVoucherReceiveDlg () {
        if (mAmySportsVoucherReceiveDialog == null) {
            mAmySportsVoucherReceiveDialog = AmySportsVoucherReceiveDialog(this)
            mAmySportsVoucherReceiveDialog?.mOnAmySportsVoucherReceiveListener = this
        }
        mAmySportsVoucherReceiveDialog?.show()
    }

    fun showReceiveAmySportVoucherSuccessDlg () {
        val mCommonTipDialog = CommonTipDialog(this)
        mCommonTipDialog?.mTitle = "温馨提示"
        mCommonTipDialog?.mContent = "领取成功，请前往卡券包使用"
        mCommonTipDialog?.mCancelText = "取消"
        mCommonTipDialog?.mOkText = "前往使用"
        mCommonTipDialog?.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goCouponActivity(this@MainActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mCommonTipDialog?.show()
    }

    fun showReceiveAmySportVoucherFailDlg () {
        val mCommonTipDialog = CommonTipDialog(this)
        mCommonTipDialog?.mTitle = "温馨提示"
        mCommonTipDialog?.mContent = "已领取，请前往卡券包使用"
        mCommonTipDialog?.mCancelText = "取消"
        mCommonTipDialog?.mOkText = "前往使用"
        mCommonTipDialog?.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goCouponActivity(this@MainActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mCommonTipDialog?.show()
    }

    fun showHomeNewReleaseDlg () {
        var currentPage = mVpHome.currentItem
        when (currentPage) {
            0 -> {
                if (mTalentReleaseDialog == null) {
                    mTalentReleaseDialog = TalentReleaseDialog(this)
                    mTalentReleaseDialog?.mOnTalentReleaseListener = this
                }
                mTalentReleaseDialog?.show()
            }
            1 -> {
                if (mEmployerReleaseDialog == null) {
                    mEmployerReleaseDialog = EmployerReleaseDialog(this)
                    mEmployerReleaseDialog?.mOnEmployerReleaseListener = this
                }
                mEmployerReleaseDialog?.show()
            }
        }
    }

    fun showNameSettingDlg () {
        var username = App.get().getUserInfo()?.username
        Loger.e(TAG,"showNameSettingDlg()......getUserInfo = ${App.get().getUserInfo()}")
        Loger.e(TAG,"showNameSettingDlg()......username = $username")

        if (!TextUtils.isEmpty(username)) {
            mNameSettingDialog?.dismiss()
            return
        }

        if (mNameSettingDialog == null) {
            mNameSettingDialog = NameSettingDialog(this)
            mNameSettingDialog?.mOnNameSettingListener = this
        }

        mNameSettingDialog?.show()
    }

    override fun OnAmySportsVoucherReceive() {
        sendReceiveCouponRequest()
    }

    override fun OnNameSetting(name: String?, inviteId: String?) {
        sendUpdateUserInfoRequest(name,inviteId)
    }

    override fun OnTalentRelease() {
        NavigationUtils.goTalentNewReleaseActivity(this)
    }

    override fun OnNewJobRelease() {
        NavigationUtils.goHireNewReleaseActivity(this)
    }

    override fun OnNewTaskRelease() {
        NavigationUtils.goTaskNewReleaseActivity(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initPushData(intent)
    }

}