package com.flash.worker.lib.common.module

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.view.activity.WebActivity

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SchemaModule
 * Author: Victor
 * Date: 2020/7/27 下午 05:34
 * Description: schema跳转模块
 * -----------------------------------------------------------------
 */
object SchemaModule {
    var TAG = "SchemaModule"

    /**
     * 打开外部地址
     */
    val DOMAIN_OPEN_URL = "openurl"
    val DOMAIN_MAIL = "mail"
    val DOMAIN_TALENT_DETAIL = "talentDetail"
    val DOMAIN_JOB_DETAIL = "jobDetail"
    val DOMAIN_GUILD_DETAIL = "guildDetail"
    val DOMAIN_TASK_DETAIL = "taskDetail"

    /**
     * 当前界面传入
     *
     * @param ctx
     * @param uri
     */
    fun dispatchSchema(activity: Activity, uri: Uri?) {
        Loger.e(TAG, "dispatchSchema()-uri = $uri")
        if (uri == null) return
        val domain = uri.authority
        try {
            when (domain) {
                DOMAIN_OPEN_URL -> {
                    //打开浏览器
                    schemaWeb(activity, uri)
                }
                DOMAIN_MAIL -> {
                    //打开邮件发送
                    sendEmail(activity, uri)
                }
                DOMAIN_TALENT_DETAIL -> {
                    val releaseId = uri.getQueryParameter("releaseId")
                    val intentType = uri.getQueryParameter("intentType")
                    var action = 0
                    if (TextUtils.isEmpty(intentType)) {
                        action = intentType?.toInt() ?: 0
                    }
                    NavigationUtils.goTalentDetailActivity(activity,releaseId,action)
                }
                DOMAIN_JOB_DETAIL -> {
                    val releaseId = uri.getQueryParameter("releaseId")
                    val intentType = uri.getQueryParameter("intentType")
                    var action = 0
                    if (TextUtils.isEmpty(intentType)) {
                        action = intentType?.toInt() ?: 0
                    }
                    NavigationUtils.goJobDetailActivity(activity,releaseId,
                        null,null,action)
                }
                DOMAIN_TASK_DETAIL -> {
                    val releaseId = uri.getQueryParameter("releaseId")
                    val intentType = uri.getQueryParameter("intentType")
                    var action = 0
                    if (TextUtils.isEmpty(intentType)) {
                        action = intentType?.toInt() ?: 0
                    }
                    NavigationUtils.goTaskDetailActivity(activity,releaseId,
                        null,null,action)
                }
                DOMAIN_GUILD_DETAIL -> {
                    val guildId = uri.getQueryParameter("guildId")
                    NavigationUtils.goGuildDetailActivity(activity,guildId,2)
                }
                else -> {
                    NavigationUtils.goHomeActivity(activity,0)
                }
            }
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * 从App内部点击进入
     *
     * @param uri
     */

    private fun schemaWeb(context: Context, uri: Uri) {
        val title = uri.getQueryParameter("title")
        val url = uri.getQueryParameter("url")
        Loger.e(TAG, "schemaWeb()-url = " + url)
        if (TextUtils.isEmpty(url)) return
        WebActivity.intentStart(context, title, url)
    }

    fun sendEmail (context: Context, uri: Uri) {
        val gmail = uri.getQueryParameter("gmail")
        // 必须明确使用mailto前缀来修饰邮件地址
        val uri = Uri.parse(String.format(Constant.MAIL_TO,gmail))
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.mail_to_me_tip)))
    }
}