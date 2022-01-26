package com.flash.worker.lib.im

import android.content.Context
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.im.interfaces.IMMessageInterface
import com.google.gson.Gson
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.auth.*
import com.netease.nimlib.sdk.mixpush.MixPushService
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.*
import com.netease.nimlib.sdk.uinfo.UserService
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import java.util.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimMessageManager
 * Author: Victor
 * Date: 2020/12/29 14:22
 * Description: 
 * -----------------------------------------------------------------
 */
class NimMessageManager: IMMessageInterface<LoginInfo> {
    val TAG = "NimMessageManager"
    private object Holder { val instance = NimMessageManager() }
    companion object {
        val  instance: NimMessageManager by lazy { Holder.instance }

        const val IS_FIRED_KEY = "IS_FIRED_KEY"

        /**
         * 云信连接重试
         */
        const val NIM_LOGIN_RETRY = 2003
    }

    /**
     * 网易登录信息实体类
     */
    var mNimLoginInfo: LoginInfo? = null

    /**
     * 重试次数
     */
    private val mRetryCount: Long = 0

    /**
     * 云信消息Handler
     */
    private var mNimHandler: NimHandler? = null

    /**
     * 全局消息控制器
     */
    private var mNimMsgController: MessageObserver? = null

    /**
     * 在线状态回调
     */
    private var mOnlineObserver: NimOnlineObserver? = null

    //已读回执回调
    private var mNimMessageReceiptObserver: NimMessageReceiptObserver? = null

    /**
     * 重试时间
     */
    private var mRetryTime: Long = 0

    init {
        mOnlineObserver = NimOnlineObserver()
        mNimMsgController = MessageObserver()
        mNimMessageReceiptObserver = NimMessageReceiptObserver()
    }

    override fun init() {
        // 注册自定义消息附件解析器
        NIMClient.getService(MsgService::class.java)
            .registerCustomAttachmentParser(CustomAttachParser())
        //状态初始化
        //注册在线状态信息
        registerOnlineStatus(true)
        //会话变更
        registRecentContactsObserver(RecentContactObserver(),true)
        //消息变更
        registMessageStatusObserver(MessageStatusObserver(),true)
        //监听的注册,必须在主进程中.
        registerReceiveObservers(true)
        //监听已读回执状态信息
        registReceiptObserver(true)

    }

