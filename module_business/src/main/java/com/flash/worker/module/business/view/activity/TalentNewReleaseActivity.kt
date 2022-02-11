package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.security.biometrics.build.S
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnAreaPickerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM
import com.flash.worker.lib.coremodel.viewmodel.TalentReleaseVM
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.view.dialog.AreaPickerDialog
import com.flash.worker.lib.common.interfaces.OnResumeSelectListener
import com.flash.worker.lib.common.etfilter.PointLengthFilter
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.module.business.view.adapter.ServiceAreaAdapter
import com.flash.worker.lib.common.view.dialog.MyResumeDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SaveTalentReleaseParm
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_talent_new_release.*

@Route(path = ARouterPath.TalentNewReleaseAct)
class TalentNewReleaseActivity : BaseActivity(),View.OnClickListener,
    OnCityPickerListener, OnAreaPickerListener,AdapterView.OnItemClickListener,
    OnResumeSelectListener,CompoundButton.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null

    var provinceData: List<ProvinceInfo>? = null

    var mProvinceInfo: ProvinceInfo? = null
    var mCityInfo: CityInfo? = null
    var mAreaInfo: AreaInfo? = null
    var mTalentCellInfo: TalentCellInfo? = null
    var mMyResumeInfo: MyResumeInfo? = null
    var selectArea: String? = null

    var mServiceAreaAdapter: ServiceAreaAdapter? = null

    companion object {
        fun intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TalentNewReleaseActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonViewModelFactory(this)
    }

    private val talentReleaseVM: TalentReleaseVM by viewModels {
        InjectorUtils.provideTalentReleaseVMFactory(this)
    }

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_talent_new_release

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

        mServiceAreaAdapter = ServiceAreaAdapter(this,this)
        mRvServiceArea.adapter = mServiceAreaAdapter

        mIvBack.setOnClickListener(this)
        mClTalentType.setOnClickListener(this)
        mClServiceCity.setOnClickListener(this)
        mTvResumeName.setOnClickListener(this)
        mTvSave.setOnClickListener(this)
        mTvPublish.setOnClickListener(this)
        mToggleDoAtHome.setOnCheckedChangeListener(this)
        mTogglePublicTel.setOnCheckedChangeListener(this)

        mEtUnitPrice.filters = arrayOf(PointLengthFilter(5, 2))
