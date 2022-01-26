package com.flash.worker.module.business.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.SaveResumeParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnEducationSelectListener
import com.flash.worker.module.business.view.adapter.BusinessQualificationAdapter
import com.flash.worker.module.business.view.adapter.BusinessWorkExperienceAdapter
import com.flash.worker.lib.common.view.adapter.BusinessWorkPicAdapter
import com.flash.worker.module.business.view.dialog.EducationPickerDialog
import com.library.flowlayout.FlowLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_new_resume.*
import kotlinx.android.synthetic.main.fl_qualification_cell.view.*
import kotlinx.android.synthetic.main.rv_business_work_experience_cell.view.*

@Route(path = ARouterPath.NewResumeAct)
class NewResumeActivity : BaseActivity(),View.OnClickListener, OnEducationSelectListener,
        AdapterView.OnItemClickListener, TextWatcher, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, NewResumeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var mEducationPickerDialog: EducationPickerDialog? = null

    var mBusinessWorkExperienceAdapter: BusinessWorkExperienceAdapter? = null
    var mBusinessQualificationAdapter: BusinessQualificationAdapter? = null
    var mBusinessWorkPicAdapter: BusinessWorkPicAdapter? = null

    var mLoadingDialog: LoadingDialog? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.activity_new_resume

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

        mBusinessWorkExperienceAdapter = BusinessWorkExperienceAdapter(this,this)
        mRvWorkExperience.adapter = mBusinessWorkExperienceAdapter

        var flowLayoutManager = FlowLayoutManager()
        mRvQualification.layoutManager = flowLayoutManager

        mBusinessQualificationAdapter = BusinessQualificationAdapter(this,this)
        mRvQualification.adapter = mBusinessQualificationAdapter

        mBusinessWorkPicAdapter = BusinessWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mBusinessWorkPicAdapter

        mIvBack.setOnClickListener(this)
        mIvAuthentication.setOnClickListener(this)
        mClEducation.setOnClickListener(this)
        mTvAddWorkExperience.setOnClickListener(this)
        mTvAddQualification.setOnClickListener(this)
        mEtIntroduction.addTextChangedListener(this)
        mTvSave.setOnClickListener(this)
    }

    fun initData () {
        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            mIvAuthentication.visibility = View.GONE
        } else {
            mIvAuthentication.visibility = View.VISIBLE
        }

        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        if (count == 0) {
            mBusinessWorkPicAdapter?.add(WorkPicInfo())
            mBusinessWorkPicAdapter?.notifyDataSetChanged()
        }

        sendUploadConfigRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.ADD_WORK_EXPERIENCE)
                .observe(this, Observer {
                    mBusinessWorkExperienceAdapter?.add(it as ResumeWorkExperienceInfo)
                    mBusinessWorkExperienceAdapter?.notifyDataSetChanged()
                })

        LiveDataBus.with(BusinessActions.DELETE_WORK_EXPERIENCE)
                .observe(this, Observer {
                    var data = it as ResumeWorkExperienceInfo
                    mBusinessWorkExperienceAdapter?.removeItem(data.index)
                    mBusinessWorkExperienceAdapter?.notifyItemRemoved(data.index)
                })

        LiveDataBus.with(BusinessActions.UPDATE_WORK_EXPERIENCE)
                .observe(this, Observer {
                    var data = it as ResumeWorkExperienceInfo
                    mBusinessWorkExperienceAdapter?.removeItem(data.index)
                    mBusinessWorkExperienceAdapter?.add(data.index,data)
                    mBusinessWorkExperienceAdapter?.notifyDataSetChanged()
                })

        LiveDataBus.with(BusinessActions.ADD_QUALIFICATION)
                .observe(this, Observer {
                    mBusinessQualificationAdapter?.add(it as ResumeCertificateInfo)
                    mBusinessQualificationAdapter?.notifyDataSetChanged()
                })

        LiveDataBus.with(BusinessActions.UPDATE_QUALIFICATION)
                .observe(this, Observer {
                    var data = it as ResumeCertificateInfo
                    mBusinessQualificationAdapter?.removeItem(data.index)
                    mBusinessQualificationAdapter?.add(data.index,data)
                    mBusinessQualificationAdapter?.notifyDataSetChanged()
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

        resumeVM.saveResumeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("保存成功！")
                    LiveDataBus.send(JobActions.REFRESH_JOB_DETAIL)
                    UMengEventModule.report(this, BussinessTalentEvent.talent_new_resume)
                    val userInfo = App.get().getUserInfo()
                    if (userInfo?.realNameStatus == 0) {
                        showAuthTipDlg()
                        return@Observer
                    }
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
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

    fun sendSaveResumeRequest () {
        var education = mTvEducation.text.toString()
        var introduction = mEtIntroduction.text.toString()

        if (TextUtils.isEmpty(education)) {
            ToastUtils.show("请选择最高学历")
            return
        }

        var resumeInfo = ResumeInfo()
        resumeInfo.highestEducation = education
        resumeInfo.selfDescription = introduction


        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = SaveResumeParm()
        body.resumeInfo = resumeInfo
        body.resumeCertificates = mBusinessQualificationAdapter?.getDatas()
        body.resumeWorkExperiences = mBusinessWorkExperienceAdapter?.getDatas()
        body.resumeWorkPics = getWorkPics()

        mLoadingDialog?.show()
        resumeVM.saveResume(token,body)
    }

    fun getEducationPickerDialog (): EducationPickerDialog? {
        if (mEducationPickerDialog == null) {
            mEducationPickerDialog = EducationPickerDialog(this)

            mEducationPickerDialog?.mOnEducationSelectListener = this
            mEducationPickerDialog?.mEducation = "中专/高中"
        }
        return mEducationPickerDialog
    }

    fun getWorkPics(): List<WorkPicInfo>? {
        var workPics = ArrayList<WorkPicInfo>()
        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mBusinessWorkPicAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    workPics.add(it)
                }
            }
        }
        return workPics
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackAction()
            }
            R.id.mIvAuthentication -> {
                NavigationUtils.goRealNameActivity(this)
            }
            R.id.mClEducation -> {
                getEducationPickerDialog()?.show()
            }
            R.id.mTvAddWorkExperience -> {
                AddWorkExperienceActivity.intentStart(this,null)
            }
            R.id.mTvAddQualification -> {
                AddQualificationActivity.intentStart(this,null)
            }
            R.id.mTvSave -> {
                sendSaveResumeRequest()
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
                sendSaveResumeRequest()
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
                NavigationUtils.goRealNameActivity(this@NewResumeActivity)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    override fun OnEducationSelect(position: Int, education: String) {
        mTvEducation.text = education
    }

    override fun afterTextChanged(s: Editable?) {
        mTvIntroductionCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

                count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0

                var lastPosition = count - 1
                var isAddIcon = TextUtils.isEmpty(mBusinessWorkPicAdapter?.getItem(lastPosition)?.pic)

                if (!isAddIcon && count < 3) {
                    mBusinessWorkPicAdapter?.add(WorkPicInfo())
                }

                mBusinessWorkPicAdapter?.notifyItemRemoved(position)
            }
            R.id.mClWorkExperience -> {
                var data = mBusinessWorkExperienceAdapter?.getItem(position)
                data?.index = position
                AddWorkExperienceActivity.intentStart(this,data)
            }
            R.id.mClQualification -> {
                var data = mBusinessQualificationAdapter?.getItem(position)
                data?.index = position
                AddQualificationActivity.intentStart(this,data)
            }
            R.id.mIvQualificationDel -> {
                mBusinessQualificationAdapter?.removeItem(position)
                mBusinessQualificationAdapter?.notifyItemRemoved(position)
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

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }
}