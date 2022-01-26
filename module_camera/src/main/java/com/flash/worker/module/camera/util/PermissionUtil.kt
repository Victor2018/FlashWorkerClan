package com.flash.worker.module.camera.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: PermissionUtil
 * Author: Victor
 * Date: 2020/12/18 18:03
 * Description: 
 * -----------------------------------------------------------------
 */
object PermissionUtil {
    /**
     * 权限检测
     * @param permission
     * @return
     */
    fun hasPermission(
        context: Context?,
        permission: String?
    ): Boolean {
        if (TextUtils.isEmpty(permission)) return false
        var hasPermission = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, permission!!)
                != PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = false
            }
        }
        return hasPermission
    }

    fun requestPermission(
        activity: Activity?,
        permissions: Array<String>?,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity!!, permissions!!, requestCode)
    }

    fun requestPermission(
        fragment: Fragment,
        permissions: Array<String>?,
        requestCode: Int
    ) {
        fragment.requestPermissions(permissions!!, requestCode)
    }

    // 判断是否打开了通知监听权限
    fun isNotificationEnabled(context: Context?): Boolean {
        if (context == null) return false
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn =
                    ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun openNotification(context: Context?) {
        if (context == null) return
        context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
    }
}