    /**
     * 注册云信全局消息回调
     * 云信:
     * 该代码的典型场景为消息对话界面，在界面 onCreate 里注册消息接收观察者，
     * 在 onDestroy 中注销观察者。在收到消息时，判断是否是当前聊天对象的消息，
     * 如果是，加入到列表中显示。
     *
     * @param register
     */
    fun registerReceiveObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(mNimMsgController, register)
    }

    fun registerReceiveObservers(
        observer: Observer<List<IMMessage>>?,
        register: Boolean
    ) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(observer, register)
    }

    /**
     * 注册在线状态信息
     *
     * @param register
     */
    fun registerOnlineStatus(register: Boolean) {
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOnlineStatus(mOnlineObserver, register)
    }

    /**
     * 已读回执状态信息
     * @param register
     */
    fun registReceiptObserver(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeMessageReceipt(mNimMessageReceiptObserver, register)
    }

    /**
     * 注册最近联系人状态变化回调
     * @param register
     */
    fun registRecentContactsObserver(
        observer: Observer<List<RecentContact>>?,
        register: Boolean
    ) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeRecentContact(observer, register)
    }

    /**
     * 注册消息状态变化回调
     * @param observer
     * @param register
     */
    fun registMessageStatusObserver(
        observer: Observer<IMMessage>?,
        register: Boolean
    ) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeMsgStatus(observer, register)
    }

    /**
     * 注册用户状态回调
     * @param observer
     * @param register
     */
    fun registUserStatusObserver(
        observer: Observer<StatusCode>?,
        register: Boolean
    ) {
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOnlineStatus(observer, register)
    }

    fun observeCustomNotification(
        observer: Observer<CustomNotification>?,
        register: Boolean
    ) {
        // 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeCustomNotification(observer, register)
    }

    /**
     * 注册在先状态回调
     * @param observer
     * @param register
     */
    fun registOnlineStatusObserver(
        observer: Observer<List<OnlineClient>>?,
        register: Boolean
    ) {
        NIMClient.getService(AuthServiceObserver::class.java)
            .observeOtherClients(observer, register)
    }

    /**
     * 踢出其他登录端
     * @param client
     */
    fun kickOtherClient(client: OnlineClient) {
        NIMClient.getService(AuthService::class.java).kickOtherClient(client)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    // 踢出其他端成功
                    // 如果双发都是aos，踢掉其他端之后，服务端下发状态不带多端信息，因此这里再次发布一次
                    if (client.clientType == ClientType.Android) {
//                        MainHandler.get().postDelayed(Runnable {
//                            OnlineStateEventManager.publishOnlineStateEvent(true)
//                        }, 2500)
                    }
                }

                override fun onFailed(code: Int) {
                    // 踢出其他端失败，返回失败code
                }

                override fun onException(exception: Throwable) {
                    // 踢出其他端错误
                }
            })
    }

    override fun login(info: LoginInfo?) {
        mNimLoginInfo = info
        //验证登录信息
        if (info == null) {
            Log.e(TAG, "LoginInfo is null")
        } else if (hasLogin()) {
            Log.d(TAG, "HasLogin = true " + info.getAccount())
        } else {
            Log.d(TAG, "Login Start...parm = " + Gson().toJson(info))
            NIMClient.getService(AuthService::class.java).login(info)
                .setCallback(NimLoginCallBack())
        }
    }

    override fun retryLogin() {
        val temp = System.currentTimeMillis()
        if (temp - mRetryTime >= 15 * 1000) {
            mRetryTime = temp
            login(mNimLoginInfo)
            Log.e(TAG, "Nim Retry Login...")
        }
    }

    override fun logout() {
        Log.i(TAG, "NIM Logout...")
        mNimLoginInfo = null
        getNimHandler()?.removeCallbacksAndMessages(null)
        NIMClient.getService(AuthService::class.java).logout()
    }

    override fun hasLogin(): Boolean {
        Log.e(TAG, "hasLogin......NIMClient.getStatus() = ${NIMClient.getStatus()}")
        return NIMClient.getStatus() == StatusCode.LOGINED
    }

    fun getNimHandler(): NimHandler? {
        if (mNimHandler == null) {
            mNimHandler = NimHandler()
        }
        return mNimHandler
    }

    /**
     * 获取延迟次数
     *
     * @return
     */
    fun getRetryDelay(): Int {
        return if (mRetryCount < 10) {
            3000
        } else if (mRetryCount >= 10 && mRetryCount < 20) {
            5000
        } else {
            10000
        }
    }

    /**
     * 延迟重新登录
     */
    fun loginRetryDelay() {
        getNimHandler()?.removeMessages(NIM_LOGIN_RETRY)
        getNimHandler()?.sendEmptyMessageDelayed(
            NIM_LOGIN_RETRY,
            getRetryDelay().toLong()
        )
    }

    class NimHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                NIM_LOGIN_RETRY -> instance.retryLogin()
            }
        }
    }

    /**
     * @param message
     * 发送消息
     */
    fun sendMessage(message: IMMessage?,reSend:Boolean = false) {
        Log.e(TAG, "sendMessage-hasLogin() = " + hasLogin())
        if (!hasLogin()) {
            retryLogin()
        }
        Log.e(TAG,"sendMessage-message = " + Gson().toJson(message))
        if (message?.config != null) {
            Log.e(TAG,"sendMessage-message.getConfig = " + Gson().toJson(message?.config))
        }
        // 发送给对方
        NIMClient.getService(MsgService::class.java).sendMessage(message, reSend)

    }

    /**
     * 发送已读回执
     */
    fun sendIsRemoteRead(message: IMMessage?, nimMessage: IMMessage) {
        Log.e(TAG,"sendIsRemoteRead-message = " + Gson().toJson(message))
        if (!hasLogin()) {
            retryLogin()
            return
        }
        /**
         * 发送消息已读回执
         * @param sessionId 会话ID（聊天对象账号）
         * @param message 已读的消息(一般是当前接收的最后一条消息）
         */
        // 以单聊类型为例
        NIMClient.getService(MsgService::class.java).sendMessageReceipt(nimMessage.fromAccount, message)
    }

    fun sendIsRemoteRead(message: IMMessage?, account: String?) {
        Log.e(TAG,"sendIsRemoteRead-message = " + Gson().toJson(message))
        if (!hasLogin()) {
            retryLogin()
            return
        }
        /**
         * 发送消息已读回执
         * @param sessionId 会话ID（聊天对象账号）
         * @param message 已读的消息(一般是当前接收的最后一条消息）
         */
        // 以单聊类型为例
        NIMClient.getService(MsgService::class.java).sendMessageReceipt(account, message)
    }

    fun sendTipMessage(message: IMMessage?, local: Boolean) {
        Log.e(TAG, "sendTipMessage-hasLogin() = " + hasLogin())
        if (!hasLogin()) {
            retryLogin()
            return
        }
        Log.e(TAG,"sendTipMessage-message = " + Gson().toJson(message))
        // 保存消息到本地数据库，但不发送到服务器
        if (local) {
            NIMClient.getService(MsgService::class.java).saveMessageToLocal(message, true)
        } else {
            NIMClient.getService(MsgService::class.java).sendMessage(message, false)
        }

        //自己解锁别人不需要自己的提示消息
        NIMClient.getService(MsgService::class.java).deleteChattingHistory(message)
    }

    fun getUnreadCount(): Int {
        if (!hasLogin()) {
            retryLogin()
            return 0
        }
        val unReadCount = NIMClient.getService(MsgService::class.java).totalUnreadCount
        Log.e(TAG, "getUnreadCount-unreadCount = $unReadCount")
        return unReadCount
    }

    /**
     * 获取最近联系人数据
     * @param callbackWrapper
     */
    fun getRecentContact(callbackWrapper: RequestCallbackWrapper<List<RecentContact>>) {
        NIMClient.getService(MsgService::class.java).queryRecentContacts().setCallback(callbackWrapper)
    }

    /**
     * 从最近联系人列表中删除一项 删除会话，删除后，消息历史被一起删除
     * @param recent
     */
    fun deleteRecentContact(recent: RecentContact?) {
        NIMClient.getService(MsgService::class.java).deleteRecentContact(recent)
        NIMClient.getService(MsgService::class.java)
                .clearChattingHistory(recent?.contactId, recent?.sessionType)
    }

    fun updateRecentContact (recent: RecentContact) {
        NIMClient.getService(MsgService::class.java).updateRecent(recent)
    }

    /**
     * 判断会话是否置顶
     */
    fun isStickTopSession(recent: RecentContact?): Boolean {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        val team = TeamNotificationUtil.getTeam(recent?.contactId)
        if (team != null) {
            sessionType = SessionTypeEnum.Team
        }
        val msgService = NIMClient.getService(MsgService::class.java)
        val isStickTopSession = msgService.isStickTopSession(recent?.contactId, sessionType)
        Log.e(TAG, "isStickTopSession()......isStickTopSession = $isStickTopSession")
        return isStickTopSession
    }

    /**
     * 添加一个置顶会话
     */
    fun addStickTopSession(recent: RecentContact?,callback:RequestCallbackWrapper<StickTopSessionInfo>) {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        val team = TeamNotificationUtil.getTeam(recent?.contactId)
        if (team != null) {
            sessionType = SessionTypeEnum.Team
        }
        val msgService = NIMClient.getService(MsgService::class.java)
        msgService.addStickTopSession(recent?.contactId, sessionType, "").setCallback(callback)
    }

    /**
     * 删除一个置顶会话
     */
    fun removeStickTopSession(recent: RecentContact?,callback:RequestCallbackWrapper<Void>) {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        val team = TeamNotificationUtil.getTeam(recent?.contactId)
        if (team != null) {
            sessionType = SessionTypeEnum.Team
        }
        val msgService = NIMClient.getService(MsgService::class.java)
        msgService.removeStickTopSession(recent?.contactId, sessionType, "").setCallback(callback)
    }

    fun removeStickTopSession(recent: RecentContact?) {
        // 以单聊类型为例
        var sessionType = SessionTypeEnum.P2P
        val team = TeamNotificationUtil.getTeam(recent?.contactId)
        if (team != null) {
            sessionType = SessionTypeEnum.Team
        }
        val msgService = NIMClient.getService(MsgService::class.java)
        msgService.removeStickTopSession(recent?.contactId, sessionType, "")
    }


    /**
     * 获取置顶会话信息的列表
     */
    fun getStickTopSession(): List<StickTopSessionInfo> {
        val sticktopsession = NIMClient.getService(MsgService::class.java).queryStickTopSessionBlock()
        return sticktopsession
    }

    /**
     * 消息撤回
     * @param message
     * @param callback
     */
    fun revokeMessage(
        message: IMMessage,
        callback: RequestCallback<Void?>?
    ) {
        deleteChattingHistory(message)
        val extentsion =
            HashMap<String, Any>()
        extentsion["content"] = message.content
        message.remoteExtension = extentsion
        NIMClient.getService(MsgService::class.java).revokeMessage(message)
            .setCallback(callback)
    }

    /**
     * 注册云信消息撤回回调
     * @param observer
     */
    fun registerMsgRevokeObserver(
        observer: Observer<RevokeMsgNotification?>?,
        register: Boolean
    ) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeRevokeMessage(observer, register)
    }

    /**
     * 删除消息历史
     * @param message
     */
    fun deleteChattingHistory(message: IMMessage?) {
        NIMClient.getService(MsgService::class.java).deleteChattingHistory(message)
    }


    /**
     * 获取云信用户信息
     * @param account 云信id
     */
    fun getNimUserInfo(account: String?): NimUserInfo? {
        val userInfo = NIMClient.getService(UserService::class.java).getUserInfo(account)
        return userInfo
    }

    fun getServerUserInfo(
        account: String?,
        callback: RequestCallback<List<NimUserInfo>>?
    ) {
        Log.e(TAG, "getServerUserInfo()......account = $account")
        try {
            val accounts: MutableList<String> = ArrayList()
            accounts?.add(account ?: "")
            NIMClient.getService(UserService::class.java).fetchUserInfo(accounts).setCallback(callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getLocalHistoryMessage(
        account: String?,
        anchor: IMMessage?,
        callback: RequestCallback<List<IMMessage>>?
    ) {

        // 本地拉取历史消息100条
//        val newTime = System.currentTimeMillis()
//        anchor = MessageBuilder.createEmptyMessage(account, SessionTypeEnum.P2P, newTime)

        NIMClient.getService(MsgService::class.java).queryMessageListEx(
            anchor, QueryDirectionEnum.QUERY_OLD, WebConfig.PAGE_SIZE, false).setCallback(callback)
    }

    /**
     * 从云信服务器查询历史消息
     * @param account
     * @param callback
     */
    fun getServerHistoryMessage(
        account: String,
        callback: RequestCallback<List<IMMessage>>?
    ) {
        Log.e(TAG, "getServerHistoryMessage-account = $account")

        // 服务端拉取历史消息100条
        val newTime = System.currentTimeMillis()
        val oldTime = newTime - 1000 * 60 * 60 * 24 * 7 // 一周前
        val anchor =
            MessageBuilder.createEmptyMessage(account, SessionTypeEnum.P2P, newTime)
        NIMClient.getService(MsgService::class.java).pullMessageHistoryEx(
            anchor, oldTime, WebConfig.PAGE_SIZE,
            QueryDirectionEnum.QUERY_OLD, false).setCallback(callback)
    }

    fun setNimNickNameAvatar(
        context: Context?, account: String?,
        mTvNickName: TextView?, mIvAvatar: ImageView?,
        avatarPlaceHoldResId:Int
    ) {
        try {
            if (context == null) return
            val userInfo: NimUserInfo? = instance.getNimUserInfo(account)
            userInfo?.let { setNickAvatarValue(context, it, mTvNickName, mIvAvatar,avatarPlaceHoldResId) }
                ?: instance.getServerUserInfo(
                    account,
                    object : RequestCallback<List<NimUserInfo>> {
                        override fun onSuccess(param: List<NimUserInfo>) {
                            if (param != null && param.size > 0) {
                                val info = param[0]
                                setNickAvatarValue(context, info, mTvNickName, mIvAvatar,avatarPlaceHoldResId)
                            }
                        }

                        override fun onFailed(code: Int) {}
                        override fun onException(exception: Throwable) {}
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setNickAvatarValue(
        context: Context?,
        info: NimUserInfo?,
        mTvNickName: TextView?,
        mIvAvatar: ImageView?,
        avatarPlaceHoldResId:Int
    ) {
        if (context == null) return
        if (info != null) {
            val nickName = info.name
            val avatarUrl = info.avatar
            if (mIvAvatar != null) {
                if (!TextUtils.isEmpty(avatarUrl)) {
                    Glide.with(context).load(avatarUrl).into(mIvAvatar)
                } else {
                    Glide.with(context).load(avatarPlaceHoldResId).into(mIvAvatar)
                }
            }
            if (mTvNickName != null) {
                mTvNickName.text = nickName
            }
        } else {
            if (mIvAvatar != null) {
                Glide.with(context).load(avatarPlaceHoldResId).into(mIvAvatar)
            }
            if (mTvNickName != null) {
                mTvNickName.text = ""
            }
        }
    }

    /**
     * 更新云信用户昵称
     */
    fun updateNimNickName(nickName: String) {
        val fields: MutableMap<UserInfoFieldEnum, Any> =
            HashMap(1)
        fields[UserInfoFieldEnum.Name] = nickName
        NIMClient.getService(UserService::class.java).updateUserInfo(fields)
    }

    /**
     * 更新云信用户头像
     */
    fun updateNimUserAvatar(avatarUrl: String) {
        val fields: MutableMap<UserInfoFieldEnum, Any> =
            HashMap(1)
        fields[UserInfoFieldEnum.AVATAR] = avatarUrl
        NIMClient.getService(UserService::class.java).updateUserInfo(fields)
    }

    /**
     * 更新云信用户性别
     * gender 1 男 ；2 女
     */
    fun updateNimUserGender(gender: Int) {
        val fields: MutableMap<UserInfoFieldEnum, Any> =
            HashMap(1)
        fields[UserInfoFieldEnum.GENDER] = if (gender == 1) GenderEnum.MALE else GenderEnum.FEMALE
        NIMClient.getService(UserService::class.java).updateUserInfo(fields)
    }

    /**
     * 清空消息未读数
     * @param account
     */
    fun clearUnreadCount(account: String?, sessionType: SessionTypeEnum?) {
        // 触发 MsgServiceObserve#observeRecentContact(Observer, boolean) 通知，
        // 通知中的 RecentContact 对象的未读数为0
        NIMClient.getService(MsgService::class.java).clearUnreadCount(account, sessionType)
    }

    /**
     * 将所有联系人的未读数清零(标记已读)
     * @param account
     */
    fun clearAllUnreadCount() {
        NIMClient.getService(MsgService::class.java).clearAllUnreadCount()
    }

    /**
     * @param sessionId p2p对方Account或者群id
     * @param sessionType
     */
    fun setChattingAccount(
        sessionId: String?,
        sessionType: SessionTypeEnum?
    ) {
        NIMClient.getService(MsgService::class.java).setChattingAccount(sessionId, sessionType)
    }

    fun setChattingAccountNone() {
        NIMClient.getService(MsgService::class.java).setChattingAccount(
                MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None
            )
    }

    /**
     * 修改被动消息
     * @param message
     */
    fun updateIMMessage(
        message: IMMessage,
        isFired: Boolean
    ): HashMap<String, Any>? {
        val localExtension =
            HashMap<String, Any>()
        localExtension[IS_FIRED_KEY] = isFired
        message.localExtension = localExtension
        NIMClient.getService(MsgService::class.java).updateIMMessage(message)
        return localExtension
    }

    fun isFired(message: IMMessage?): Boolean {
        var fired = false
        if (message == null) return fired
        val localExtension =
            message.localExtension
        if (localExtension != null) {
            if (localExtension.containsKey(IS_FIRED_KEY)) {
                fired = localExtension[IS_FIRED_KEY] as Boolean
            }
        }
        return fired
    }

    /**
     * 修改本地消息状态
     * @param message
     */
    fun updateIMMessageStatus(message: IMMessage?) {
        NIMClient.getService(MsgService::class.java).updateIMMessageStatus(message)
    }


    /**
     * 消息推送开关
     * @param enable
     */
    fun enablePush(enable: Boolean) {
        NIMClient.getService(MixPushService::class.java).enable(enable)
    }

    fun isEnablePush(): Boolean {
        return NIMClient.getService(MixPushService::class.java).isEnable
    }

}