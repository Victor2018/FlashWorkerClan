package com.flash.worker.module.business.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.SettlementInfo
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.module.business.R
import com.flash.worker.lib.common.etfilter.PointLengthFilter
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SaveEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.req.EmployerReleaseDetailReq
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.common.view.adapter.BusinessWorkPicAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.HireActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.interfaces.*
import com.flash.worker.module.business.view.dialog.*
import kotlinx.android.synthetic.main.activity_hire_update_release.*
import kotlinx.android.synthetic.main.hire_release_content.*

class HireUpdateReleaseActivity : BaseActivity(), View.OnClickListener, OnDatePickListener,
    RadioGroup.OnCheckedChangeListener, OnSettlementMethodSelectListener,OnTimePickerListener,
    CompoundButton.OnCheckedChangeListener, OnCityPickerListener, OnEmployerSelectListener,
    AdapterView.OnItemClickListener, OnUploadListener, OnGenderSelectListener, OnAgeSelectListener,
    OnEducationSelectListener ,OnWorkingHoursSelectListener,
    OnIdentitySelectListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: EmployerReleaseInfo?, status: Int) {
            var intent = Intent(activity, HireUpdateReleaseActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.STATUS_KEY,status)
            activity.startActivity(intent)
        }
    }

    var mIdentityRequirementDialog: IdentityRequirementDialog? = null
    var mGenderRequirementDialog: GenderRequirementDialog? = null
    var mEducationPickerDialog: EducationPickerDialog? = null
    var mAgeRequirementDialog: AgeRequirementDialog? = null
    var mTalentCellInfo: TalentCellInfo? = null
    var mLoadingDialog: LoadingDialog? = null

    var provinceData: List<ProvinceInfo>? = null
    var mProvinceInfo: ProvinceInfo? = null
    var mCityInfo: CityInfo? = null
    var mAreaInfo: AreaInfo? = null
    var mSettlementMethod: Int? = null
    var sexRequirement: Int = 2
    var educationRequirement: String? = null
    var ageRequirement: String? = null
    var mEmployerInfo: EmployerInfo? = null

    var mBusinessWorkPicAdapter: BusinessWorkPicAdapter? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var mStatus: Int = 0//雇佣发布状态,1,编辑中；2,发布中；3，已下架；4，已驳回

    var mEmployerReleaseInfo: EmployerReleaseInfo? = null

    var mSaveEmployerReleaseParm: SaveEmployerReleaseParm? = null

    var isInit: Boolean = true

    var selectDateMode: Int = 0//选择时间模式，0，开始时间；1，结束时间；2截止时间

    var mCurrentTime: Long = 0
    var releaseType: Int = 1//发布类型 1-普通发布；2-紧急发布

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonViewModelFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    private val employerReleaseVM: EmployerReleaseVM by viewModels {
        InjectorUtils.provideEmployerReleaseVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_hire_update_release

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

        mBusinessWorkPicAdapter = BusinessWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mBusinessWorkPicAdapter

        mIvBack.setOnClickListener(this)
        mTvEmployer.setOnClickListener(this)
        mTvTalentType.setOnClickListener(this)
        mTvWorkArea.setOnClickListener(this)

        mTvStartDate.setOnClickListener(this)
        mTvEndDate.setOnClickListener(this)
        mTvStartTime.setOnClickListener(this)
        mTvEndTime.setOnClickListener(this)
        mIvWorkTimeNotice.setOnClickListener(this)
        mClPaidHour.setOnClickListener(this)
        mClSettlementMethod.setOnClickListener(this)
        mClRequireIdentity.setOnClickListener(this)
        mClAgeRequirement.setOnClickListener(this)
        mClEduRequirement.setOnClickListener(this)
        mClSexRequirement.setOnClickListener(this)
        mTvSave.setOnClickListener(this)
        mTvRelease.setOnClickListener(this)

        mToggleDoAtHome.setOnCheckedChangeListener(this)
        mRgSalary.setOnCheckedChangeListener(this)

        mEtUnitPrice.filters = arrayOf(PointLengthFilter(5, 2))

        var filter = MoneyInputFilter()
        filter.setMaxValue(100.0)
        filter.setDecimalLength(0)

        mEtEmploymentCount.filters = arrayOf(filter)

        mEtDescription.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                mTvDescriptionCount.text = "${s?.length}/200"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mTvPaidHour.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (isInit) return
                calcuAmount()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mEtUnitPrice.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (isInit) return
                calcuAmount()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    fun initData (intent: Intent?) {
        provinceData = App.get().getCityData()

        mBusinessWorkPicAdapter?.add(WorkPicInfo())
        mBusinessWorkPicAdapter?.notifyDataSetChanged()

        mEmployerReleaseInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployerReleaseInfo?
        mStatus = intent?.getIntExtra(Constant.STATUS_KEY,0) ?: 0

        sendUploadConfigRequest()

        sendEmployerReleaseDetailRequest()
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
                    uploadImgae2Oss()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.saveEmployerDraftsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when (it) {
                is HttpResult.Success -> {
                    showSaveSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.saveEmployerReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when (it) {
                is HttpResult.Success -> {
                    showReleaseSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.updateEmployerDraftsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when (it) {
                is HttpResult.Success -> {
                    showSaveSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
        employerReleaseVM.updateEmployerReleaseData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when (it) {
                is HttpResult.Success -> {
                    LiveDataBus.send(HireActions.REFRESH_HIRE_RELEASE)
                    showReleaseSuccessDlg()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerVM.employersData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getMyEmployerDialog(it.value.data).show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerReleaseVM.employerReleaseDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showEmployerReleaseDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commonVM.systemTimeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showDateDialog(it.value.data?.currentTime ?: 0)
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

    fun sendSystemTimeRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        commonVM.fetchSystemTime(token)
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendEmployersRequest () {
        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employerVM.getEmployers(token)
    }

    fun sendEmployerReleaseDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        mLoadingDialog?.show()
        employerReleaseVM.fetchEmployerReleaseDetail(token,mEmployerReleaseInfo?.id)
    }

    fun sendSaveEmployerReleaseRequest (isRelease: Boolean) {
        var employerName = mTvEmployer.text.toString()
        if (TextUtils.isEmpty(employerName) && isRelease) {
            ToastUtils.show("请选择雇主信息")
            return
        }
        var talentType = mTvTalentType.text.toString()
        if (TextUtils.isEmpty(talentType)) {
            ToastUtils.show("请选择雇用的人才类型")
            return
        }
        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入岗位名称")
            return
        }
        var employmentCount = mEtEmploymentCount.text.toString()
        if (TextUtils.isEmpty(employmentCount) && isRelease) {
            ToastUtils.show("请输入需要雇用的人数")
            return
        }
        var workArea = mTvWorkArea.text.toString()
        if (TextUtils.isEmpty(workArea) && isRelease && !mToggleDoAtHome.isChecked) {
            ToastUtils.show("请选择服务区域")
            return
        }
        var address = mEtAddress.text.toString()
        var jobStartDate = mTvStartDate.text.toString()
        if (TextUtils.isEmpty(jobStartDate) && isRelease) {
            ToastUtils.show("请选择开始日期")
            return
        }
        var jobEndDate = mTvEndDate.text.toString()
        if (TextUtils.isEmpty(jobEndDate) && isRelease) {
            ToastUtils.show("请选择结束日期")
            return
        }

        var startTime = mTvStartTime.text.toString()
        if (TextUtils.isEmpty(startTime) && isRelease) {
            ToastUtils.show("请选择日开工时间")
            return
        }
        var endTime = mTvEndTime.text.toString()
        if (TextUtils.isEmpty(endTime) && isRelease) {
            ToastUtils.show("请选择日完工时间")
            return
        }

        var paidHour = mTvPaidHour.text.toString().replace("小时","")
        if (TextUtils.isEmpty(paidHour) && isRelease) {
            ToastUtils.show("请输入日工作时长")
            return
        }
        var unitPrice = mEtUnitPrice.text.toString()

        if (isRelease) {
            if (TextUtils.isEmpty(unitPrice)) {
                ToastUtils.show("请输入您的报酬单价")
                return
            } else {
                val price = AmountUtil.getRoundUpDouble(unitPrice.toDouble(),2)
                if (price <= 0.0) {
                    ToastUtils.show("报酬单价必须大于0")
                    return
                }
            }
        }

        if (mSettlementMethod == 0 && isRelease) {
            ToastUtils.show("请选择结算方式")
            return
        }

        var settlementAmount = mTvSettlementAmount.text.toString().replace(",","")

        var dailySalary = mTvDailySalary.text.toString().replace(",","")
        var dayTotalPrice = mTvDayTotalPrice.text.toString().replace(",","")
        var totalPieceSalary = mTvPieceTotalSalary.text.toString().replace(",","")


        val userInfo = App.get().getUserInfo()
        if (isRelease && userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        if (mEmployerInfo != null) {
            mSaveEmployerReleaseParm?.employerId = mEmployerInfo?.id
        }

        if (mTalentCellInfo != null) {
            mSaveEmployerReleaseParm?.jobCategoryId = mTalentCellInfo?.id
        }

        if (mToggleEmergencyRelease.isChecked) {
            mSaveEmployerReleaseParm?.type = 2
        } else {
            mSaveEmployerReleaseParm?.type = 1
        }

        mSaveEmployerReleaseParm?.sexRequirement = sexRequirement
        mSaveEmployerReleaseParm?.ageRequirement = ageRequirement
        mSaveEmployerReleaseParm?.eduRequirement = educationRequirement
        mSaveEmployerReleaseParm?.title = title
        if (!TextUtils.isEmpty(employmentCount)) {
            mSaveEmployerReleaseParm?.peopleCount = employmentCount.toInt()
        } else {
            mSaveEmployerReleaseParm?.peopleCount = null
        }

        mSaveEmployerReleaseParm?.workProvince = mProvinceInfo?.name
        mSaveEmployerReleaseParm?.workCity= mCityInfo?.name
        mSaveEmployerReleaseParm?.workDistrict= mAreaInfo?.name

        mSaveEmployerReleaseParm?.address = address
        mSaveEmployerReleaseParm?.isAtHome = mToggleDoAtHome.isChecked
        mSaveEmployerReleaseParm?.jobStartTime = jobStartDate
        mSaveEmployerReleaseParm?.jobEndTime = jobEndDate
        mSaveEmployerReleaseParm?.startTime = startTime
        if (!TextUtils.isEmpty(endTime)) {
            mSaveEmployerReleaseParm?.endTime = endTime.replace("次日","")
        }


        var totalDays = DateUtil.getDiffDay(jobStartDate,jobEndDate)
        if (totalDays > 0) {
            mSaveEmployerReleaseParm?.totalDays = DateUtil.getDiffDay(jobStartDate,jobEndDate)
        }

        var isBeforeStartTime = DateUtil.isNextDayDate(startTime,endTime
            .replace("次日",""),"HH:mm")
        if (isBeforeStartTime) {
            mSaveEmployerReleaseParm?.shiftType = 2
        } else {
            mSaveEmployerReleaseParm?.shiftType = 1
        }
        if (!TextUtils.isEmpty(paidHour)) {
            mSaveEmployerReleaseParm?.paidHour = paidHour.toDouble()
        }
        if (!TextUtils.isEmpty(unitPrice)) {
            mSaveEmployerReleaseParm?.price = AmountUtil.getRoundUpDouble(unitPrice.toDouble(),2)
        } else {
            mSaveEmployerReleaseParm?.price = null
        }
        if (mRbHourlySalary.isChecked) {
            mSaveEmployerReleaseParm?.payrollMethod = 1
            if (!TextUtils.isEmpty(dayTotalPrice)) {
                val totalAmount = AmountUtil.getRoundUpDouble(dayTotalPrice.toDouble(),2)
                if (totalAmount > 0) {
                    mSaveEmployerReleaseParm?.totalAmount = totalAmount
                }
            }
        } else {
            mSaveEmployerReleaseParm?.payrollMethod = 2
            if (!TextUtils.isEmpty(totalPieceSalary)) {
                val totalAmount = AmountUtil.getRoundUpDouble(totalPieceSalary.toDouble(),2)
                if (totalAmount > 0) {
                    mSaveEmployerReleaseParm?.totalAmount = totalAmount
                }
            }
        }
        if (!TextUtils.isEmpty(dailySalary)) {
            val dailySalary = AmountUtil.getRoundUpDouble(dailySalary.toDouble(),2)
            if (dailySalary > 0) {
                mSaveEmployerReleaseParm?.dailySalary = dailySalary
            }
        }

        mSaveEmployerReleaseParm?.settlementMethod = mSettlementMethod
        if (!TextUtils.isEmpty(settlementAmount)) {
            val settlementAmount = AmountUtil.getRoundUpDouble(settlementAmount.toDouble(),2)
            if (settlementAmount > 0) {
                mSaveEmployerReleaseParm?.settlementAmount = settlementAmount
            }
        }
        mSaveEmployerReleaseParm?.pics = getWorkPics()
        mSaveEmployerReleaseParm?.workDescription = mEtDescription.text.toString()

        if (mStatus == 1) {//编辑中
            if (isRelease) {
                employerReleaseVM.updateEmployerRelease(token,mSaveEmployerReleaseParm)
            } else {
                employerReleaseVM.updateEmployerDrafts(token,mSaveEmployerReleaseParm)
            }
        } else {
            if (isRelease) {
                employerReleaseVM.saveEmployerRelease(token,mSaveEmployerReleaseParm)
            } else {
                employerReleaseVM.saveEmployerDrafts(token,mSaveEmployerReleaseParm)
            }
        }
    }

    fun showEmployerReleaseDetail (data: EmployerReleaseDetailReq) {
        isInit = true
        mSaveEmployerReleaseParm = data.data

        releaseType = data.data?.type ?: 0

        mProvinceInfo = ProvinceInfo()
        mProvinceInfo?.name = data.data?.workProvince
        mCityInfo = CityInfo()
        mCityInfo?.name = data.data?.workCity
        mAreaInfo = AreaInfo()
        mAreaInfo?.name = data.data?.workDistrict

        if (data?.data?.settlementMethod != null) {
            mSettlementMethod = data?.data?.settlementMethod ?: 0
        }
        if (data?.data?.sexRequirement != null) {
            sexRequirement = data?.data?.sexRequirement ?: 0
        }
        if (data?.data?.eduRequirement != null) {
            educationRequirement = data?.data?.eduRequirement
        }
        if (data?.data?.ageRequirement != null) {
            ageRequirement = data?.data?.ageRequirement
        }

        setSettlementMethodUi()

        mTvEmployer.text = data.data?.employerName
        mTvTalentType.text = data.data?.jobCategoryName
        mEtTitle.setText(data.data?.title)
        mEtEmploymentCount.setText(data.data?.peopleCount.toString())

        if (TextUtils.isEmpty(data.data?.workCity) || TextUtils.isEmpty(data.data?.workDistrict)) {
            mTvWorkArea.text = ""
        } else {
            mTvWorkArea.text = data.data?.workCity + data.data?.workDistrict
        }
        mEtAddress.setText(data.data?.address)

        mToggleEmergencyRelease.isChecked = mSaveEmployerReleaseParm?.type == 2

        if (data.data?.identityRequirement == 1) {
            mTvRequireIdentity.text = "非学生"
        } else if (data.data?.identityRequirement == 2) {
            mTvRequireIdentity.text = "学生"
        } else if (data.data?.identityRequirement == 3) {
            mTvRequireIdentity.text = "不限"
        }

        mToggleDoAtHome.isChecked = data.data?.isAtHome ?: false

        var chectSartTime = data.data?.jobStartTime
        if (mSaveEmployerReleaseParm?.type == 1) {
            chectSartTime =  DateUtil.getOldFetureDate(chectSartTime,-2,"yyyy.MM.dd")
        }
        if (!DateUtil.isBeforeTodayDate(chectSartTime,"yyyy.MM.dd")) {
            mTvStartDate.text = data.data?.jobStartTime
        }

        var chectEndTime = data.data?.jobEndTime
        if (mSaveEmployerReleaseParm?.type == 1) {
            chectEndTime =  DateUtil.getOldFetureDate(chectEndTime,-2,"yyyy.MM.dd")
        }

        if (!DateUtil.isBeforeTodayDate(chectEndTime,"yyyy.MM.dd")) {
            mTvEndDate.text = data.data?.jobEndTime
        }

        mTvStartTime.text = data.data?.startTime

        var isBeforeStartTime = DateUtil.isNextDayDate(data.data?.startTime,data.data?.endTime,"HH:mm")
        if (isBeforeStartTime) {
            mTvEndTime.text = "次日" + data.data?.endTime
        } else {
            mTvEndTime.text = data.data?.endTime
        }

        var totalDays = data.data?.totalDays ?: 0
        if (totalDays > 0) {
            mTvDayCount.text = data.data?.totalDays.toString()
        }

        val paidHour = data.data?.paidHour ?: 0.0
        if (paidHour > 0) {
            mTvPaidHour.text = AmountUtil.getRoundUp(paidHour,1) + "小时"
        }

        mRbHourlySalary.isChecked = data.data?.payrollMethod == 1
        mRbPieceSalary.isChecked = data.data?.payrollMethod == 2

        mEtUnitPrice.setText(data.data?.price.toString())

        mTvDailySalary.text = AmountUtil.getRoundUpString(data.data?.dailySalary,2)

        val dailySalary = data.data?.dailySalary ?: 0.0
        if (dailySalary > 0 && totalDays > 0) {
            var dayTotalPrice = dailySalary * totalDays
            mTvDayTotalPrice.text = AmountUtil.getRoundUpString(dayTotalPrice,2)
        }

        mTvPieceTotalSalary.setText(AmountUtil.getRoundUpString(data.data?.totalAmount,2))
        
        if (data.data?.settlementMethod == 1) {
            mTvSettlementMethod.text = "日结"
        } else if (data.data?.settlementMethod == 2) {
            mTvSettlementMethod.text = "周结"
        } else if (data.data?.settlementMethod == 3) {
            mTvSettlementMethod.text = "整单结"
        }

        mTvSettlementAmount.text = AmountUtil.getRoundUpString(data.data?.settlementAmount,2)

        if (data.data?.sexRequirement == 0) {
            mTvSexRequirement.text = "女"
        } else if (data.data?.sexRequirement == 1) {
            mTvSexRequirement.text = "男"
        } else if (data.data?.sexRequirement == 2) {
            mTvSexRequirement.text = "不限"
        }

        var ageRequirement = data.data?.ageRequirement ?: "不限"
        mTvAgeRequirement.text = ageRequirement

        var eduRequirement = data.data?.eduRequirement ?: "不限"
        mTvEduRequirement.text = eduRequirement

        mBusinessWorkPicAdapter?.clear()
        mBusinessWorkPicAdapter?.add(getPics(data.data?.pics))

        var picCount = data.data?.pics?.size ?: 0
        if (picCount < 3) {
            mBusinessWorkPicAdapter?.add(WorkPicInfo())
        }
        mBusinessWorkPicAdapter?.notifyDataSetChanged()

        if (!TextUtils.isEmpty(data.data?.workDescription)) {
            mEtDescription.setText(data.data?.workDescription)
        }

        isInit = false
    }

    fun getPics (pics: List<String>?): List<WorkPicInfo> {
        var datas = ArrayList<WorkPicInfo>()
        if (pics == null) return datas
        if (pics.size == 0) return datas

        for (url in pics) {
            if (!TextUtils.isEmpty(url)) {
                var item = WorkPicInfo()
                item.pic = url
                datas.add(item)
            }
        }
        return datas
    }

    fun showSaveSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "成功保存，可在操作台-雇主-雇用发布-编辑中查看"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                HireReleaseActivity.intentStart(this@HireUpdateReleaseActivity,2)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    /**
     * 计算默认录取截止时间
     * 普通发布时，雇用截止时间默认为开工时间前12小时
     * 紧急发布时，雇用截止时间默认为开工时间前2小时
     */
    fun cacuDeadlineDate () {
        Loger.e(TAG,"cacuDeadlineDate()......")
        var startDate = mTvStartDate.text.toString()
        var startTime = mTvStartTime.text.toString()

        if (TextUtils.isEmpty(startDate)) {
            Loger.e(TAG,"cacuDeadlineDate()......startDate is empty")
            return
        }
        if (TextUtils.isEmpty(startTime)) {
            Loger.e(TAG,"cacuDeadlineDate()......startTime is empty")
            return
        }
    }

    /**
     * 计算日薪、合计、结算金额
     */
    fun calcuAmount () {
        var settlementPieceCount = 1

        var unitPrice = mEtUnitPrice.text.toString()
        var paidHour = mTvPaidHour.text.toString().replace("小时","")
        var dayCount = mTvDayCount.text.toString()

        //计算件薪
        if (!TextUtils.isEmpty(unitPrice)) {
            if (!TextUtils.equals("null",unitPrice)) {
                if (unitPrice != null && settlementPieceCount != null) {
                    var total = unitPrice.toDouble() * settlementPieceCount
                    mTvPieceTotalSalary.text = AmountUtil.getRoundUpString(total,2)
                }
            }
        }

        if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
            //计算日薪
            var dayPrice = unitPrice.toDouble() * paidHour.toDouble()
            mTvDailySalary.text = AmountUtil.getRoundUpString(dayPrice,2)
            if (!TextUtils.isEmpty(dayCount)) {
                //计算合计
                var dayTotalPrice = unitPrice.toDouble() * paidHour.toDouble()* dayCount.toInt()
                mTvDayTotalPrice.text = AmountUtil.getRoundUpString(dayTotalPrice,2)
            }
        }

        //计算结算金额
        if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
            if (mSettlementMethod == 1) {//日结
                var settlementAmt = unitPrice.toDouble() * paidHour.toDouble()
                mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
            } else if (mSettlementMethod == 2) {//周结
                var startDate = mTvStartDate.text.toString()
                var endDate = mTvEndDate.text.toString()
                if (TextUtils.isEmpty(startDate)) {
                    mSettlementMethod = 0
                    mTvSettlementMethod.text = ""
                    return
                }
                if (TextUtils.isEmpty(endDate)) {
                    mSettlementMethod = 0
                    mTvSettlementMethod.text = ""
                    return
                }
                var diffDays = DateUtil.getDiffDay(startDate,endDate)
                if (diffDays < 7) {
                    mSettlementMethod = 0
                    mTvSettlementMethod.text = ""
                    return
                }

                if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
                    var settlementAmt = unitPrice.toDouble() * paidHour.toDouble() * 7
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
                }
            } else if (mSettlementMethod == 3) {//整单结
                var settlementPieceCount = 1
                if (!TextUtils.isEmpty(unitPrice)) {
                    var total = unitPrice.toDouble() * settlementPieceCount
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(total,2)
                }
            }
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
                NavigationUtils.goRealNameActivity(this@HireUpdateReleaseActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showReleaseSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您已成功发布雇用信息"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                HireReleaseActivity.intentStart(this@HireUpdateReleaseActivity,0)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    fun showDateDialog (currentTime: Long) {
        mCurrentTime = currentTime

        var formater = "yyyy.MM.dd.HH:mm"
        when (selectDateMode) {
            0 -> {//开始日期
                var startDate = DateUtil.getOldFetureDate(mCurrentTime,2,"yyyy.MM.dd")
                var endDate = DateUtil.getOldFetureDate(mCurrentTime,30 + 2 - 1,"yyyy.MM.dd")
                if (mToggleEmergencyRelease.isChecked) {//紧急发布
                    //判断当天是否还有3小时，没有3小时则开始日期+1天
                    //结束日期为开始日期+1天
                    //--->20:40 //21,22,23
                    var currentHour = DateUtil.stampToDate(mCurrentTime,"HH")?.toInt() ?: 0
                    var currentMinute = DateUtil.stampToDate(mCurrentTime,"mm")?.toInt() ?: 0
                    Loger.e(TAG,"showDateDialog-currentHour = $currentHour")
                    Loger.e(TAG,"showDateDialog-currentMinute = $currentMinute")
                    if (currentHour > 20) {
                        startDate = DateUtil.getOldFetureDate(mCurrentTime,1,"yyyy.MM.dd")
                        endDate = DateUtil.getOldFetureDate(mCurrentTime,1,"yyyy.MM.dd")
                    } else {
                        if (currentHour == 20 && currentMinute > 30) {
                            startDate = DateUtil.getOldFetureDate(mCurrentTime,1,"yyyy.MM.dd")
                            endDate = DateUtil.getOldFetureDate(mCurrentTime,1,"yyyy.MM.dd")
                        } else {
                            startDate = DateUtil.stampToDate(mCurrentTime,formater)
                            endDate = DateUtil.getOldFetureDate(mCurrentTime,1,"yyyy.MM.dd")
                        }
                    }
                }
                Loger.e(TAG, "showDateDialog-startDate = $startDate")
                Loger.e(TAG, "showDateDialog-endDate = $endDate")
                getWorkDatePickerDialog("开始日期",startDate,endDate).show()
            }
            1 -> {//结束日期
                var startDate = mTvStartDate.text.toString()
                var timestamp = DateUtil.getTimestampByDate(startDate,"yyyy.MM.dd")
                var endDate = DateUtil.getOldFetureDate(timestamp,29,"yyyy.MM.dd")

                Loger.e(TAG, "showDateDialog-startDate = $startDate")
                Loger.e(TAG, "showDateDialog-endDate = $endDate")
                getWorkDatePickerDialog("结束日期",startDate,endDate).show()
            }
            2 -> {//日开工时间
                var hour = "08"
                var minute = "00"
                var startHour = 0
                var endHour = 23

                var startDate = mTvStartDate.text.toString()
                var isToday = DateUtil.isSameDay(startDate.replace(".","-"),mCurrentTime)

                if (mToggleEmergencyRelease.isChecked) {
                    var currentHour = DateUtil.stampToDate(mCurrentTime,"HH")?.toInt() ?: 0
                    var currentMinute = DateUtil.stampToDate(mCurrentTime,"mm")?.toInt() ?: 0

                    if (isToday) {
                        startHour = currentHour
                        var addHour = 3

                        if (currentMinute > 30) {
                            minute = "00"
                            addHour++
                        } else {
                            minute = "30"
                        }
                        startHour += addHour
                        hour = startHour.toString()
                    } else {
                        //不是今天
                        if (currentHour > 20) {
                            var roll3HDate = DateUtil.getRollDate(mCurrentTime,3,"HH:mm")
                            if (currentMinute > 30) {
                                roll3HDate = DateUtil.getRollDate(mCurrentTime,4,"HH:mm")
                            }

                            startHour = roll3HDate?.split(":")?.get(0)?.toInt() ?: 0
                            Loger.e(TAG,"showDateDialog-roll3HDate = $roll3HDate")
                            hour = startHour.toString()
                        } else {
                            if (currentHour == 20 && currentMinute > 30) {
                                var roll3HDate = DateUtil.getRollDate(mCurrentTime,3,"HH:mm")
                                if (currentMinute > 30) {
                                    roll3HDate = DateUtil.getRollDate(mCurrentTime,4,"HH:mm")
                                }

                                startHour = roll3HDate?.split(":")?.get(0)?.toInt() ?: 0
                                Loger.e(TAG,"showDateDialog-roll3HDate = $roll3HDate")
                                hour = startHour.toString()
                            }
                        }
                    }
                }

                Loger.e(TAG,"showDateDialog-currentTime = $mCurrentTime")
                Loger.e(TAG,"showDateDialog-startHour = $startHour")
                Loger.e(TAG,"showDateDialog-endHour = $endHour")

                getWorkTimePickerDialog("日开工时间",mCurrentTime,
                        mToggleEmergencyRelease.isChecked,hour,minute,startHour,endHour,
                        true,true,true).show()
            }
            3 -> {//日完工时间
                var startTime = mTvStartTime.text.toString()
                if (TextUtils.isEmpty(startTime)) {
                    ToastUtils.show("请选择日开工时间")
                    return
                }

                var hour = "18"
                var minute = "00"
                var startHour = 0
                var endHour = 0

                if (mToggleEmergencyRelease.isChecked) {
                    hour = startTime?.split(":").get(0)
                    minute = startTime?.split(":").get(1)
                }

                var start = DateUtil.getRollDate(startTime,1,"HH:mm")
                startHour = start?.split(":")?.get(0)?.toInt() ?: 0

                var end = DateUtil.getRollDate(startTime,18,"HH:mm")
                endHour = end?.split(":")?.get(0)?.toInt() ?: 0

                var showFirst0 = true
                var showLast30 = true
                var endMins = end?.split(":")?.get(1)?.toInt() ?: 0
                if (endMins == 0) {
                    //最后一个去掉30分
                    showLast30 = false
                } else if (endMins == 30) {
                    //第一个去掉00分
                    showFirst0 = false
                }

                Loger.e(TAG,"showDateDialog-startHour = $startHour")
                Loger.e(TAG,"showDateDialog-endHour = $endHour")

                getWorkTimePickerDialog("日完工时间",mCurrentTime,
                        mToggleEmergencyRelease.isChecked,hour,minute,startHour,endHour,
                        false,showFirst0,showLast30).show()
            }
        }
    }

    fun getWorkPics (): List<String> {
        var workPics = ArrayList<String>()
        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        if (count > 1) {
            mBusinessWorkPicAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    it.pic?.let { it1 -> workPics.add(it1) }
                }
            }
        }

        return workPics
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {

        var mCityPickerDialog = CityPickerDialog(this)
        mCityPickerDialog.mCancelText = "清除"
        mCityPickerDialog.provinceDatas = data
        mCityPickerDialog.mOnCityPickerListener = this
        provinceData = data

        if (mProvinceInfo != null) {
            mCityPickerDialog.mProvince = mProvinceInfo?.name
        }
        if (mCityInfo != null) {
            mCityPickerDialog.mCity = mCityInfo?.name
        }
        if (mAreaInfo != null) {
            mCityPickerDialog.mArea = mAreaInfo?.name
        }

        if (mToggleDoAtHome.isChecked) {
            mCityPickerDialog.mTitleText = "选择发布城市"
        } else {
            mCityPickerDialog.mTitleText = "选择工作地区"
        }

        return mCityPickerDialog
    }

    fun getWorkDatePickerDialog (title: String?,startDate: String?,endDate: String?): WorkDatePickerDialog {
        var mWorkDatePickerDialog = WorkDatePickerDialog(this)
        mWorkDatePickerDialog.mDatePickerTitle = title
        mWorkDatePickerDialog.startDate = startDate
        mWorkDatePickerDialog.endDate = endDate
        mWorkDatePickerDialog.mOnDatePickListener = this
        return mWorkDatePickerDialog
    }

    fun getWorkTimePickerDialog (title: String,currentTime: Long,isEmergencyRelease: Boolean,
                                 hour: String?,minute: String?,startHour: Int,endHour: Int,
                                 isSelectStartTime: Boolean,showFirst0: Boolean,showLast30: Boolean
    ): WorkTimePickerDialog {
        var mWorkTimePickerDialog = WorkTimePickerDialog(this)
        mWorkTimePickerDialog.mTimePickerTitle = title
        mWorkTimePickerDialog.isEmergencyRelease = isEmergencyRelease
        mWorkTimePickerDialog.currentTime = currentTime
        mWorkTimePickerDialog.mStartDate = mTvStartDate.text.toString()
        mWorkTimePickerDialog.mHour = hour
        mWorkTimePickerDialog.mMinute = minute
        mWorkTimePickerDialog.mStartHour = startHour
        mWorkTimePickerDialog.mEndHour = endHour
        mWorkTimePickerDialog.isSelectStartTime = isSelectStartTime
        mWorkTimePickerDialog.showFirst0 = showFirst0
        mWorkTimePickerDialog.showLast30 = showLast30

        mWorkTimePickerDialog.mOnTimePickerListener = this
        return mWorkTimePickerDialog
    }

    fun getWorkingHoursPickerDialog (): WorkingHoursPickerDialog {
        var mWorkingHoursPickerDialog = WorkingHoursPickerDialog(this)
        mWorkingHoursPickerDialog.mOnWorkingHoursSelectListener = this
        return mWorkingHoursPickerDialog
    }

    fun getSettlementMethodDialog (): SettlementMethodDialog? {
        var mSettlementMethodDialog = SettlementMethodDialog(this)
        mSettlementMethodDialog?.settlementMethods = getSettlementMethods()
        mSettlementMethodDialog?.mOnSettlementMethodSelectListener = this
        return mSettlementMethodDialog
    }

    fun getIdentityRequirementDialog (): IdentityRequirementDialog? {
        if (mIdentityRequirementDialog == null) {
            mIdentityRequirementDialog = IdentityRequirementDialog(this)
            mIdentityRequirementDialog?.mOnIdentitySelectListener = this
        }
        return mIdentityRequirementDialog
    }

    fun getGenderRequirementDialog (): GenderRequirementDialog? {
        if (mGenderRequirementDialog == null) {
            mGenderRequirementDialog = GenderRequirementDialog(this)
            mGenderRequirementDialog?.mOnGenderSelectListener = this
        }
        return mGenderRequirementDialog
    }

    fun getEducationPickerDialog (title: String?): EducationPickerDialog? {
        if (mEducationPickerDialog == null) {
            mEducationPickerDialog = EducationPickerDialog(this)
            mEducationPickerDialog?.mOnEducationSelectListener = this
            mEducationPickerDialog?.title = title
        }
        return mEducationPickerDialog
    }

    fun getAgeRequirementDialog (): AgeRequirementDialog? {
        if (mAgeRequirementDialog == null) {
            mAgeRequirementDialog = AgeRequirementDialog(this)
            mAgeRequirementDialog?.mOnAgeSelectListener = this
        }
        return mAgeRequirementDialog
    }

    fun getMyEmployerDialog (data: List<EmployerInfo>?): MyEmployerDialog {
        var mMyEmployerDialog = MyEmployerDialog(this)
        mMyEmployerDialog?.employerList = data
        mMyEmployerDialog?.mOnEmployerSelectListener = this

        return mMyEmployerDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvEmployer -> {
                sendEmployersRequest()
            }
            R.id.mTvTalentType -> {
                TalentTypeActivity.intentStart(this,mTvTalentType.text.toString())
            }
            R.id.mTvWorkArea -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mTvStartDate -> {
                selectDateMode = 0
                sendSystemTimeRequest()
            }
            R.id.mTvEndDate -> {
                selectDateMode = 1
                var startDate = mTvStartDate.text.toString()
                if (TextUtils.isEmpty(startDate)) {
                    ToastUtils.show("请选择开始日期")
                    return
                }
                sendSystemTimeRequest()
            }
            R.id.mTvStartTime -> {
                selectDateMode = 2

                var startDate = mTvStartDate.text.toString()
                if (TextUtils.isEmpty(startDate)) {
                    ToastUtils.show("请选择开始日期")
                    return
                }
                sendSystemTimeRequest()
            }
            R.id.mTvEndTime -> {
                selectDateMode = 3
                var startDate = mTvStartDate.text.toString()
                var startTime = mTvStartTime.text.toString()

                if (TextUtils.isEmpty(startDate)) {
                    ToastUtils.show("请选择开始日期")
                    return
                }
                if (TextUtils.isEmpty(startTime)) {
                    ToastUtils.show("请选择日开工时间")
                    return
                }
                sendSystemTimeRequest()
            }
            R.id.mIvWorkTimeNotice -> {
                val xOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_10) * 1.0f)
                val yOffset = DensityUtil.dip2px(this, ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var workTimeNoticePopWindow = WorkTimeNoticePopWindow(this)
                workTimeNoticePopWindow.show(mIvWorkTimeNotice, AbsPopWindow.LocationGravity.BOTTOM_LEFT,xOffset, yOffset)
            }
            R.id.mClPaidHour -> {
                getWorkingHoursPickerDialog().show()
            }
            R.id.mClSettlementMethod -> {
                var startDate = mTvStartDate.text.toString()
                var endDate = mTvEndDate.text.toString()
                if (TextUtils.isEmpty(startDate)) {
                    ToastUtils.show("请选择开始日期")
                    return
                }
                if (TextUtils.isEmpty(endDate)) {
                    ToastUtils.show("请选择结束日期")
                    return
                }
                getSettlementMethodDialog()?.show()
            }
            R.id.mClRequireIdentity -> {
                getIdentityRequirementDialog()?.show()
            }
            R.id.mClSexRequirement -> {
                getGenderRequirementDialog()?.show()
            }
            R.id.mClAgeRequirement -> {
                getAgeRequirementDialog()?.show()
            }
            R.id.mClEduRequirement -> {
                getEducationPickerDialog("学历要求")?.show()
            }
            R.id.mTvSave -> {
                sendSaveEmployerReleaseRequest(false)
            }
            R.id.mTvRelease -> {
                sendSaveEmployerReleaseRequest(true)
            }
        }
    }

    override fun OnDatePick(date: String) {
        Loger.e(TAG,"OnDatePick-date = $date")
        if (selectDateMode == 0) {//开始日期
            var endDate = mTvEndDate.text.toString()
            if (!TextUtils.isEmpty(endDate)) {
                if (DateUtil.isBeforeStartDate(date,endDate,"yyyy.MM.dd")) {
                    ToastUtils.show("开始日期不能在结束日期之后，请重新选择")
                    return
                }
                var diffDays = DateUtil.getDiffDay(date,endDate)
                if (diffDays > 30) {
                    ToastUtils.show("开始时间不能选择结束时间30天以前")
                    return
                }
                mTvDayCount.text = diffDays.toString()
                calcuAmount()
            }
            mTvStartDate.text = date
        } else if (selectDateMode == 1) {//结束日期
            var startDate = mTvStartDate.text.toString()
            if (!TextUtils.isEmpty(startDate)) {

                if (DateUtil.isBeforeStartDate(startDate,date,"yyyy.MM.dd")) {
                    ToastUtils.show("结束日期不能在开始日期之前，请重新选择")
                    return
                }
                var diffDays = DateUtil.getDiffDay(startDate,date)

                if (diffDays > 30) {
                    ToastUtils.show("结束时间不能选择开始时间30天以后")
                    return
                }
                mTvDayCount.text = diffDays.toString()
                calcuAmount()
            }
            mTvEndDate.text = date
        }

        cacuDeadlineDate()
    }

    override fun OnTimePicker(time: String) {
        Loger.e(TAG,"OnTimePicker-time = $time")
        if (selectDateMode == 2) {//日开工时间
            var endTime = mTvEndTime.text.toString()
            if (TextUtils.isEmpty(endTime)) {
                var current = DateUtil.stampToDate(mCurrentTime,"HH:mm") ?: ""
                if (!checkStartTime(current,time)) {
                    ToastUtils.show("紧急发布日开工时间必须在当前时间3小时之后，请重新选择")
                    return
                }
            } else {
                if (!checkEndTime(time,endTime)) {
                    ToastUtils.show("日开工时间，不能在日完工时间18小时之前，请重新选择")
                    return
                }
            }
            mTvStartTime.text = time
        } else if (selectDateMode == 3) {//日完工时间
            var startTime = mTvStartTime.text.toString()
            if (!checkEndTime(startTime,time)) {
                ToastUtils.show("日完工时间，不能超过日开工时间18小时，请重新选择")
                return
            }
            var isBeforeStartTime = DateUtil.isNextDayDate(startTime,time,"HH:mm")
            if (isBeforeStartTime) {
                mTvEndTime.text = "次日$time"
            } else {
                mTvEndTime.text = time
            }
        }

        cacuDeadlineDate()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbHourlySalary -> {
                mClHourlySalary.visibility = View.VISIBLE
                mClPieceSalary.visibility = View.GONE

                mTvSettlementMethod.text = "日结"
                mSettlementMethod = 1

                calcuAmount()
            }
            R.id.mRbPieceSalary -> {
                mClPieceSalary.visibility = View.VISIBLE
                mClHourlySalary.visibility = View.GONE

                mTvSettlementMethod.text = "整单结"
                mSettlementMethod = 3

                calcuAmount()
            }
        }
    }

    override fun OnSettlementMethodSelect(settlementMethod: SettlementInfo?) {
        mSettlementMethod = settlementMethod?.settlementMethod
        when (settlementMethod?.settlementMethod) {
            1 -> {//日结
                mTvSettlementMethod.text = "日结"

                var unitPrice = mEtUnitPrice.text.toString()
                var paidHour = mTvPaidHour.text.toString().replace("小时","")

                if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
                    var settlementAmt = unitPrice.toDouble() * paidHour.toDouble()
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
                }
            }
            2 -> {//周结
                mTvSettlementMethod.text = "周结(7天)"

                mSettlementMethod = 2

                var unitPrice = mEtUnitPrice.text.toString()
                var paidHour = mTvPaidHour.text.toString().replace("小时","")

                if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
                    var settlementAmt = unitPrice.toDouble() * paidHour.toDouble() * 7
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
                }
            }
            3 -> {//整单结
                mTvSettlementMethod.text = "整单结"

            }
        }

        setSettlementMethodUi()
    }

    /**
     * 结算方式（1-日结；2-周结；3-整单结）
     */
    fun setSettlementMethodUi () {
        when (mSettlementMethod) {
            1 -> {//日结
                mTvSettlementMethod.text = "日结"

                var unitPrice = mEtUnitPrice.text.toString()
                var paidHour = mTvPaidHour.text.toString().replace("小时","")

                if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
                    var settlementAmt = unitPrice.toDouble() * paidHour.toDouble()
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
                }
            }
            2 -> {//周结
                mTvSettlementMethod.text = "周结(7天)"

                var unitPrice = mEtUnitPrice.text.toString()
                var paidHour = mTvPaidHour.text.toString().replace("小时","")

                if (!TextUtils.isEmpty(unitPrice) && !TextUtils.isEmpty(paidHour)) {
                    var settlementAmt = unitPrice.toDouble() * paidHour.toDouble() * 7
                    mTvSettlementAmount.text = AmountUtil.getRoundUpString(settlementAmt,2)
                }
            }
            3 -> {//整单结
                mTvSettlementMethod.text = "整单结"
            }
        }
    }

    fun getSettlementMethods (): ArrayList<SettlementInfo> {
        var settlementMethods = ArrayList<SettlementInfo>()
        //如果是时薪
        if (mRbHourlySalary.isChecked) {
            var startDate = mTvStartDate.text.toString()
            var endDate = mTvEndDate.text.toString()
            if (TextUtils.isEmpty(startDate)) {
                ToastUtils.show("请选择开始日期")
                return settlementMethods
            }
            if (TextUtils.isEmpty(endDate)) {
                ToastUtils.show("请选择结束日期")
                return settlementMethods
            }

            var item = SettlementInfo()
            item.settlementMethod = 1
            item.settlementName = "日结"
            settlementMethods.add(item)

            var diffDays = DateUtil.getDiffDay(startDate,endDate)
            if (diffDays >= 7) {
                var item = SettlementInfo()
                item.settlementMethod = 2
                item.settlementName = "周结"
                settlementMethods.add(item)
            }

        } else {
            var item = SettlementInfo()
            item.settlementMethod = 3
            item.settlementName = "整单结"
            settlementMethods.add(item)
        }
        return settlementMethods
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleDoAtHome -> {
                if (isChecked) {
                    mRtvWorkAddress.setTextValue(" 发布城市")
                    mTvWorkArea.hint = "默认全国(可选城市)"
                    mEtAddress.hint = "收货地址(非必填，默认线上)"
                } else {
                    mRtvWorkAddress.setTextValue(" 工作地区")
                    mTvWorkArea.hint = "请选择工作地区"
                    mEtAddress.hint = "详细地址(必填)"
                }
            }
            R.id.mToggleEmergencyRelease -> {
                cacuDeadlineDate()
            }
        }
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) {
            mProvinceInfo = null
            mCityInfo = null
            mAreaInfo = null
            mTvWorkArea.text = ""
            return
        }

        mTvWorkArea.text = province?.name + city?.name + area?.name
        mProvinceInfo = province
        mCityInfo = city
        mAreaInfo = area
    }

    override fun OnEmployerSelect(data: EmployerInfo?, employerCount: Int) {
        if (data == null) {
            NewEmployerActivity.intentStart(this)
            return
        }
        mTvEmployer.text = data?.name
        mEmployerInfo = data
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        when (view?.id) {
            R.id.mIvWorkPic,R.id.mIvWorkPicBorder -> {
                if (position == count - 1) {
                    var picUrl = mBusinessWorkPicAdapter?.getItem(position)?.pic
                    if (!TextUtils.isEmpty(picUrl)) return //已经添加3张图片最后一张图片不是添加按钮
                    var maxCount = 4 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加3张")
                        return
                    }
                    PictureSelectorUtil.selectMedia(this,false,true,false,maxCount)
                }
            }
            R.id.mIvWorkPicDel -> {
                mBusinessWorkPicAdapter?.removeItem(position)
                mBusinessWorkPicAdapter?.notifyItemRemoved(position)

                count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
                if (count > 0 && count < 3) {
                    var picUrl = mBusinessWorkPicAdapter?.getItem(count - 1)?.pic
                    if (TextUtils.isEmpty(picUrl)) return //已经添加按钮
                    mBusinessWorkPicAdapter?.add(WorkPicInfo())
                    mBusinessWorkPicAdapter?.notifyItemInserted(count)
                }
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

    fun checkStartTime (startTime: String,selectTime: String): Boolean {
        if (releaseType == 2) {
            var startDate = mTvStartDate.text.toString()
            if (!TextUtils.isEmpty(startDate)) {
//                var isToday = DateUtil.isToday(startDate.replace(".","-"))
                var isToday = DateUtil.isSameDay(startDate.replace(".","-"),mCurrentTime)
                Loger.e(TAG,"checkStartTime-isToday = " + isToday)
                if (isToday) {
                    return DateUtil.isAfter3Hours(startTime,selectTime)
                }
            }
        }
        return true
    }

    fun checkEndTime (startTime: String,selectTime: String): Boolean {
        return !DateUtil.isAfter18Hours(startTime,selectTime)
    }

    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"url = $url")

                var data = WorkPicInfo()
                data.pic = url

                var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
                mBusinessWorkPicAdapter?.removeItem(count - 1)
                mBusinessWorkPicAdapter?.add(data)
                if (count < 3) {
                    mBusinessWorkPicAdapter?.add(WorkPicInfo())
                }
                mBusinessWorkPicAdapter?.notifyDataSetChanged()

                if (selectList != null && selectList.size > 1) {
                    selectList.removeAt(0)
                    uploadImgae2Oss()
                }
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    override fun OnGenderSelect(position: Int, gender: String) {
        mTvSexRequirement.text = gender
        when (position) {
            0 -> {//不限
                sexRequirement = 2
            }
            1 -> {//男
                sexRequirement = 1
            }
            2 -> {//女
                sexRequirement = 0
            }
        }
    }


    override fun OnAgeSelect(selectAge: String) {
        mTvAgeRequirement.text = selectAge
        if (TextUtils.equals("不限",selectAge)) {
            ageRequirement = ""
        } else {
            ageRequirement = selectAge
        }
    }

    override fun OnEducationSelect(position: Int, education: String) {
        mTvEduRequirement.text = education

        when (position) {
            0 -> {//不限
                educationRequirement = ""
            }
            else -> {
                educationRequirement = education
            }
        }
    }

    override fun OnWorkingHoursSelect(position: Int, hours: String) {
        mTvPaidHour.text = hours
    }

    override fun OnIdentitySelect(identityRequirement: Int, identityRequirementTxt: String) {
        mTvRequireIdentity.text = identityRequirementTxt
        mSaveEmployerReleaseParm?.identityRequirement = identityRequirement
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