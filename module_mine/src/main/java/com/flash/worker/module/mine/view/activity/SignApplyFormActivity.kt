package com.flash.worker.module.mine.view.activity

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.etfilter.PointLengthFilter
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.LocationHelper
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.ApplyEstablishGuildParm
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildImageAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_sign_apply_form.*

class SignApplyFormActivity : BaseActivity(),View.OnClickListener, OnCityPickerListener,
        AdapterView.OnItemClickListener, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, SignApplyFormActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    var mGuildImageAdapter: GuildImageAdapter? = null
    var mLoadingDialog: LoadingDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null
    var provinceData: List<ProvinceInfo>? = null
    var mProvinceInfo: ProvinceInfo? = null
    var mCityInfo: CityInfo? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var mProvince: String? = App.get().mProvince
    var mCity: String = App.get().mCity

    override fun getLayoutResource() = R.layout.activity_sign_apply_form

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mGuildImageAdapter = GuildImageAdapter(this,this)
        mRvImage.adapter = mGuildImageAdapter

        mIvBack.setOnClickListener(this)
        mClCity.setOnClickListener(this)
        mClRelatedWorkingExperience.setOnClickListener(this)
        mClIntroduction.setOnClickListener(this)
        mClRegulation.setOnClickListener(this)
        mTvApply.setOnClickListener(this)

        mEtAverageIncome.filters = arrayOf(PointLengthFilter(8, 2))
    }

    fun initData () {
        provinceData = App.get().getCityData()

        mGuildImageAdapter?.add(WorkPicInfo())
        mGuildImageAdapter?.notifyDataSetChanged()
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

        guildVM.applyData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showApplySuccessTipDlg()
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

    }

    fun subscribeEvent() {
        LiveDataBus.with(MineActions.UPDATE_WORKING_EXPERIENCE)
                .observe(this, Observer {
                    var data = it.toString()
                    mTvRelatedWorkingExperience.text = data

                    if (TextUtils.isEmpty(data)) {
                        mTvRelatedWorkingExperience.visibility = View.GONE
                        mTvRelatedWorkingExperienceLabel.text = "请输入内容"
                    } else {
                        mTvRelatedWorkingExperience.visibility = View.VISIBLE
                        mTvRelatedWorkingExperienceLabel.text = ""

                    }
                })

        LiveDataBus.with(MineActions.UPDATE_GUILD_INTRODUCTION)
                .observe(this, Observer {
                    var data = it.toString()
                    mTvIntroduction.text = data

                    if (TextUtils.isEmpty(data)) {
                        mTvIntroduction.visibility = View.GONE
                        mTvIntroductionLabel.text = "请输入内容"
                    } else {
                        mTvIntroduction.visibility = View.VISIBLE
                        mTvIntroductionLabel.text = ""

                    }
                })

        LiveDataBus.with(MineActions.UPDATE_GUILD_REGULATION)
                .observe(this, Observer {
                    var data = it.toString()
                    mTvRegulation.text = data

                    if (TextUtils.isEmpty(data)) {
                        mTvRegulation.visibility = View.GONE
                        mTvRegulationLabel.text = "请输入内容"
                    } else {
                        mTvRegulation.visibility = View.VISIBLE
                        mTvRegulationLabel.text = ""
                    }
                })
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendApplyRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        var city = mTvCity.text.toString()
        if (TextUtils.isEmpty(city)) {
            ToastUtils.show("请选择城市")
            return
        }

        if (mProvinceInfo == null || mCityInfo == null) {
            ToastUtils.show("请选择城市")
            return
        }

        var guildName = mEtGuildName.text.toString()
        if (TextUtils.isEmpty(guildName)) {
            ToastUtils.show("请输入公会名称")
            return
        }

        if (guildName.length < 2) {
            ToastUtils.show("公会名称2-7个字")
            return
        }

        var workexperience = mTvRelatedWorkingExperience.text.toString()

        var introduction = mTvIntroduction.text.toString()
        if (TextUtils.isEmpty(introduction)) {
            ToastUtils.show("请输入公会简介")
            return
        }

        var regulation = mTvRegulation.text.toString()

        var peopleCount = mEtPeopleCount.text.toString()

        var averageIncome = mEtAverageIncome.text.toString()

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        val body = ApplyEstablishGuildParm()
        body.guildProvince = mProvinceInfo?.name
        body.guildCity = mCityInfo?.name
        body.guildName = guildName
        if (!TextUtils.isEmpty(peopleCount)) {
            body.developmentPeopleNum = peopleCount.toInt()
        }
        if (!TextUtils.isEmpty(averageIncome)) {
            body.memberMonthIncomeAmount = AmountUtil.getRoundUpDouble(averageIncome.toDouble(),2)
        }
        body.guildProfile = introduction
        if (!TextUtils.isEmpty(workexperience)) {
            body.workExperience = workexperience
        }
        if (!TextUtils.isEmpty(regulation)) {
            body.guildRules = regulation
        }

        body.pics = getGuildPics()

        guildVM.apply(token,body)
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {
        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.mTitleText = "选择城市"
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.showAreaPicker = false
            mCityPickerDialog?.mOnCityPickerListener = this
            mCityPickerDialog?.mProvince = mProvince
            mCityPickerDialog?.mCity = mCity
            provinceData = data
        }

        return mCityPickerDialog
    }

    fun showApplySuccessTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的用户：" +
                "\n\t\t\t\t您的申请已提交，我们将在3—7个工作日内进行审核，请耐心等待！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "好的"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mClCity -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mClRelatedWorkingExperience -> {
                var workExperience = mTvRelatedWorkingExperience.text.toString()
                FillGuildWorkExperienceActivity.intentStart(this,workExperience)
            }
            R.id.mClIntroduction -> {
                var introduction = mTvIntroduction.text.toString()
                FillGuildIntroductionActivity.intentStart(this,introduction)
            }
            R.id.mClRegulation -> {
                var regulation = mTvRegulation.text.toString()
                FillGuildRegulationActivity.intentStart(this,regulation)
            }
            R.id.mTvApply -> {
                sendApplyRequest()
            }

        }
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return
        mProvinceInfo = province
        mCityInfo = city
        mTvCity.text = province?.name + city?.name
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var count = mGuildImageAdapter?.itemCount ?: 0
        when (view?.id) {
            R.id.mClCertificateRoot -> {
                if (position == count - 1) {
                    var maxCount = 9 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加8张")
                        return
                    }

                    PictureSelectorUtil.selectMedia(this,false,true,false,maxCount)
                }
            }
            R.id.mIvRelatedCertificateDel -> {
                mGuildImageAdapter?.removeItem(position)

                var lastPosition = count - 1
                var isAddIcon = TextUtils.isEmpty(mGuildImageAdapter?.getItem(lastPosition)?.pic)

                if (!isAddIcon && count < 8) {
                    mGuildImageAdapter?.add(WorkPicInfo())
                }
                mGuildImageAdapter?.notifyItemRemoved(position)
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

                var data = WorkPicInfo()
                data.pic = url
                var count = mGuildImageAdapter?.itemCount ?: 0
                mGuildImageAdapter?.removeItem(count - 1)
                mGuildImageAdapter?.add(data)
                if (count < 8) {
                    mGuildImageAdapter?.add(WorkPicInfo())
                }
                mGuildImageAdapter?.notifyDataSetChanged()

                if (selectList != null && selectList.size > 1) {
                    selectList.removeAt(0)
                    uploadImgae2Oss()
                }
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

    fun getGuildPics(): List<String>? {
        var guildPics = ArrayList<String>()
        var count = mGuildImageAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mGuildImageAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    it.pic?.let { it1 -> guildPics.add(it1) }
                }
            }
        }
        if (guildPics.size == 0) return null
        return guildPics
    }

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }
}