package com.flash.worker.lib.common.util

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.text.TextUtils
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.view.activity.SplashActivity
import com.flash.worker.lib.coremodel.util.AppUtil
import com.netease.nimlib.sdk.NotificationFoldStyle
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import com.netease.nimlib.sdk.mixpush.MixPushConfig
import java.io.IOException


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: NimSDKOptionConfig
 * Author: Victor
 * Date: 2021/3/11 14:41
 * Description: 
 * -----------------------------------------------------------------
 */
object NimSDKOptionConfig {
    fun getSDKOptions(context: Context): SDKOptions? {
        val options = SDKOptions()
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options)
        // 配置 APP 保存图片/语音/文件/log等数据的目录
        options.sdkStorageRootPath = getAppCacheDir(context) + "/nim" // 可以不设置，那么将采用默认路径
        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true
        // 配置附件缩略图的尺寸大小
//        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge()
        // 通知栏显示用户昵称和头像
//        options.userInfoProvider = NimUserInfoProvider(DemoCache.getContext())
        // 在线多端同步未读数
        options.sessionReadAck = true
        // 动图的缩略图直接下载原图
        options.animatedImageThumbnailEnabled = true
        // 采用异步加载SDK
        options.asyncInitSDK = true
        // 是否是弱IM场景
        options.reducedIM = false
        // 是否检查manifest 配置，调试阶段打开，调试通过之后请关掉
        options.checkManifestConfig = true
        // 是否启用群消息已读功能，默认关闭
        options.enableTeamMsgAck = true
        // 打开消息撤回未读数-1的开关
        options.shouldConsiderRevokedMessageUnreadCount = true
        options.mixPushConfig = buildMixPushConfig()
        //        options.mNosTokenSceneConfig = createNosTokenScene();
        options.loginCustomTag = "登录自定义字段"
        options.useXLog = false
        // 会话置顶是否漫游
        options.notifyStickTopSession = true
        // 设置数据库加密秘钥（替换为自己的秘钥），开启加密；如不需要加密，则去掉
//        options.databaseEncryptKey = "123456"
        return options
    }


    /**
     * 配置 APP 保存图片/语音/文件/log等数据的目录
     * 这里示例用SD卡的应用扩展存储目录
     */
    fun getAppCacheDir(context: Context): String? {
        var storageRootPath: String? = null
        try {
            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
            if (context.externalCacheDir != null) {
                storageRootPath = context.externalCacheDir!!.canonicalPath
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(storageRootPath)) {
            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
            storageRootPath = Environment.getExternalStorageDirectory()
                .toString() + "/" + AppUtil.getPackageName(context)
        }
        return storageRootPath
    }

    private fun initStatusBarNotificationConfig(options: SDKOptions) {
        // SDK statusBarNotificationConfig
        options.statusBarNotificationConfig = getStatusBarNotificationConfig()
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    fun getStatusBarNotificationConfig(): StatusBarNotificationConfig {
        val config = StatusBarNotificationConfig()
        // 点击通知需要跳转到的界面
        config.notificationEntrance = SplashActivity::class.java
        config.notificationSmallIconId = R.mipmap.ic_nim_notice
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg"
        config.notificationFoldStyle = NotificationFoldStyle.ALL
        config.downTimeEnableNotification = true
        // 呼吸灯配置
        config.ledARGB = Color.GREEN
        config.ledOnMs = 1000
        config.ledOffMs = 1500
        // 是否APP ICON显示未读数红点(Android O有效)
        config.showBadge = true
        // save cache，留做切换账号备用
//        DemoCache.setNotificationConfig(config)
        return config
    }


    private fun buildMixPushConfig(): MixPushConfig? {
        // 第三方推送配置
        val config = MixPushConfig()
        // 小米推送
        config.xmAppId = "2882303761519784634"
        config.xmAppKey = "5721978484634"
        config.xmCertificateName = "xm_push"


        // 华为推送
        config.hwAppId = "103975779"
        config.hwCertificateName = "hw_push"


        // vivo推送
        config.vivoCertificateName = "vivo_push"

        // oppo推送
        config.oppoAppId = "30490011"
        config.oppoAppKey = "fbdcd35a6a634484a366d7fc1768070c"
        config.oppoAppSercet = "b4a77634764d41a8a90a1bb0f617dc57"
//        config.oppoAppSercet = "2ba1878662364b58afdf86e92e05442f"//MasterSecret经过测试都可以收到
        config.oppoCertificateName = "oppo_push"

        // 魅族推送
        config.mzAppId = "139713"
        config.mzAppKey = "28a576ba60b040c891a99ea6c8d70028"
        config.mzCertificateName = "flyme_push"

        return config
    }
}