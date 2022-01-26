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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
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
import com.flash.worker.lib.coremodel.data.parm.UpdateJobResumeParm
import com.flash.worker.lib.coremodel.data.req.ResumeDetailReq
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.ResumeVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.BusinessQualificationAdapter
import com.flash.worker.module.business.view.adapter.BusinessWorkExperienceAdapter
import com.flash.worker.lib.common.view.adapter.BusinessWorkPicAdapter
import com.flash.worker.module.business.view.dialog.EducationPickerDialog
import com.flash.worker.module.business.interfaces.OnEducationSelectListener
import com.library.flowlayout.FlowLayoutManager
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.interfaces.OnResumeCopyListener
import com.flash.worker.module.business.view.dialog.CopyResumeDialog
import kotlinx.android.synthetic.main.activity_resume_detail.*

class ResumeDetailActivity : BaseActivity(),View.OnClickListener, OnEducationSelectListener,
        AdapterView.OnItemClickListener, OnUploadListener, TextWatcher,
        SwipeRefreshLayout.OnRefreshListener, OnResumeCopyListener {

    var mEducationPickerDialog: EducationPickerDialog? = null

    var mBusinessWorkExperienceAdapter: BusinessWorkExperienceAdapter? = null
    var mBusinessQualificationAdapter: BusinessQualificationAdapter? = null
    var mBusinessWorkPicAdapter: BusinessWorkPicAdapter? = null

    var mLoadingDialog: LoadingDialog? = null
    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var mMyResumeInfo: MyResumeInfo? = null
    var mResumeDetailData: ResumeDetailData? = null
    var resumeCount = 0

    private val resumeVM: ResumeVM by viewModels {
        InjectorUtils.provideResumeVMFactory(this)
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: MyResumeInfo?,resumeCount: Int) {
            var intent = Intent(activity, ResumeDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.RESUME_COUNT_KEY,resumeCount)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_resume_detail

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
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mBusinessWorkExperienceAdapter = BusinessWorkExperienceAdapter(this,this)
        mRvWorkExperience.adapter = mBusinessWorkExperienceAdapter

        var flowLayoutManager = FlowLayoutManager()
        mRvQualification.layoutManager = flowLayoutManager

        mBusinessQualificationAdapter = BusinessQualificationAdapter(this,this)
        mRvQualification.adapter = mBusinessQualificationAdapter

        mBusinessWorkPicAdapter = BusinessWorkPicAdapter(this,this)
        mRvWorksPic.adapter = mBusinessWorkPicAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvCopy.setOnClickListener(this)
        mIvAuthentication.setOnClickListener(this)
        mTvDelete.setOnClickListener(this)
        mClEducation.setOnClickListener(this)
        mTvAddWorkExperience.setOnClickListener(this)
        mTvAddQualification.setOnClickListener(this)
        mTvSave.setOnClickListener(this)

        mEtIntroduction.addTextChangedListener(this)

    }

    fun initData (intent: Intent?) {
        mMyResumeInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as MyResumeInfo?
        resumeCount = intent?.getIntExtra(Constant.RESUME_COUNT_KEY,0) ?: 0

        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            mIvAuthentication.visibility = View.GONE
            line_authentication.visibility = View.GONE
        } else {
            mIvAuthentication.visibility = View.VISIBLE
            line_authentication.visibility = View.VISIBLE
        }

        if (mResumeDetailData == null) {
            sendResumeDetailRequest()
        }
    }

    fun subscribeUi () {
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

        resumeVM.resumeDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showResumeDetail(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        resumeVM.updateJobResumeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("保存成功")
                    UMengEventModule.report(this, BussinessTalentEvent.talent_update_resume)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        resumeVM.deleteResumeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("简历删除成功！")
                    UMengEventModule.report(this, BussinessTalentEvent.talent_delete_resume)
                    onBackPressed()
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
                    ToastUtils.show("简历复制成功！")
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
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

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendResumeDetailRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        resumeVM.fetchResumeDetail(token,mMyResumeInfo?.id)
    }

    fun sendUpdateJobResumeRequest () {
        var resumeName = mEtResumeName.text.toString()
        var education = mTvEducation.text.toString()
        var introduction = mEtIntroduction.text.toString()

        if (TextUtils.isEmpty(resumeName)) {
            ToastUtils.show("请输入2~7个字符的简历名称")
            return
        }
        if (resumeName.length < 2) {
            ToastUtils.show("请输入2~7个字符的简历名称")
            return
        }

        if (TextUtils.isEmpty(education)) {
            ToastUtils.show("请选择最高学历")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var resumeInfo = ResumeInfo()
        resumeInfo.highestEducation = education
        resumeInfo.selfDescription = introduction
        resumeInfo.id = mResumeDetailData?.resumeInfo?.id
        resumeInfo.name = resumeName

        var body = UpdateJobResumeParm()
        body.resumeWorkExperiences = mBusinessWorkExperienceAdapter?.getDatas()
        body.resumeCertificates = mBusinessQualificationAdapter?.getDatas()
        body.resumeWorkPics = getWorkPics()

        body.resumeInfo = resumeInfo

        resumeVM.updateJobResume(token,body)
    }

    fun sendDeleteResumeRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        resumeVM.deleteResume(token,mMyResumeInfo?.id)
    }

    fun sendSaveResumeRequest (resumeName: String?) {
        mResumeDetailData?.resumeInfo?.name = resumeName

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var resumeInfo = mResumeDetailData?.resumeInfo
        resumeInfo?.name = resumeName

        var body = SaveResumeParm()
        body.resumeInfo = resumeInfo
        body.resumeCertificates = mResumeDetailData?.resumeCertificates
        body.resumeWorkExperiences = mResumeDetailData?.resumeWorkExperiences
        body.resumeWorkPics = mResumeDetailData?.resumeWorkPics

        mLoadingDialog?.show()
        resumeVM.saveResume(token,body)
    }

    fun showResumeDetail (data: ResumeDetailReq) {
        var modifyStatus = data.data?.modifyStatus ?: false
        if (!modifyStatus) {
            ToastUtils.show(data.data?.modifyTips)
        }
        mResumeDetailData = data.data

        mEtResumeName.setText(mResumeDetailData?.resumeInfo?.name)
        mTvEducation.text = mResumeDetailData?.resumeInfo?.highestEducation

        mBusinessWorkExperienceAdapter?.clear()
        mBusinessWorkExperienceAdapter?.add(mResumeDetailData?.resumeWorkExperiences)
        mBusinessWorkExperienceAdapter?.notifyDataSetChanged()

        mBusinessQualificationAdapter?.clear()
        mBusinessQualificationAdapter?.add(mResumeDetailData?.resumeCertificates)
        mBusinessQualificationAdapter?.notifyDataSetChanged()

        mBusinessWorkPicAdapter?.clear()
        mBusinessWorkPicAdapter?.add(mResumeDetailData?.resumeWorkPics)

        var picCount = mResumeDetailData?.resumeWorkPics?.size ?: 0
        if (picCount < 3) {
            mBusinessWorkPicAdapter?.add(WorkPicInfo())
        }
        mBusinessWorkPicAdapter?.notifyDataSetChanged()

        mEtIntroduction.setText(mResumeDetailData?.resumeInfo?.selfDescription)

        mTvIntroductionCount.text = "${mResumeDetailData?.resumeInfo?.selfDescription?.length}/200"
    }

    fun getEducationPickerDialog (): EducationPickerDialog? {
        if (mEducationPickerDialog == null) {
            mEducationPickerDialog = EducationPickerDialog(this)
            mEducationPickerDialog?.mOnEducationSelectListener = this
        }
        mEducationPickerDialog?.mEducation = mResumeDetailData?.resumeInfo?.highestEducation
        return mEducationPickerDialog
    }

    fun getWorkPics (): List<WorkPicInfo> {
        var workPics = ArrayList<WorkPicInfo>()
        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        if (count > 1) {
            mBusinessWorkPicAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    var data = WorkPicInfo()
                    data.pic = it.pic
                    workPics.add(data)
                }
            }
        }

        return workPics
    }

    fun showDeleteTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "删除简历后，已完成工单和收藏该简历的雇主将无法查看简历，请谨慎删除!"
        commonTipDialog.mCancelText = "暂不删除"
        commonTipDialog.mOkText = "确定删除"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendDeleteResumeRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
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

    fun showAddResumeTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您的简历数已达上限可删除后再新增！"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCopyResumeDlg () {
        var copyResumeDialog = CopyResumeDialog(this)
        copyResumeDialog.mOnResumeCopyListener = this
        copyResumeDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvCopy -> {
                if (resumeCount >= 5) {
                    showAddResumeTipDlg()
                    return
                }
                showCopyResumeDlg()
            }
            R.id.mIvAuthentication -> {
                NavigationUtils.goRealNameActivity(this)
            }
            R.id.mTvDelete -> {
                showDeleteTipDlg()
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
                sendUpdateJobResumeRequest()
            }
        }
    }

    override fun OnEducationSelect(position: Int, education: String) {
        mResumeDetailData?.resumeInfo?.highestEducation = education
        mTvEducation.text = education
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var count = mBusinessWorkPicAdapter?.getContentItemCount() ?: 0
        when (view?.id) {
            R.id.mIvWorkPic,R.id.mIvWorkPicBorder  -> {
                if (position == count - 1) {
                    var picUrl = mBusinessWorkPicAdapter?.getItem(position)?.pic
                    if (!TextUtils.isEmpty(picUrl)) return //已经添加3张图片最后一张图片不是添加按钮
                    var maxCount = 4 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加3张")
                        return
                    }
                    PictureSelectorUtil.selectMedia(this,false,true,
                        false,maxCount)
                }
            }
            R.id.mIvWorkPicDel ->{
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

    override fun afterTextChanged(s: Editable?) {
        mTvIntroductionCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun OnResumeCopy(resumeName: String?) {
        sendSaveResumeRequest(resumeName)
    }

    override fun onRefresh() {
        sendResumeDetailRequest()
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