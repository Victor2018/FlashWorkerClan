package com.flash.worker.module.mine.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.*
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.mine.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(),View.OnClickListener, OnUploadListener,
        OnWorkingYearsSelectListener, OnHeightSelectListener, OnWeightSelectListener,
        OnCityPickerListener,CompoundButton.OnCheckedChangeListener {

    var selectList: MutableList<LocalMedia> = ArrayList()
    var uploadConfigReq: UploadConfigReq? = null

    var mWorkingYearsPickerDialog: WorkingYearsPickerDialog? = null
    var mHeightPickerDialog: HeightPickerDialog? = null
    var mWeightPickerDialog: WeightPickerDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null
    var mLoadingDialog: LoadingDialog? = null
    var mUserInfo: UserInfo? = null
    var provinceData: List<ProvinceInfo>? = null

    var mProvinceInfo: ProvinceInfo? = null
    var mCityInfo: CityInfo? = null
    var mAreaInfo: AreaInfo? = null

    var isInIt: Boolean = true

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EditProfileActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_edit_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mClAvatar.setOnClickListener(this)
        mClEditUserName.setOnClickListener(this)
        mClWorkYears.setOnClickListener(this)
        mClStudent.setOnClickListener(this)
        mClHeight.setOnClickListener(this)
        mClWeight.setOnClickListener(this)
        mClCity.setOnClickListener(this)
        mClEmergencyContact.setOnClickListener(this)
        mClInviteId.setOnClickListener(this)

        mToggleStudent.setOnCheckedChangeListener(this)
    }

    fun initData () {
        mUserInfo = App.get().getUserInfo()
        provinceData = App.get().getCityData()

        ImageUtils.instance.loadImage(this,mCivAvatar,mUserInfo?.headpic,R.mipmap.ic_avatar)
        mTvUserId.text = mUserInfo?.userId
        mTvUserName.text = mUserInfo?.username
        if (mUserInfo?.sex == 0) {
            mTvSex.text = "女"
        } else if (mUserInfo?.sex == 1) {
            mTvSex.text = "男"
        } else if (mUserInfo?.sex == 2) {
            mTvSex.text = "其它"
        }
        var currentCity = "${mUserInfo?.liveCity}${mUserInfo?.liveDistrict}"
        if (!TextUtils.isEmpty(currentCity)) {
            if (!currentCity.contains("null")) {
                mTvCity.text = currentCity
            }
        }
        mTvEmergencyContact.text = mUserInfo?.contactPhone
        mTvWorkingYears.text = mUserInfo?.workYears
        mToggleStudent.isChecked = mUserInfo?.identity == 2
        mTvHeight.text = "${mUserInfo?.height}cm"
        mTvWeight.text = "${mUserInfo?.weight}kg"

        mTvInviteId.text = mUserInfo?.inviterUserId

        if (TextUtils.isEmpty(mUserInfo?.inviterUserId)) {
            TextViewBoundsUtil.setTvDrawableRight(this,mTvInviteId,R.mipmap.ic_right)
        } else {
            TextViewBoundsUtil.setTvDrawableRight(this,mTvInviteId,0)
        }

        isInIt = false
    }

    fun subscribeUi() {
        userVM.updateAvatarData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ImageUtils.instance.loadImage(this,mCivAvatar,mUserInfo?.headpic,R.mipmap.ic_avatar)
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("头像修改成功")
                    UMengEventModule.report(this, MineEvent.edit_avatar)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.updateWorkYearsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("工作年限修改成功")
                    UMengEventModule.report(this, MineEvent.edit_work_years)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.updateIdentityData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (mToggleStudent.isChecked) {
                        mUserInfo?.identity = 2
                    } else {
                        mUserInfo?.identity = 1
                    }
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("身份修改成功")

                    UMengEventModule.report(this, MineEvent.edit_identity)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.updateHeightData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("身高修改成功")

                    UMengEventModule.report(this, MineEvent.edit_height)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.updateWeightData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    App.get().setUserInfo(mUserInfo)
                    ToastUtils.show("体重修改成功")

                    UMengEventModule.report(this, MineEvent.edit_weight)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.updateLiveCityData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mTvCity.text = mCityInfo?.name + mAreaInfo?.name

                    mUserInfo?.liveProvince = mProvinceInfo?.name
                    mUserInfo?.liveCity = mCityInfo?.name
                    mUserInfo?.liveDistrict = mAreaInfo?.name
                    App.get().setUserInfo(mUserInfo)

                    ToastUtils.show("现居城市修改成功")

                    UMengEventModule.report(this, MineEvent.edit_live_city)
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
                    uploadImgae2Oss()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

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
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendUpdateAvatarRequest (url: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateAvatarParm()
        body.headpic = url

        userVM.updateAvatar(token,body)
    }

    fun sendUpdateWorkYearsRequest (workYears: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateWorkYearsParm()
        body.workYears = workYears

        userVM.updateWorkYears(token,body)
    }

    fun sendUpdateIdentityRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateIdentityParm()
        if (mToggleStudent.isChecked) {
            body.identity = 2
        } else {
            body.identity = 1
        }

        userVM.updateIdentity(token,body)
    }

    fun sendUpdateHeightRequest (height: Int) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateHeightParm()
        body.height = height

        userVM.updateHeight(token,body)
    }

    fun sendUpdateWeightRequest (weight: Int) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateWeightParm()
        body.weight = weight

        userVM.updateWeight(token,body)
    }

    fun sendUpdateCityRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateLiveCityParm()
        body.liveProvince = mProvinceInfo?.name
        body.liveCity = mCityInfo?.name
        body.liveDistrict = mAreaInfo?.name

        userVM.updateLiveCity(token,body)
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun getWorkingYearsPickerDialog (): WorkingYearsPickerDialog? {
        if (mWorkingYearsPickerDialog == null) {
            mWorkingYearsPickerDialog = WorkingYearsPickerDialog(this)
            mWorkingYearsPickerDialog?.mOnWorkingYearsSelectListener = this
        }
        mWorkingYearsPickerDialog?.mWorkYears = mUserInfo?.workYears
        return mWorkingYearsPickerDialog
    }

    fun getHeightPickerDialog (): HeightPickerDialog? {
        if (mHeightPickerDialog == null) {
            mHeightPickerDialog = HeightPickerDialog(this)
            mHeightPickerDialog?.mOnHeightSelectListener = this
        }
        mHeightPickerDialog?.mHeight = mUserInfo?.height
        return mHeightPickerDialog
    }

    fun getWeightPickerDialog (): WeightPickerDialog? {
        if (mWeightPickerDialog == null) {
            mWeightPickerDialog = WeightPickerDialog(this)
            mWeightPickerDialog?.mOnWeightSelectListener = this
        }
        mWeightPickerDialog?.mWeight = mUserInfo?.weight
        return mWeightPickerDialog
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {

        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.mOnCityPickerListener = this
            provinceData = data
        }

        if (mProvinceInfo == null) {
            mCityPickerDialog?.mProvince = mUserInfo?.liveProvince
            mCityPickerDialog?.mCity = mUserInfo?.liveCity
            mCityPickerDialog?.mArea = mUserInfo?.liveDistrict
        } else {
            mCityPickerDialog?.mProvince = mProvinceInfo?.name
            mCityPickerDialog?.mCity = mCityInfo?.name
            mCityPickerDialog?.mArea = mAreaInfo?.name
        }


        return mCityPickerDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mClAvatar -> {
                PictureSelectorUtil.selectMedia(this,false,true,true,1)
            }
            R.id.mClEditUserName -> {
                EditUserNameActivity.intentStart(this)
            }
            R.id.mClWorkYears -> {
                getWorkingYearsPickerDialog()?.show()
            }
            R.id.mClStudent -> {
                mToggleStudent.isChecked = !mToggleStudent.isChecked
            }
            R.id.mClHeight -> {
                getHeightPickerDialog()?.show()
            }
            R.id.mClWeight -> {
                getWeightPickerDialog()?.show()
            }
            R.id.mClCity -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mClEmergencyContact -> {
                EditEmergencyContactActivity.intentStart(this)
            }
            R.id.mClInviteId -> {
                if (TextUtils.isEmpty(mUserInfo?.inviterUserId)) {
                    EditInviteIdActivity.intentStart(this)
                }
            }
        }
    }

    fun uploadImgae2Oss () {
        if(uploadConfigReq == null) {
            sendUploadConfigRequest()
            return
        }
        if (selectList == null || selectList.size == 0) {
            ToastUtils.show("请选择图片")
            return
        }
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.show(R.string.network_error)
            return
        }

        val uploadData = UploadData()
        uploadData.localMedia = selectList.get(0)
        uploadData.dir = uploadConfigReq?.data?.modelMap?.get("headpic")?.dir
        uploadData.bucketName = uploadConfigReq?.data?.modelMap?.get("headpic")?.bucket
        uploadData.ossEndPoint = uploadConfigReq?.data?.modelMap?.get("headpic")?.endpoint

        mLoadingDialog?.show()

        OssUploadModule.instance.upload(uploadData,this)
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
                Loger.e(BaseFragment.TAG,"url = $url")
                mUserInfo?.headpic = url
                sendUpdateAvatarRequest(url)
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    override fun OnWorkingYearsSelect(position: Int, years: String) {
        mTvWorkingYears.text = years
        mUserInfo?.workYears = years
        sendUpdateWorkYearsRequest(years)
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return

        mProvinceInfo = province
        mCityInfo = city
        mAreaInfo = area
        sendUpdateCityRequest()
    }

    override fun OnHeightSelect(position: Int, height: String) {
        mTvHeight.text = "${height}cm"
        mUserInfo?.height = height.toInt()
        sendUpdateHeightRequest(height.toInt())
    }

    override fun OnWeightSelect(position: Int, weight: String) {
        mTvWeight.text = "${weight}kg"
        mUserInfo?.weight = weight.toInt()
        sendUpdateWeightRequest(weight.toInt())
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleStudent -> {
                if (isInIt) return
                sendUpdateIdentityRequest()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }
}