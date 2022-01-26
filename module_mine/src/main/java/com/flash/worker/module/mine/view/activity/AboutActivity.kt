package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.dialog.AppUpdateDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.LatestVersionParm
import com.flash.worker.lib.coremodel.data.req.LatestVersionReq
import com.flash.worker.lib.coremodel.http.api.HtmlApi
import com.flash.worker.lib.coremodel.util.AppConfig
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UpdateVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, AboutActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val updateVM: UpdateVM by viewModels {
        InjectorUtils.provideUpdateVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mLatestVersionReq: LatestVersionReq? = null

    override fun getLayoutResource() = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvServiceAgreement.setOnClickListener(this)
        mTvPrivacyPolicy.setOnClickListener(this)
        mTvVersionUpdate.setOnClickListener(this)
    }

    fun initData () {
        var vName = "版本号：V" + AppUtil.getAppVersionName(this)

        if (AppConfig.MODEL_DEBUG) {
            vName += "_" + AppConfig.BUILD_NUMBER
            if (AppConfig.MODEL_DEV) {
                vName += "_dev"
            }
            if (AppConfig.MODEL_UAT) {
                vName += "_uat"
            }
        }
        mTvVersion.text = vName

        sendLatestVersionRequest()
    }

    fun subscribeUi () {
        updateVM.latestVersionData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showLatestVersionData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendLatestVersionRequest () {
        mLoadingDialog?.show()

        var body = LatestVersionParm()
        body.versionCode = AppUtil.getAppVersionCode(this)

        updateVM.fetchLatestVersion(body)
    }

    fun showLatestVersionData (data: LatestVersionReq) {
        mLatestVersionReq = data
        var isUpdate = data.data?.isUpdate ?: false
        if (isUpdate) {
            mViewUpdateTip.visibility = View.VISIBLE
        } else {
            mViewUpdateTip.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mTvServiceAgreement -> {
                WebActivity.intentStart(this,"服务协议",HtmlApi.SERVICE_AGREEMENT)
                UMengEventModule.report(this, MineEvent.view_service_agreement)
            }
            R.id.mTvPrivacyPolicy -> {
                WebActivity.intentStart(this,"隐私政策",HtmlApi.PRIVACY_POLICY)
                UMengEventModule.report(this, MineEvent.view_privacy_policy)
            }
            R.id.mTvVersionUpdate -> {
                var isUpdate = mLatestVersionReq?.data?.isUpdate ?: false
                if (isUpdate) {
                    var appUpdateDialog = AppUpdateDialog(this)
                    appUpdateDialog.mLatestVersionData = mLatestVersionReq?.data

                    appUpdateDialog.show()
                } else {
                    ToastUtils.show("当前已是最新版本")
                }
            }
        }
    }

}