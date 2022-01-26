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
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.req.EditEmployerDetailReq
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import com.flash.worker.lib.coremodel.data.bean.CityInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ProvinceInfo
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.UpdateEmployerParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.EmployerVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.module.business.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_edit_employer.*

class EditEmployerActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
    TextWatcher,RadioGroup.OnCheckedChangeListener, OnCityPickerListener, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,employerId: String?) {
            var intent = Intent(activity, EditEmployerActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,employerId)
            activity.startActivity(intent)
        }
    }

    var employerId: String? = null

    var mLoadingDialog: LoadingDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null

    var provinceData: List<ProvinceInfo>? = null
    var workProvince: String? = null
    var workCity: String? = null
    var workDistrict: String? = null

    var selectList: MutableList<LocalMedia> = ArrayList()
    var uploadConfigReq: UploadConfigReq? = null

    var identity = 3//雇主类型：默认个人
    var businessLicenseUrl: String? = null
    var mEditEmployerDetailReq: EditEmployerDetailReq? = null

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonViewModelFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_edit_employer

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mIvAuthentication.setOnClickListener(this)
        mFlBusinessLicense.setOnClickListener(this)
        mIvDelBusinessLicense.setOnClickListener(this)
        mEtDescription.addTextChangedListener(this)
        mTvDelete.setOnClickListener(this)
        mTvSave.setOnClickListener(this)

        mRgIdentity.setOnCheckedChangeListener(this)
    }


    fun initData (intent: Intent?) {
        employerId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)

        sendUploadConfigRequest()

        if (mEditEmployerDetailReq == null) {
            sendEditEmployerDetailRequest()
        }

        provinceData = App.get().getCityData()

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            mIvAuthentication.visibility = View.GONE
        } else {
            mIvAuthentication.visibility = View.VISIBLE
        }

    }

    fun subscribeEvent() {

    }

    fun subscribeUi() {
        commonVM.areaTreeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getCityPickerDialog(it.value.data)?.show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

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

        employerVM.editEmployerDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showEmployerData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerVM.updateEmployerData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("保存成功！")
                    UMengEventModule.report(this, BussinessEmployerEvent.update_employer)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerVM.deleteEmployerData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("雇主删除成功！")
                    UMengEventModule.report(this, BussinessEmployerEvent.delete_employer)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendEditEmployerDetailRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        employerVM.getEditEmployerDetail(token,employerId)
    }

    fun sendUpdateEmployerRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var name = mEtEmployerName.text.toString()
        var description = mEtDescription.text.toString()
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show("请输入雇主名称")
            return
        }

        var body = UpdateEmployerParm()
        body.id = employerId
        body.name = name
        body.identity = identity
        body.workProvince = workProvince
        body.workCity = workCity
        body.workDistrict = workDistrict
        if (identity != 3) {
            body.licencePic = businessLicenseUrl
        }
        body.selfDescription = description

        mLoadingDialog?.show()
        employerVM.updateEmployer(token,body)
    }

    fun sendDeleteEmployerRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        mLoadingDialog?.show()
        employerVM.deleteEmployer(token,employerId)
    }

    fun showEmployerData (data: EditEmployerDetailReq) {
        mEditEmployerDetailReq = data
        businessLicenseUrl = data.data?.licencePic
        identity = data.data?.identity ?: 0

        workProvince = data.data?.workProvince
        workCity = data.data?.workCity
        workDistrict = data.data?.workDistrict


        mEtEmployerName.setText(data.data?.name)
        if (data.data?.identity == 1) {
            mRbEnterprise.isChecked = true
        } else if (data.data?.identity == 2) {
            mRbMerchant.isChecked = true
        } else if (data.data?.identity == 3) {
            mRbPersonal.isChecked = true
        }

        var licenceAuth = data.data?.licenceAuth ?: false
        if (!TextUtils.isEmpty(data.data?.licencePic) && licenceAuth) {
            ImageUtils.instance.loadImage(this,mIvBusinessLicense,data.data?.licencePic)
            mIvDelBusinessLicense.visibility = View.VISIBLE
            mIvUploadAdd.visibility = View.GONE
            mTvUploadTip.visibility = View.GONE
        } else {
            mIvDelBusinessLicense.visibility = View.GONE
            mIvUploadAdd.visibility = View.VISIBLE
            mTvUploadTip.visibility = View.VISIBLE
        }

        mEtDescription.setText(data.data?.selfDescription)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackAction()
            }
            R.id.mIvAuthentication -> {
                NavigationUtils.goRealNameActivity(this)
            }
            R.id.mTvWorkArea -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mFlBusinessLicense -> {
                PictureSelectorUtil.selectMedia(this,false,true,false,1)
            }
            R.id.mIvDelBusinessLicense -> {
                businessLicenseUrl = ""
                mIvDelBusinessLicense.visibility = View.GONE
                mIvUploadAdd.visibility = View.VISIBLE
                mTvUploadTip.visibility = View.VISIBLE
                mIvBusinessLicense.setImageBitmap(null)
            }
            R.id.mTvDelete -> {
                showDeleteTipDlg()
            }
            R.id.mTvSave -> {
                sendUpdateEmployerRequest()
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
                sendUpdateEmployerRequest()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    fun showDeleteTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "雇主信息删除后，已完成工单和关注该雇主的人才将无法查看信息，请谨慎删除！"
        commonTipDialog.mCancelText = "暂不删除"
        commonTipDialog.mOkText = "确定删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendDeleteEmployerRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showAuthTipDlg () {
        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            onBackPressed()
            return
        }
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未进行实名认证，发布求职岗位时会发布不了哦。"
        commonTipDialog.mCancelText = "暂不认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@EditEmployerActivity)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {

        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.mOnCityPickerListener = this
            provinceData = data
        }

        return mCityPickerDialog
    }


    override fun afterTextChanged(s: Editable?) {
        mTvDescriptionCount.setText("${s?.length}/200")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return

        workProvince = province?.name
        workCity = city?.name
        workDistrict = area?.name
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}