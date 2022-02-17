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
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnAreaPickerListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.view.dialog.AreaPickerDialog
import com.flash.worker.lib.common.interfaces.OnResumeSelectListener
import com.flash.worker.lib.common.etfilter.PointLengthFilter
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.module.business.view.adapter.ServiceAreaAdapter
import com.flash.worker.lib.common.view.dialog.MyResumeDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SaveTalentReleaseParm
import com.flash.worker.lib.coremodel.data.parm.UpdateTalentReleaseParm
import com.flash.worker.lib.coremodel.data.req.TalentReleaseDetailReq
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_talent_update_release.*
import java.net.URLEncoder

class TalentUpdateReleaseActivity : BaseActivity(),View.OnClickListener,
    OnCityPickerListener, OnAreaPickerListener, AdapterView.OnItemClickListener,
    OnResumeSelectListener,CompoundButton.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null
    var mCityPickerDialog: CityPickerDialog? = null

    var provinceData: List<ProvinceInfo>? = null
    var areaData: ArrayList<AreaInfo>? = null

    var mCityInfo: CityInfo? = null

    var workProvince: String? = null
    var workCity: String? = null
    var workDistrict: String? = null
    var jobCategoryId: String? = null
    var resumeId: String? = null
    var resumeName: String? = null

    var mServiceAreaAdapter: ServiceAreaAdapter? = null

    var mStatus: Int = 0//接活发布状态,1,编辑中；2,发布中；3，已下架；4，已驳回

    var mTalentReleaseInfo: TalentReleaseInfo? = null

    var mCity: String? = null

    var selectArea: String? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: TalentReleaseInfo?, status: Int) {
            var intent = Intent(activity, TalentUpdateReleaseActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.STATUS_KEY,status)
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

    override fun getLayoutResource() = R.layout.activity_talent_update_release

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
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
    }

    fun initData (intent: Intent?) {
        provinceData = App.get().getCityData()

        mTalentReleaseInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as TalentReleaseInfo?
        mStatus = intent?.getIntExtra(Constant.STATUS_KEY,0) ?: 0

        sendTalentReleaseDetailRequest()
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

        talentReleaseVM.saveTalentDraftsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showSaveSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.saveTalentReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(JobActions.REFRESH_TALENT_RELEASE)
                    showReleaseSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.updateTalentDraftsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showSaveSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.updateTalentReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showReleaseSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentReleaseVM.talentReleaseDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showTalentReleaseDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commonVM.cityAreaData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    areaData = it.value.data
                    showAreaPickerDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userVM.checkTalentBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    if (it.value.data?.status!!) {
                        NewResumeActivity.intentStart(this)
                    } else {
                        TalentBasicActivity.intentStart(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.TALENT_TYPE)
                .observe(this, Observer {
                    var mTalentCellInfo = it as TalentCellInfo
                    jobCategoryId = mTalentCellInfo.id
                    mTvTalentType.text = mTalentCellInfo?.name
                })
    }

    fun sendTalentReleaseDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        mLoadingDialog?.show()
        talentReleaseVM.fetchTalentReleaseDetail(token,mTalentReleaseInfo?.id)
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendCityAreaRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        commonVM.fetchAreaTreeData(token,URLEncoder.encode(mCity,"UTF-8"))
    }

    fun sendCheckTalentBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkTalentBaseInfo(token)
    }

    fun sendUpdateTalentJobRequest (isRelease: Boolean) {
        var talentType = mTvTalentType.text.toString()
        if (TextUtils.isEmpty(talentType)) {
            ToastUtils.show("请选择人才类型")
            return
        }

        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入技能方向")
            return
        }

        if (isRelease && !mToggleDoAtHome.isChecked) {
            if (TextUtils.isEmpty(workProvince) || TextUtils.isEmpty(workCity)) {
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

        if (isRelease) {
            if (TextUtils.isEmpty(resumeId) || TextUtils.isEmpty(resumeName)) {
                ToastUtils.show("请选择简历")
                return
            }
        }

        val userInfo = App.get().getUserInfo()
        if (isRelease && userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = UpdateTalentReleaseParm()
        body.title = title
        body.isAtHome = mToggleDoAtHome.isChecked
        body.resumeId = resumeId
        if (!TextUtils.isEmpty(price)) {
            body.price = price.toDouble()
        }

        if (!mToggleDoAtHome.isChecked) {
            body.workProvince = workProvince
            body.workCity = workCity
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

        body.isOpenContactPhone = mTogglePublicTel.isChecked
        if (mTogglePublicTel.isChecked) {
            body.contactPhone = tel
        }

        body.jobCategoryId = jobCategoryId
        body.id = mTalentReleaseInfo?.id

        mLoadingDialog?.show()

        if (isRelease) {
            talentReleaseVM.updateTalentRelease(token,body)
        } else {
            talentReleaseVM.updateTalentDrafts(token,body)
        }

    }

    fun sendSaveTalentJobRequest (isRelease: Boolean) {
        var talentType = mTvTalentType.text.toString()
        if (TextUtils.isEmpty(talentType)) {
            ToastUtils.show("请选择人才类型")
            return
        }

        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入技能方向")
            return
        }

        if (isRelease && !mToggleDoAtHome.isChecked) {
            if (TextUtils.isEmpty(workProvince) || TextUtils.isEmpty(workCity)) {
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

        if (isRelease) {
            if (TextUtils.isEmpty(resumeId) || TextUtils.isEmpty(resumeName)) {
                ToastUtils.show("请选择简历")
                return
            }
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
        body.resumeId = resumeId
        if (!TextUtils.isEmpty(price)) {
            body.price = price.toDouble()
        }
        if (!mToggleDoAtHome.isChecked) {
            body.workProvince = workProvince
            body.workCity = workCity
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

        body.isOpenContactPhone = mTogglePublicTel.isChecked
        if (mTogglePublicTel.isChecked) {
            body.contactPhone = tel
        }

        body.jobCategoryId = jobCategoryId

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

    fun showTalentReleaseDetail(data: TalentReleaseDetailReq?) {
        mCity = data?.data?.workCity?: ""
        //编辑中
        if (mStatus != 2) {
            mTvSave.visibility = View.VISIBLE
        } else {
            mTvSave.visibility = View.GONE
        }

        jobCategoryId = data?.data?.jobCategoryId
        resumeId = data?.data?.resumeId
        resumeName = data?.data?.resumeName

        workProvince = data?.data?.workProvince ?: ""
        workCity = data?.data?.workCity?: ""
        workDistrict = data?.data?.workDistrict?: ""

        mTvTalentType.text = data?.data?.jobCategoryName
        mEtTitle.setText(data?.data?.title)

        var isAtHome = data?.data?.isAtHome ?: false
        mToggleDoAtHome.isChecked = isAtHome

        if (!isAtHome) {
            mTvServiceCity.text = workProvince + workCity
        }

        mServiceAreaAdapter?.clear()
        var areaList = getAreaList(data?.data?.workDistrict)
        if (areaList.size > 1) {
            selectArea = areaList.get(areaList.size -1).name
            if (TextUtils.isEmpty(selectArea)) {
                selectArea = areaList.get(areaList.size -2).name
            }
            mServiceAreaAdapter?.add(areaList)
        } else if (areaList.size == 1) {
            selectArea = areaList.get(0).name
            mServiceAreaAdapter?.add(areaList)

            if (!TextUtils.equals("全市",selectArea)) {
                mServiceAreaAdapter?.add(AreaInfo())
            }
        } else {
            if (!TextUtils.isEmpty(workProvince) && !TextUtils.isEmpty(workCity)) {
                selectArea = "全市"
                var allCity = AreaInfo()
                allCity.name = "全市"
                mServiceAreaAdapter?.add(allCity)
            } else {
                mServiceAreaAdapter?.add(AreaInfo())
            }
        }

        mServiceAreaAdapter?.notifyDataSetChanged()

        if (data?.data?.price!! > 0) {
            mEtUnitPrice.setText(data?.data?.price.toString())
        }

        mRbHourlySalary.isChecked = data?.data?.settlementMethod == 1
        mRbPieceSalary.isChecked = data?.data?.settlementMethod == 2

        mToggleAccept.isChecked = data?.data?.inviteMethod == 2

        if (!TextUtils.isEmpty(data?.data?.resumeName)) {
            mTvResumeName.setTextValue(" " + data?.data?.resumeName)
        }

        mTogglePublicTel.isChecked = data?.data?.isOpenContactPhone ?: false
        var contactPhone = data.data?.contactPhone
        if (!TextUtils.isEmpty(contactPhone)) {
            mEtTel.setText(contactPhone)
        }
    }

    fun showAuthTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未做身份认证，暂时发布不了岗位哦~"
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@TalentUpdateReleaseActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showAreaPickerDlg () {
        if (TextUtils.isEmpty(workProvince) || TextUtils.isEmpty(workCity)) {
            ToastUtils.show("请选择服务城市")
            return
        }

        var mAreaPickerDialog = AreaPickerDialog(this)
        mAreaPickerDialog.mArea = selectArea
        mAreaPickerDialog.mOnAreaPickerListener = this

        if (mCityInfo == null) {
            if (areaData != null && areaData?.size!! > 0) {
                mAreaPickerDialog.setAreaData(areaData)
                mAreaPickerDialog.show()
            } else {
                sendCityAreaRequest()
            }
        } else {
            mAreaPickerDialog.setAreaData(mCityInfo?.childs)
            mAreaPickerDialog.show()
        }

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

    fun getAreaList(area: String?): ArrayList<AreaInfo> {
        var areaList = ArrayList<AreaInfo>()
        if (!TextUtils.isEmpty(area)) {
            if (area?.contains(",")!!) {
                var areas = area?.split(",")
                for (areaInfo in areas) {
                    var item = AreaInfo()
                    item.name = areaInfo
                    areaList.add(item)
                }
            } else {
                var item = AreaInfo()
                item.name = area
                areaList.add(item)
            }
        }
        return areaList
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {

        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.mOnCityPickerListener = this
            mCityPickerDialog?.showAreaPicker = false
            provinceData = data
        }

        mCityPickerDialog?.mProvince = workProvince
        mCityPickerDialog?.mCity = workCity

        return mCityPickerDialog
    }

    fun getResumeDialog (data: List<MyResumeInfo>?): MyResumeDialog {
        var mMyResumeDialog = MyResumeDialog(this)
        mMyResumeDialog.resumeList = data
        mMyResumeDialog.mOnResumeSelectListener = this

        return mMyResumeDialog
    }

    fun showSaveSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "保存成功！可在操作台-人才-接活发布-编辑中查看"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object :
                OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                TalentReleaseActivity.intentStart(this@TalentUpdateReleaseActivity,2)
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
                TalentReleaseActivity.intentStart(this@TalentUpdateReleaseActivity,0)
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
            R.id.mClTalentType -> {
                TalentTypeActivity.intentStart(this,mTvTalentType.text.toString())
            }
            R.id.mClServiceCity -> {
                if (mToggleDoAtHome.isChecked) return
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mTvResumeName -> {
                sendUserResumeRequest()
            }
            R.id.mTvSave -> {
                if (mStatus == 1) {//编辑中
                    sendUpdateTalentJobRequest(false)
                } else if (mStatus == 3) {//已下架
                    sendSaveTalentJobRequest(false)
                }
            }
            R.id.mTvPublish -> {
                if (mStatus == 1) {//编辑中
                    sendUpdateTalentJobRequest(true)
                } else if (mStatus == 3) {//已下架
                    sendSaveTalentJobRequest(true)
                }
            }
        }
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return

        mTvServiceCity.text = province?.name + city?.name
        if (!TextUtils.equals(workCity,city?.name)) {
            //重新选择
            mServiceAreaAdapter?.clear()
            mServiceAreaAdapter?.add(AreaInfo())
            mServiceAreaAdapter?.notifyDataSetChanged()
        }

        workProvince = province?.name
        workCity = city?.name

        mCityInfo = city
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
            NewResumeActivity.intentStart(this)
            return
        }
        resumeId = data.id
        resumeName = data.name
        mTvResumeName.setTextValue(" " + data?.name)
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

                    var privince = workProvince ?: ""
                    var city = workCity ?: ""
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}