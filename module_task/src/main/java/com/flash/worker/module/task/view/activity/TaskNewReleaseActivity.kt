package com.flash.worker.module.task.view.activity

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
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.util.ViewUtils.hide
import com.flash.worker.lib.common.util.ViewUtils.show
import com.flash.worker.lib.common.view.adapter.BusinessWorkPicAdapter
import com.flash.worker.lib.common.view.dialog.*
import com.flash.worker.lib.coremodel.data.bean.EmployerInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ReleaseTaskParms
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.ReleaseTaskParm
import com.flash.worker.lib.coremodel.data.parm.SaveTaskParm
import com.flash.worker.lib.coremodel.data.parm.TaskPrepaidDetailParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerVM
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.TaskVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.TaskActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import com.flash.worker.module.task.interfaces.OnTaskCompleteTimeSelectListener
import com.flash.worker.module.task.view.dialog.TaskCompleteTimePickerDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_task_new_release.*
import kotlinx.android.synthetic.main.task_release_content.*

@Route(path = ARouterPath.TaskNewReleaseAct)
class TaskNewReleaseActivity : BaseActivity(),View.OnClickListener,RadioGroup.OnCheckedChangeListener,
    OnTaskCompleteTimeSelectListener, OnEmployerSelectListener, AdapterView.OnItemClickListener,
    OnUploadListener,TextWatcher, OnAgeSelectListener, OnGenderSelectListener,
    CompoundButton.OnCheckedChangeListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TaskNewReleaseActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val employerVM: EmployerVM by viewModels {
        InjectorUtils.provideEmployerVMFactory(this)
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    private val taskVM: TaskVM by viewModels {
        InjectorUtils.provideTaskVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mAgeRequirementDialog: AgeRequirementDialog? = null
    var mGenderRequirementDialog: GenderRequirementDialog? = null
    var mEmployerInfo: EmployerInfo? = null
    var mBusinessWorkPicAdapter: BusinessWorkPicAdapter? = null
    var selectList: MutableList<LocalMedia> = ArrayList()
    var uploadConfigReq: UploadConfigReq? = null

    var ageRequirement: String? = null
    var sexRequirement: Int = 2

    var mReleaseTaskParm: ReleaseTaskParm? = null

    override fun getLayoutResource() = R.layout.activity_task_new_release

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mBusinessWorkPicAdapter = BusinessWorkPicAdapter(this,this)
        mBusinessWorkPicAdapter?.workPicTitle = "图片上传"
        mRvWorksPic.adapter = mBusinessWorkPicAdapter

        mIvBack.setOnClickListener(this)
        mClEmployer.setOnClickListener(this)
        mClFinishTimeLimit.setOnClickListener(this)
        mClAgeRequirement.setOnClickListener(this)
        mClSexRequirement.setOnClickListener(this)
        mTvSave.setOnClickListener(this)
        mTvRelease.setOnClickListener(this)

        mRgCompletionTime.setOnCheckedChangeListener(this)
        mTogglePublicTel.setOnCheckedChangeListener(this)
        mEtDescription.addTextChangedListener(this)


        var filter = MoneyInputFilter()
        filter.setMaxValue(1000.0)
        filter.setDecimalLength(0)
        mEtTaskCount.filters = arrayOf(filter)
    }

    fun initData () {
        mBusinessWorkPicAdapter?.add(WorkPicInfo())
        mBusinessWorkPicAdapter?.notifyDataSetChanged()

        sendUploadConfigRequest()
    }

    fun subscribeEvent()  {
        LiveDataBus.with(TaskActions.RELEASE_TASK_SUCCESS)
            .observe(this,Observer {
            finish()
        })
    }

    fun subscribeUi() {
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

        employerVM.employersData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getMyEmployerDialog(it.value.data).show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.checkEmployerBaseInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        NavigationUtils.goNewEmployerActivity(this)
                    } else {
                        NavigationUtils.goEmployerBasicActivity(this)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        taskVM.saveTaskData.observe(this, Observer {
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

        taskVM.taskPrepaidDetailData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var parms = ReleaseTaskParms()
                    parms.body = mReleaseTaskParm
                    parms.taskPrepaidDetailData = it.value.data
                    PrepaidFreezeActivity.intentStart(this,parms)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendUploadConfigRequest () {
        if (!App.get().hasLogin()) return
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

    fun sendCheckEmployerBaseInfoRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        userVM.checkEmployerBaseInfo(token)
    }

    fun sendSaveTaskRequest () {
        if (!App.get().hasLogin()) return

        var employerName = mTvEmployer.text.toString()
        if (TextUtils.isEmpty(employerName)) {
            ToastUtils.show("请选择雇主信息")
            return
        }

        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入任务名称")
            return
        }

        var taskCount = mEtTaskCount.text.toString()
        if (TextUtils.isEmpty(taskCount)) {
            ToastUtils.show("请输入任务数量")
            return
        }

        var price = mEtPrice.text.toString()
        if (TextUtils.isEmpty(price)) {
            ToastUtils.show("请输入单价")
            return
        }

        var finishTimeLimit = mTvFinishTimeLimit.text.toString()

        var finishTimeLimitUnit = 1
        if (mRbHour.isChecked) {
            finishTimeLimitUnit = 1
        } else if (mRbDay.isChecked) {
            finishTimeLimitUnit = 2
        }

        var settlementTimeLimit = 24
        if (mRb24Hour.isChecked) {
            settlementTimeLimit = 24
        } else if (mRb48Hour.isChecked) {
            settlementTimeLimit = 48
        }

        var timesLimit = 1
        if (mRbSingle.isChecked) {
            timesLimit = 1
        } else if (mRbMultiple.isChecked) {
            timesLimit = 2
        }

        var description = mEtDescription.text.toString()

        var submitLabel = StringBuffer()
        if (mChkOrderNo.isChecked) {
            submitLabel.append("单号")
            submitLabel.append(",")
        }
        if (mChkIdNumber.isChecked) {
            submitLabel.append("身份证号后六位")
            submitLabel.append(",")
        }
        if (mChkScreenShot.isChecked) {
            submitLabel.append("截图")
            submitLabel.append(",")
        }
        if (mChkUserName.isChecked) {
            submitLabel.append("用户名")
            submitLabel.append(",")
        }

        if (submitLabel.length > 0) {
            submitLabel = submitLabel.deleteCharAt(submitLabel.length-1)
        }

        mLoadingDialog?.show()

        var body = SaveTaskParm()
        body.employerId = mEmployerInfo?.id
        body.title = title
        body.taskQty = taskCount.toInt()
        body.price = price.toInt()

        body.finishTimeLimitUnit = finishTimeLimitUnit
        if (!TextUtils.isEmpty(finishTimeLimit)) {
            body.finishTimeLimit = finishTimeLimit.toInt()
        }
        body.settlementTimeLimit = settlementTimeLimit
        body.timesLimit = timesLimit
        body.workDescription = description
        body.submitLabel = submitLabel.toString()
        body.pics = getWorkPics()
        body.ageRequirement = ageRequirement
        body.sexRequirement = sexRequirement

        var tel = mEtTel.text.toString()
        if (!TextUtils.isEmpty(tel) && mTogglePublicTel.isChecked) {
            body.isOpenContactPhone = mTogglePublicTel.isChecked
            body.contactPhone = tel
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        taskVM.saveTask(token,body)

    }

    fun sendTaskPrepaidDetailRequest() {
        if (!App.get().hasLogin()) return

        var employerName = mTvEmployer.text.toString()
        if (TextUtils.isEmpty(employerName)) {
            ToastUtils.show("请选择雇主信息")
            return
        }

        var title = mEtTitle.text.toString()
        if (TextUtils.isEmpty(title)) {
            ToastUtils.show("请输入任务名称")
            return
        }

        var taskCount = mEtTaskCount.text.toString()
        if (TextUtils.isEmpty(taskCount)) {
            ToastUtils.show("请输入任务数量")
            return
        }

        var price = mEtPrice.text.toString()
        if (TextUtils.isEmpty(price)) {
            ToastUtils.show("请输入单价")
            return
        }

        var finishTimeLimit = mTvFinishTimeLimit.text.toString()

        if (TextUtils.isEmpty(finishTimeLimit)) {
            ToastUtils.show("请选择完成时限")
            return
        }

        var tel = mEtTel.text.toString()
        if (TextUtils.isEmpty(tel) && mTogglePublicTel.isChecked) {
            ToastUtils.show("请输入联系方式")
            return
        }

        var finishTimeLimitUnit = 1
        if (mRbHour.isChecked) {
            finishTimeLimitUnit = 1
        } else if (mRbDay.isChecked) {
            finishTimeLimitUnit = 2
        }

        var settlementTimeLimit = 24
        if (mRb24Hour.isChecked) {
            settlementTimeLimit = 24
        } else if (mRb48Hour.isChecked) {
            settlementTimeLimit = 48
        }

        var timesLimit = 1
        if (mRbSingle.isChecked) {
            timesLimit = 1
        } else if (mRbMultiple.isChecked) {
            timesLimit = 2
        }

        var description = mEtDescription.text.toString()
        if (TextUtils.isEmpty(description)) {
            ToastUtils.show("请输入任务描述")
            return
        }

        var submitLabel = StringBuffer()
        if (mChkOrderNo.isChecked) {
            submitLabel.append("单号")
            submitLabel.append(",")
        }
        if (mChkIdNumber.isChecked) {
            submitLabel.append("身份证号后六位")
            submitLabel.append(",")
        }
        if (mChkScreenShot.isChecked) {
            submitLabel.append("截图")
            submitLabel.append(",")
        }
        if (mChkUserName.isChecked) {
            submitLabel.append("用户名")
            submitLabel.append(",")
        }

        if (TextUtils.isEmpty(submitLabel.toString())) {
            ToastUtils.show("请选择任务提交所需资料")
            return
        }

        submitLabel = submitLabel.deleteCharAt(submitLabel.length-1)

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 0) {
            showAuthTipDlg()
            return
        }

        mReleaseTaskParm = ReleaseTaskParm()
        mReleaseTaskParm?.employerId = mEmployerInfo?.id
        mReleaseTaskParm?.title = title
        mReleaseTaskParm?.taskQty = taskCount.toInt()
        mReleaseTaskParm?.price = price.toInt()
        mReleaseTaskParm?.finishTimeLimitUnit = finishTimeLimitUnit
        mReleaseTaskParm?.finishTimeLimit = finishTimeLimit.toInt()
        mReleaseTaskParm?.settlementTimeLimit = settlementTimeLimit
        mReleaseTaskParm?.timesLimit = timesLimit
        mReleaseTaskParm?.workDescription = description
        mReleaseTaskParm?.submitLabel = submitLabel.toString()
        mReleaseTaskParm?.pics = getWorkPics()
        mReleaseTaskParm?.ageRequirement = ageRequirement
        mReleaseTaskParm?.sexRequirement = sexRequirement

        if (mTogglePublicTel.isChecked) {
            mReleaseTaskParm?.isOpenContactPhone = mTogglePublicTel.isChecked
            mReleaseTaskParm?.contactPhone = tel
        }

        mLoadingDialog?.show()

        var body = TaskPrepaidDetailParm()
        body.title = title
        body.price = AmountUtil.getRoundUpDouble(price.toDouble(),2)
        body.taskQty = taskCount.toInt()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        taskVM.fetchTaskPrepaidDetail(token,body)
    }

    fun showTaskCompleteTimePickerDlg () {
        var taskCompleteTimePickerDialog = TaskCompleteTimePickerDialog(this)
        taskCompleteTimePickerDialog.mOnTaskCompleteTimeSelectListener = this
        taskCompleteTimePickerDialog.isHour = mRbHour.isChecked
        taskCompleteTimePickerDialog.show()
    }

    fun getMyEmployerDialog (data: List<EmployerInfo>?): MyEmployerDialog {
        var mMyEmployerDialog = MyEmployerDialog(this)
        mMyEmployerDialog?.employerList = data
        mMyEmployerDialog?.mOnEmployerSelectListener = this

        return mMyEmployerDialog
    }

    fun getAgeRequirementDialog (): AgeRequirementDialog? {
        if (mAgeRequirementDialog == null) {
            mAgeRequirementDialog = AgeRequirementDialog(this)
            mAgeRequirementDialog?.mOnAgeSelectListener = this
        }
        return mAgeRequirementDialog
    }

    fun getGenderRequirementDialog (): GenderRequirementDialog? {
        if (mGenderRequirementDialog == null) {
            mGenderRequirementDialog = GenderRequirementDialog(this)
            mGenderRequirementDialog?.mOnGenderSelectListener = this
        }
        return mGenderRequirementDialog
    }

    fun showSaveSuccessDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "成功保存，可在操作台-雇主-雇用发布-编辑中查看"
        commonTipDialog.mCancelText = "留在此页"
        commonTipDialog.mOkText = "前往查看"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goHireReleaseActivity(this@TaskNewReleaseActivity,2)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }
        }
        commonTipDialog.show()
    }

    fun showBackTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您还未保存当前页面所填内容哟，确定返回吗？"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "保存"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendSaveTaskRequest()
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
        commonTipDialog.mCancelText = "放弃认证"
        commonTipDialog.mOkText = "前往认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goRealNameActivity(this@TaskNewReleaseActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getWorkPics (): List<String> {
        var workPics = ArrayList<String>()
        var count = mBusinessWorkPicAdapter?.itemCount ?: 0
        if (count > 1) {
            mBusinessWorkPicAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    it.pic?.let { it1 -> workPics.add(it1) }
                }
            }
        }

        return workPics
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                showBackTipDlg()
            }
            R.id.mClEmployer -> {
                sendEmployersRequest()
            }
            R.id.mClFinishTimeLimit -> {
                showTaskCompleteTimePickerDlg()
            }
            R.id.mClAgeRequirement -> {
                getAgeRequirementDialog()?.show()
            }
            R.id.mClSexRequirement -> {
                getGenderRequirementDialog()?.show()
            }
            R.id.mTvSave -> {
                sendSaveTaskRequest()
            }
            R.id.mTvRelease -> {
                sendTaskPrepaidDetailRequest()
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbHour -> {
                mTvFinishTimeLimit.text = ""
                tv_finish_time_limit_unit.text = ""
                tv_finish_time_limit_unit.hint = "选择小时数"
            }
            R.id.mRbDay -> {
                mTvFinishTimeLimit.text = ""
                tv_finish_time_limit_unit.text = ""
                tv_finish_time_limit_unit.hint = "选择天数"
            }
        }
    }

    override fun OnTaskCompleteTimeSelect(position: Int, time: String) {
        tv_finish_time_limit_unit.hint = ""
        mTvFinishTimeLimit.text = time
        if (mRbHour.isChecked) {
            tv_finish_time_limit_unit.text = "小时"
        } else {
            tv_finish_time_limit_unit.text = "天"
        }
    }

    override fun OnEmployerSelect(data: EmployerInfo?, employerCount: Int) {
        if (data == null) {
            if (employerCount >= 50) {
                ToastUtils.show("您的简历数已达上限  您可删除后再新增！")
                return
            }
            if (employerCount == 0) {
                sendCheckEmployerBaseInfoRequest()
                return
            }
            sendCheckEmployerBaseInfoRequest()
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
                    if (!TextUtils.isEmpty(picUrl)) return //已经添加4张图片最后一张图片不是添加按钮
                    var maxCount = 5 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加4张")
                        return
                    }
                    PictureSelectorUtil.selectMedia(this,false,true,false,maxCount)
                }
            }
            R.id.mIvWorkPicDel -> {
                mBusinessWorkPicAdapter?.removeItem(position)
                mBusinessWorkPicAdapter?.notifyItemRemoved(position)
                count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
                if (count > 0 && count < 4) {
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
                    Loger.e(TAG,"onActivityResult-res = " + JsonUtils.toJSONString(list.get(0)))
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

                var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
                mBusinessWorkPicAdapter?.removeItem(count - 1)
                mBusinessWorkPicAdapter?.add(data)
                if (count < 4) {
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        mTvDescriptionCount.text = "${s?.length}/500"
    }

    override fun OnAgeSelect(selectAge: String) {
        mTvAgeRequirement.text = selectAge
        if (TextUtils.equals("不限",selectAge)) {
            ageRequirement = ""
        } else {
            ageRequirement = selectAge
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
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