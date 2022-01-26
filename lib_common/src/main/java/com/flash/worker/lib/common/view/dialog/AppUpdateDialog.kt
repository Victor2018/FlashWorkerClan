package com.flash.worker.lib.common.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.data.ProgressInfo
import com.flash.worker.lib.common.interfaces.OnDownloadProgressListener
import com.flash.worker.lib.common.module.DownloadModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.coremodel.data.bean.LatestVersionData
import kotlinx.android.synthetic.main.dlg_app_update.*
import java.io.File


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AppUpdateDialog
 * Author: Victor
 * Date: 2021/1/15 15:26
 * Description: app 升级 dialog
 * -----------------------------------------------------------------
 */
class AppUpdateDialog(context: Context): AbsDialog(context), View.OnClickListener,
        DialogInterface.OnKeyListener, OnDownloadProgressListener {

    val TAG = "AppUpdateDialog"

    var mLatestVersionData: LatestVersionData? = null

    var mExitTime: Long = 0
    var mProgressInfo: ProgressInfo? = null

    override fun bindContentView() = R.layout.dlg_app_update

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (DensityUtil.getDisplayWidth() * 0.85).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        setCanceledOnTouchOutside(false)
        mIvClose.setOnClickListener(this)
        mTvUpdateNow.setOnClickListener(this)

        setOnKeyListener(this)
    }

    fun initData () {
        mTvNewVersion.text = mLatestVersionData?.versionName
        mTvUpdateContent.text = mLatestVersionData?.releaseNotes

        var isForceUpdate = mLatestVersionData?.isForceUpdate ?: false
        if (isForceUpdate) {
            mIvClose.visibility = View.GONE
        } else {
            mIvClose.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
       when (v?.id) {
           R.id.mIvClose -> {
               dismiss()
           }
           R.id.mTvUpdateNow -> {
               if (mProgressInfo != null){
                   install(true,mProgressInfo?.getFile()!!)
                   return
               }
               download()
           }
       }
    }

    fun install (install: Boolean,file: File?) {
        Loger.e(TAG,"install()......")
        mTvUpdateNow.text = "安装"
        mTvUpdateNow.visibility = View.VISIBLE
        mPbDownloadProgress.visibility = View.GONE
        mTvStatus.text = ""
        if (install) {
            InstallApkUtil.install(context,file!!)
        }
    }

    fun download () {
        mTvUpdateNow.visibility = View.INVISIBLE
        mPbDownloadProgress.visibility = View.VISIBLE
        mTvStatus.text = "正在更新..."

        DownloadModule.instance.downloadFile(context,mLatestVersionData,this)
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                var isForceUpdate = mLatestVersionData?.isForceUpdate ?: false
                if (isForceUpdate) {
                    if (System.currentTimeMillis() - mExitTime < 2000) {
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0)
                    } else {
                        mExitTime = System.currentTimeMillis()
                        ToastUtils.show("再按一次退出")
                    }
                    return true
                }

            }
        }

        return false
    }

    override fun OnDownloadProgress(data: ProgressInfo?) {
        Loger.e(TAG,"OnDownloadProgress-percent = " + data?.percent())
        mPbDownloadProgress?.progress = data?.percent()?.toInt() ?: 0
    }

    override fun OnDownloadCompleted(data: ProgressInfo?) {
        if (DownloadModule.instance.isFileExists(data)) {
            mProgressInfo = data
            install(true,data?.getFile()!!)
        }
    }

    override fun OnError(error: String) {
        ToastUtils.show("下载失败：$error")
        mTvUpdateNow.text = "重试"
        mTvUpdateNow.visibility = View.VISIBLE
        mPbDownloadProgress.visibility = View.GONE
        mTvStatus.text = ""
    }

}