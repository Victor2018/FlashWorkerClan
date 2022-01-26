package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.IDCardType
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.AuthParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.CameraActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.lib.luban.CompressHelper
import com.flash.worker.lib.luban.OnCompressListener
import com.flash.worker.module.business.R
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_authentication.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AuthenticationActivity
 * Author: Victor
 * Date: 2020/12/18 14:18
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.AuthenticationAct)
class AuthenticationActivity: BaseActivity(),View.OnClickListener, OnCompressListener, OnUploadListener {

    var mLoadingDialog: LoadingDialog? = null
    var idcardBackPath: String? = null
    var idcardFacePath: String? = null

    var idcardBackUrl: String? = null
    var idcardFaceUrl: String? = null

    var uploadConfigReq: UploadConfigReq? = null
    var mCompressHelper: CompressHelper? = null

    var isFront: Boolean = false

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, AuthenticationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeEvent()
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mCompressHelper = CompressHelper(null,this)

        mIvBack.setOnClickListener(this)
        mIvShootFront.setOnClickListener(this)
        mIvShootBack.setOnClickListener(this)
        mTvSubmit.setOnClickListener(this)
    }

    fun initData () {
        sendUploadConfigRequest()
    }

    fun subscribeUi() {
        fileVM.uploadConfigData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    uploadConfigReq = it.value
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.authData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    val userInfo = App.get().getUserInfo()
                    userInfo?.realNameStatus = 1
                    App.get().setUserInfo(userInfo)

                    ToastUtils.show("认证成功！")
                    LiveDataBus.send(JobActions.REFRESH_JOB_DETAIL)
                    UMengEventModule.report(this, BussinessTalentEvent.real_name_authentication_succeeded)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun subscribeEvent() {
        LiveDataBus.with(CameraActions.IDCARD_FRONT_RESULT)
            .observe(this, Observer {
                idcardFacePath = it.toString()

                var mediasList = ArrayList<LocalMedia>()
                var frontMedia = LocalMedia()
                frontMedia.path = idcardFacePath
                frontMedia.androidQToPath = idcardFacePath
                mediasList.add(frontMedia)
                mLoadingDialog?.show()
                mCompressHelper?.sendCompressRequest(mediasList)
            })

        LiveDataBus.with(CameraActions.IDCARD_BACK_RESULT)
            .observe(this, Observer {
                idcardBackPath = it.toString()

                var mediasList = ArrayList<LocalMedia>()
                var backMedia = LocalMedia()
                backMedia.path = idcardBackPath
                backMedia.androidQToPath = idcardBackPath
                mediasList.add(backMedia)
                mLoadingDialog?.show()
                mCompressHelper?.sendCompressRequest(mediasList)
            })
    }

    fun sendAuthRequest () {
        if (TextUtils.isEmpty(idcardFaceUrl)) {
            ToastUtils.show("您还没有上传身份证正面照片哦~")
            return
        }
        if (TextUtils.isEmpty(idcardBackUrl)) {
            ToastUtils.show("您还没有上传身份证反面照片哦~")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = AuthParm()
        body.idcardBackUrl = idcardBackUrl
        body.idcardFaceUrl = idcardFaceUrl

        userVM.auth(token,body)
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun showBackTipDlg () {
        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            onBackPressed()
            return
        }
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未进行实名认证，将不能发布信息哟！"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "继续认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                showBackTipDlg()
            }
            R.id.mIvShootFront -> {
                isFront = true
                NavigationUtils.goCameraActivity(this, IDCardType.TYPE_IDCARD_FRONT)
            }
            R.id.mIvShootBack -> {
                isFront = false
                NavigationUtils.goCameraActivity(this,IDCardType.TYPE_IDCARD_BACK)
            }
            R.id.mTvSubmit -> {
                sendAuthRequest()
            }
        }
    }

    fun uploadImgae2Oss (localMedia: LocalMedia?) {
        if(uploadConfigReq == null) {
            sendUploadConfigRequest()
            return
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.show(R.string.network_error)
            return
        }

        val uploadData = UploadData()
        uploadData.dir = uploadConfigReq?.data?.modelMap?.get("certificate")?.dir
        uploadData.bucketName = uploadConfigReq?.data?.modelMap?.get("certificate")?.bucket
        uploadData.ossEndPoint = uploadConfigReq?.data?.modelMap?.get("certificate")?.endpoint

        uploadData.localMedia = localMedia

        mLoadingDialog?.show()

        OssUploadModule.instance.upload(uploadData,this)
    }

    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"OnUpload-url = $url")
                if (isFront) {
                    idcardFaceUrl = url
                    ImageUtils.instance.loadImage(this,mIvShootFront,url)
                } else {
                    idcardBackUrl = url
                    ImageUtils.instance.loadImage(this,mIvShootBack,url)
                }
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    override fun OnCompress(datas: List<LocalMedia>?, msg: String?) {
        mLoadingDialog?.dismiss()

        if (datas == null) {
            ToastUtils.showShort("图片压缩失败")
            return
        }
        if (datas?.size == 0) {
            ToastUtils.showShort("图片压缩失败")
            return
        }
        uploadImgae2Oss(datas.get(0))
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
        mCompressHelper?.onDestroy()
    }
}