//        mEtUnitPrice.filters = arrayOf(DecimalDigitsInputFilter(2))
    }

    fun initData () {
        provinceData = App.get().getCityData()

        mServiceAreaAdapter?.clear()
        mServiceAreaAdapter?.add(AreaInfo())
        mServiceAreaAdapter?.notifyDataSetChanged()
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

        talentReleaseVM.saveTalentDraftsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showSaveSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        talentReleaseVM.saveTalentReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(JobActions.REFRESH_TALENT_RELEASE)
                    showReleaseSuccessDlg()
                    UMengEventModule.report(this, BussinessTalentEvent.talent_release)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        resumeVM.userResumesData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getResumeDialog(it.value.data).show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.checkTalentBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NewResumeActivity.intentStart(this)
                    } else {
                        TalentBasicActivity.intentStart(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.TALENT_TYPE)
                .observe(this, Observer {
                    mTalentCellInfo = it as TalentCellInfo
                    mTvTalentType.text = mTalentCellInfo?.name
                })
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendSaveTalentReleaseRequest (isRelease: Boolean) {
        if (mTalentCellInfo == null) {
            ToastUtils.show("请选择人才类型")
            return
        }

        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入技能方向")
            return
        }

        if (isRelease && !mToggleDoAtHome.isChecked) {
            if (mProvinceInfo == null || mCityInfo == null) {
                ToastUtils.show("请选择服务地区")
                return
            }
        }

        var price = mEtUnitPrice.text.toString()
        if (TextUtils.isEmpty(price) && isRelease) {
            ToastUtils.show("请输入报酬单价")
            return
        }

        var tel = mEtTel.text.toString()
        if (TextUtils.isEmpty(tel) && mTogglePublicTel.isChecked && isRelease) {
            ToastUtils.show("请输入联系方式")
            return
        }

        if (mMyResumeInfo == null && isRelease) {
            ToastUtils.show("请选择简历")
            return
        }

        val userInfo = App.get().getUserInfo()
        if (isRelease && userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = SaveTalentReleaseParm()
        body.title = title
        body.isAtHome = mToggleDoAtHome.isChecked
        body.resumeId = mMyResumeInfo?.id
        if (!TextUtils.isEmpty(price)) {
            body.price = price.toDouble()
        }

        if (!mToggleDoAtHome.isChecked) {
            body.workProvince = mProvinceInfo?.name
            body.workCity = mCityInfo?.name
            body.workDistrict = getServiceArea()
        }

        if (mRbHourlySalary.isChecked) {
            body.settlementMethod = 1
        } else {
            body.settlementMethod = 2
        }

        if (mToggleAccept.isChecked) {
            body.inviteMethod = 2
        } else {
            body.inviteMethod = 1
        }

        if (mTogglePublicTel.isChecked) {
            body.isOpenContactPhone = mTogglePublicTel.isChecked
            body.contactPhone = tel
        }

        body.jobCategoryId = mTalentCellInfo?.id

        mLoadingDialog?.show()
        if (isRelease) {
            talentReleaseVM.saveTalentRelease(token,body)
        } else {
            talentReleaseVM.saveTalentDrafts(token,body)
        }
    }

    fun sendUserResumeRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        resumeVM.fetchUserResumes(token)
    }

    fun sendCheckTalentBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkTalentBaseInfo(token)
    }

    fun getServiceArea (): String? {
        var areas = StringBuffer()
        mServiceAreaAdapter?.getDatas()?.forEach {
            if (!TextUtils.isEmpty(it.name)) {
                if (!TextUtils.equals("全市",it.name)) {
                    areas.append(it.name + ",")
                }
            }
        }
        if (areas.length > 0) {
            return areas.substring(0,areas.length - 1)
        }
        return null
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {

        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.mTitleText = "选择服务城市"
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.mOnCityPickerListener = this
            mCityPickerDialog?.showAreaPicker = false
            provinceData = data
        }

        return mCityPickerDialog
    }

    fun getResumeDialog (data: List<MyResumeInfo>?): MyResumeDialog {
        var mMyResumeDialog = MyResumeDialog(this)
        mMyResumeDialog?.resumeList = data
        mMyResumeDialog?.mOnResumeSelectListener = this

        return mMyResumeDialog
    }

    fun showSaveSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "保存成功！可在操作台-人才-接活发布-编辑中查看"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                TalentReleaseActivity.intentStart(this@TalentNewReleaseActivity,2)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    fun showReleaseSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已成功发布接活信息"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                TalentReleaseActivity.intentStart(this@TalentNewReleaseActivity,0)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    fun showAreaPickerDlg () {
        if (mCityInfo == null) {
            ToastUtils.show("请选择服务城市")
            return
        }

        var mAreaPickerDialog = AreaPickerDialog(this)
        mAreaPickerDialog.setAreaData(mCityInfo?.childs)
        mAreaPickerDialog.mArea = selectArea
        mAreaPickerDialog.mOnAreaPickerListener = this
        mAreaPickerDialog.show()
    }

    fun showBackTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未保存当前填写的内容哟，确定返回吗？"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "保存"
        commonTipDialog.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendSaveTalentReleaseRequest(false)
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }
        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                showBackTipDlg()
            }
            mClTalentType -> {
                TalentTypeActivity.intentStart(this,mTvTalentType.text.toString())
            }
            mClServiceCity -> {
                if (mToggleDoAtHome.isChecked) return
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            mTvResumeName -> {
                sendUserResumeRequest()
            }
            mTvSave -> {
                sendSaveTalentReleaseRequest(false)
            }
            mTvPublish -> {
                sendSaveTalentReleaseRequest(true)
            }
        }
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未进行身份认证，将不能发布信息哟！"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@TalentNewReleaseActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return
        mTvServiceCity.text = province?.name + city?.name
        if (mCityInfo != null) {
            if (mCityInfo?.id != city?.id) {
                //重新选择
                mServiceAreaAdapter?.clear()
                mServiceAreaAdapter?.add(AreaInfo())
                mServiceAreaAdapter?.notifyDataSetChanged()
            }
        }
        mProvinceInfo = province
        mCityInfo = city
        mAreaInfo = area

        Loger.e(TAG,"OnCityPicker-mCityInfo = " + JsonUtils.toJSONString(mCityInfo))
    }

    override fun OnAreaPicker(area: AreaInfo?) {
        selectArea = area?.name
        if (TextUtils.equals("全市",selectArea)) {
            mServiceAreaAdapter?.clear()
            mServiceAreaAdapter?.add(area)
            mServiceAreaAdapter?.notifyDataSetChanged()
        } else {
            if (hasAddArea("全市")) {
                mServiceAreaAdapter?.clear()
                mServiceAreaAdapter?.add(area)
                mServiceAreaAdapter?.add(AreaInfo())
                mServiceAreaAdapter?.notifyDataSetChanged()
            } else {
                if (!hasAddArea(area?.name)) {
                    var count = mServiceAreaAdapter?.getContentItemCount() ?: 0
                    mServiceAreaAdapter?.removeItem(count - 1)
                    mServiceAreaAdapter?.add(area)
                    mServiceAreaAdapter?.add(AreaInfo())
                    mServiceAreaAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun hasAddArea(areaName: String?): Boolean {
        mServiceAreaAdapter?.getDatas()?.forEach {
            if (TextUtils.equals(it.name,areaName)) {
                return true
            }
        }
        return false
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mTvServiceArea -> {
                var itemCount = mServiceAreaAdapter?.getContentItemCount() ?: 0

                if (position == itemCount - 1) {
                    if (TextUtils.isEmpty(mServiceAreaAdapter?.getItem(position)?.name)) {
                        if (mServiceAreaAdapter?.getContentItemCount() == 6) {
                            ToastUtils.show("服务地区最多5个")
                            return
                        }
                        showAreaPickerDlg()
                    } else {
                        mServiceAreaAdapter?.removeItem(position)
                        mServiceAreaAdapter?.add(AreaInfo())
                        mServiceAreaAdapter?.notifyDataSetChanged()
                    }
                } else {
                    mServiceAreaAdapter?.removeItem(position)
                    mServiceAreaAdapter?.notifyItemRemoved(position)
                }
            }
        }
    }

    override fun OnResumeSelect(data: MyResumeInfo?, resumeCount: Int) {
        if (data == null) {
            if (resumeCount >= 5) {
                ToastUtils.show("您的简历数已达上限  您可删除后再新增！")
                return
            }
            if (resumeCount == 0) {
                sendCheckTalentBaseInfoRequest()
                return
            }
            sendCheckTalentBaseInfoRequest()
            return
        }
        mMyResumeInfo = data
        mTvResumeName.setTextValue(" " + mMyResumeInfo?.name)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleDoAtHome -> {
                if (isChecked) {
                    mTvServiceCity.text = ""
                    rtv_service_city.setTextValue(" 发布城市")
                    tv_city_tip.visibility = View.GONE
                    mRvServiceArea.visibility = View.GONE
                    mTvServiceCity.hint = "默认全国"
                    TextViewBoundsUtil.setTvDrawableRight(this,mTvServiceCity,0)
                } else {
                    rtv_service_city.setTextValue(" 服务地区")
                    tv_city_tip.visibility = View.VISIBLE
                    mRvServiceArea.visibility = View.VISIBLE
                    mTvServiceCity.hint = "请选择服务地区"
                    TextViewBoundsUtil.setTvDrawableRight(this,mTvServiceCity,R.mipmap.ic_right)

                    var privince = mProvinceInfo?.name ?: ""
                    var city = mCityInfo?.name ?: ""
                    mTvServiceCity.text = privince + city
                }
            }
            R.id.mTogglePublicTel -> {
                if (isChecked) {
                    mClTel.show()
                } else {
                    mClTel.hide()
                }
            }
        }
    }

}