package com.flash.worker.module.business.view.activity

import android.app.Activity
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
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.business.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_employer_basic.*

@Route(path = ARouterPath.EmployerBasicAct)
class EmployerBasicActivity : BaseActivity(),View.OnClickListener, OnCityPickerListener, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EmployerBasicActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null

    var liveProvince: String? = null
    var liveCity: String? = null
    var liveDistrict: String? = null

    var provinceData: List<ProvinceInfo>? = null
    var headPic: String? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonViewModelFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_employer_basic

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)

        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mCivEmployerAvatar.setOnClickListener(this)
        mTvResumeCurrentCity.setOnClickListener(this)
        mTvNext.setOnClickListener(this)
    }

    fun initData () {
        provinceData = App.get().getCityData()

        var userInfo = App.get().getUserInfo()
        headPic = userInfo?.headpic ?: ""
        liveProvince = userInfo?.liveProvince ?: ""
        liveCity = userInfo?.liveCity ?: ""
        liveDistrict = userInfo?.liveDistrict ?: ""

        mEtUserName.setText(userInfo?.username ?: "")
        ImageUtils.instance.loadImage(this,mCivEmployerAvatar,headPic,R.mipmap.ic_avatar)
        mTvResumeCurrentCity.text = liveProvince + liveCity + liveDistrict

        sendUploadConfigRequest()
    }

    fun subscribeUi() {
        commonVM.areaTreeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getCityPickerDialog(it.value.data)?.show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        fileVM.uploadConfigData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    uploadConfigReq = it.value
                    uploadImgae2Oss()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.updateEmployerBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    sendUserInfoRequest(true)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.userInfoData.observeForever {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(it.value.data)
                    NewEmployerActivity.intentStart(this)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        }
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

    fun sendUpdateEmployerBaseInfoRequest () {
        var userName = mEtUserName.text.toString()
        var currentCity = mTvResumeCurrentCity.text.toString()

        if (TextUtils.isEmpty(userName)) {
            ToastUtils.show("请输入闪工名")
            return
        }
        if (TextUtils.isEmpty(currentCity)) {
            ToastUtils.show("请选择现居城市")
            return
        }

        var resumeBaseInfo = EmployerBaseInfo()
        resumeBaseInfo.username = userName
        resumeBaseInfo.liveProvince = liveProvince
        resumeBaseInfo.liveCity = liveCity
        resumeBaseInfo.liveDistrict = liveDistrict
        if (!TextUtils.isEmpty(headPic)) {
            resumeBaseInfo.headpic = headPic
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        mLoadingDialog?.show()
        userVM.updateEmployerBaseInfo(token,resumeBaseInfo)
    }

    fun sendUserInfoRequest (showLoading: Boolean) {
        if (!App.get().hasLogin()) return

        if (showLoading) {
            mLoadingDialog?.show()
        }
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.fetchUserInfo(token)
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

    fun uploadImgae2Oss () {
        if(uploadConfigReq == null) {
            sendUploadConfigRequest()
            return
        }

        if (selectList == null || selectList.size == 0) return

        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.show(R.string.network_error)
            return
        }

        val uploadData = UploadData()
        uploadData.dir = uploadConfigReq?.data?.modelMap?.get("works")?.dir
        uploadData.bucketName = uploadConfigReq?.data?.modelMap?.get("works")?.bucket
        uploadData.ossEndPoint = uploadConfigReq?.data?.modelMap?.get("works")?.endpoint

        uploadData.localMedia = selectList.get(0)


        mLoadingDialog?.show()

        OssUploadModule.instance.upload(uploadData,this)
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return
        liveProvince = province?.name
        liveCity = city?.name
        liveDistrict = area?.name

        mTvResumeCurrentCity.text = province?.name + city?.name + area?.name
    }


    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"url = $url")
                headPic = url
                ImageUtils.instance.loadImage(this,mCivEmployerAvatar,
                    headPic,R.mipmap.ic_avatar)
                if (selectList != null && selectList.size > 1) {
                    selectList.removeAt(0)
                    uploadImgae2Oss()
                }
            } else {
                ToastUtils.show("图片上传失败-error = $url")
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
                    Loger.e(TAG,"onActivityResult-res = " + JsonUtils.toJSONString(list.get(0)))
                    selectList.clear()
                    selectList.addAll(list)

                    uploadImgae2Oss()

                    PictureSelector.obtainMultipleResult(data).clear()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mCivEmployerAvatar -> {
                PictureSelectorUtil.selectMedia(this,false,true,
                    true,1)
            }
            R.id.mTvResumeCurrentCity -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mTvNext -> {
                sendUpdateEmployerBaseInfoRequest()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }
}