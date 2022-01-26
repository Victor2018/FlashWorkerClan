package com.flash.worker.lib.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: InstallApkUtil
 * Author: Victor
 * Date: 2021/8/31 15:27
 * Description: 
 * -----------------------------------------------------------------
 */
object InstallApkUtil {
    fun install (context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val packageName = context.packageName
        val authority = "$packageName.provider"
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context?.startActivity(intent)
    }
}