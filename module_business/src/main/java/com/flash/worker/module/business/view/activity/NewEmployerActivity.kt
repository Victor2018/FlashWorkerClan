 package com.flash.worker.module.business.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.module.business.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_new_employer.*

@Route(path = ARouterPath.NewEmployerAct)
class NewEmployerActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
    TextWatcher,RadioGroup.OnCheckedChangeListener, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, NewEmployerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null

    var selectList: MutableList<LocalMedia> = ArrayList()
    var uploadConfigReq: UploadConfigReq? = null

    var identity = 3//雇主类型：默认个人
    var businessLicenseUrl: String? = null

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_new_employer

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)

        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mIvAuthentication.setOnClickListener(this)
        mFlBusinessLicense.setOnClickListener(this)
        mIvDelBusinessLicense.setOnClickListener(this)
        mEtDescription.addTextChangedListener(this)
        mTvSave.setOnClickListener(this)

        mRgIdentity.setOnCheckedChangeListener(this)
    }

    fun initData () {
        sendUploadConfigRequest()

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 0) {
            mIvAuthentication.visibility = View.VISIBLE
        } else {
            mIvAuthentication.visibility = View.GONE
        }
    }

    fun subscribeUi() {
        fileVM.uploadConfigData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    uploadConfigReq = it.value
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerVM.saveEmployerData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("保存成功！")
                    val userInfo = App.get().getUserInfo()
                    if (userInfo?.realNameStatus == 0) {
                        showAuthTipDlg()
                        return@Observer
                    }

                    UMengEventModule.report(this, BussinessEmployerEvent.new_employer)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendSaveEmployerRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var name = mEtEmployerName.text.toString()
        var description = mEtDescription.text.toString()
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show("请输入雇主名称")
            return
        }

        var body = SaveEmployerParm()
        body.name = name
        body.identity = identity
        if (identity != 3) {
            body.licencePic = businessLicenseUrl
        }
        body.selfDescription = description

        mLoadingDialog?.show()
        employerVM.saveEmployer(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackAction()
            }
            R.id.mIvAuthentication -> {
                NavigationUtils.goRealNameActivity(this)
            }
            R.id.mFlBusinessLicense -> {
                PictureSelectorUtil.selectMedia(this,false,true,
                    false,1)
            }
            R.id.mIvDelBusinessLicense -> {
                businessLicenseUrl = ""
                mIvDelBusinessLicense.visibility = View.GONE
                mIvUploadAdd.visibility = View.VISIBLE
                mTvUploadTip.visibility = View.VISIBLE
                mIvBusinessLicense.setImageBitmap(null)
            }
            R.id.mTvSave -> {
                sendSaveEmployerRequest()
            }
        }
    }

    fun onBackAction () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未保存当前页面\n如需保存，请点击保存返回。"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "保存"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendSaveEmployerRequest()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未进行实名认证，将不能发布信息哟！"
        commonTipDialog.mCancelText = "暂不认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@NewEmployerActivity)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    override fun afterTextChanged(s: Editable?) {
        mTvDescriptionCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (id) {
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbEnterprise -> {
                identity = 1
                mTvBusinessLicense.visibility = View.VISIBLE
                mFlBusinessLicense.visibility = View.VISIBLE
            }
            R.id.mRbPersonal -> {
                identity = 3
                mTvBusinessLicense.visibility = View.GONE
                mFlBusinessLicense.visibility = View.GONE
            }
            R.id.mRbMerchant -> {
                identity = 2
                mTvBusinessLicense.visibility = View.VISIBLE
                mFlBusinessLicense.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val list = PictureSelector.obtainMultipleResult(data)
                    Loger.e(BaseFragment.TAG,"onActivityResult-res = " + JsonUtils.toJSONString(list.get(0)))
                    selectList.clear()
                    selectList.addAll(list)
                    uploadImgae2Oss()

                    PictureSelector.obtainMultipleResult(data).clear()
                }
            }
        }
    }

    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"url = $url")
                businessLicenseUrl = url
                ImageUtils.instance.loadImage(this,mIvBusinessLicense,businessLicenseUrl)
                mIvDelBusinessLicense.visibility = View.VISIBLE
                mIvUploadAdd.visibility = View.GONE
                mTvUploadTip.visibility = View.GONE
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    fun uploadImgae2Oss () {
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

        var localMedia = LocalMedia()

        if (selectList == null || selectList.size == 0) {
            localMedia.androidQToPath = businessLicenseUrl
            localMedia.path = businessLicenseUrl
            uploadData.localMedia = localMedia
        } else {
            uploadData.localMedia = selectList.get(0)
        }


        mLoadingDialog?.show()

        OssUploadModule.instance.upload(uploadData,this)
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }

}