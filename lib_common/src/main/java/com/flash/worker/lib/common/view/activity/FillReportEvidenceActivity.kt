package com.flash.worker.lib.common.view.activity

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.JobEvent
import com.flash.worker.lib.common.event.TalentEvent
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.RelatedCertificateAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ViolationLabelInfo
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.ReportEmployerViolationParm
import com.flash.worker.lib.coremodel.data.parm.ReportTalentViolationParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.ViolationReportVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_fill_report_evidence.*

class FillReportEvidenceActivity : BaseActivity(), View.OnClickListener, TextWatcher, OnUploadListener,
        AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: ViolationLabelInfo?,releaseId: String?) {
            var intent = Intent(activity, FillReportEvidenceActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.RELEASE_ID_KEY,releaseId)
            activity.startActivity(intent)
        }
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val violationReportVM: ViolationReportVM by viewModels {
        InjectorUtils.provideViolationReportVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mRelatedCertificateAdapter: RelatedCertificateAdapter? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var mViolationLabelInfo: ViolationLabelInfo? = null
    var releaseId: String? = null

    override fun getLayoutResource() = R.layout.activity_fill_report_evidence

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mRelatedCertificateAdapter = RelatedCertificateAdapter(this, this)
        mRvCertificate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        mRvCertificate.adapter = mRelatedCertificateAdapter

        mIvBack.setOnClickListener(this)
        mTvSubmit.setOnClickListener(this)

        mEtDescription.addTextChangedListener(this)
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
                    ToastUtils.show(it.message)
                }
            }
        })

        violationReportVM.reportTalentViolationData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("检举成功")
                    UMengEventModule.report(this,TalentEvent.report_talent_violation)
                    LiveDataBus.send(BusinessActions.BACK_VIOLATION_REPORT)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        violationReportVM.reportEmployerViolationData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("检举成功")
                    UMengEventModule.report(this,JobEvent.report_employer_violation)
                    LiveDataBus.send(BusinessActions.BACK_VIOLATION_REPORT)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun initData (intent: Intent?) {
        mViolationLabelInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as ViolationLabelInfo?
        releaseId = intent?.getStringExtra(Constant.RELEASE_ID_KEY)

        Loger.e(TAG,"initData-releaseId = $releaseId")

        mTvTitle.text = mViolationLabelInfo?.name

        mRelatedCertificateAdapter?.add(WorkPicInfo())
        mRelatedCertificateAdapter?.notifyDataSetChanged()

        sendUploadConfigRequest()
    }

    fun sendUploadConfigRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        fileVM.fetchUploadConfigData(token)
    }

    fun sendReportTalentViolationRequest () {
        var reportDesc = mEtDescription.text.toString()
        if (TextUtils.isEmpty(reportDesc)) {
            ToastUtils.show("请填写具体情况说明")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ReportTalentViolationParm()
        body.labelId = mViolationLabelInfo?.id
        body.talentReleaseId = releaseId
        body.reportDesc = reportDesc
        body.pics = getReportPics()

        violationReportVM.reportTalentViolation(token,body)
    }

    fun sendReportEmployerViolationRequest () {
        var reportDesc = mEtDescription.text.toString()
        if (TextUtils.isEmpty(reportDesc)) {
            ToastUtils.show("请填写具体情况说明")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ReportEmployerViolationParm()
        body.labelId = mViolationLabelInfo?.id
        body.employerReleaseId = releaseId
        body.reportDesc = reportDesc
        body.pics = getReportPics()

        violationReportVM.reportEmployerViolation(token,body)
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
                onBackPressed()
            }
            R.id.mTvSubmit -> {
                var isReportTalentViolation = mViolationLabelInfo?.isReportTalentViolation ?: false
                if (isReportTalentViolation) {
                    sendReportTalentViolationRequest()
                } else {
                    sendReportEmployerViolationRequest()
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvDescriptionCount.text = "${s?.length}/500"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mClCertificateRoot -> {
                if (position == mRelatedCertificateAdapter?.itemCount!! - 1) {
                    var maxCount = 4 - mRelatedCertificateAdapter?.itemCount!!
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加3张")
                        return
                    }

                    PictureSelectorUtil.selectMedia(this,false,true,false,maxCount)
                }
            }
            R.id.mIvRelatedCertificateDel -> {
                mRelatedCertificateAdapter?.removeItem(position)

                var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
                var lastPosition = count - 1
                var isAddIcon = TextUtils.isEmpty(mRelatedCertificateAdapter?.getItem(lastPosition)?.pic)

                if (!isAddIcon && mRelatedCertificateAdapter?.itemCount!! < 3) {
                    mRelatedCertificateAdapter?.add(WorkPicInfo())
                }
                mRelatedCertificateAdapter?.notifyItemRemoved(position)
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
                mRelatedCertificateAdapter?.removeItem(mRelatedCertificateAdapter?.itemCount!! - 1)
                mRelatedCertificateAdapter?.add(data)
                var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
                if (count < 3) {
                    mRelatedCertificateAdapter?.add(WorkPicInfo())
                }
                mRelatedCertificateAdapter?.notifyDataSetChanged()

                if (selectList != null && selectList.size > 1) {
                    selectList.removeAt(0)
                    uploadImgae2Oss()
                }
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
    }

    fun getReportPics(): List<String>? {
        var reportPics = ArrayList<String>()
        var count = mRelatedCertificateAdapter?.getContentItemCount() ?: 0
        if (count > 0) {
            mRelatedCertificateAdapter?.getDatas()?.forEach {
                if (!TextUtils.isEmpty(it.pic)) {
                    reportPics.add(it.pic!!)
                }
            }
        }
        return reportPics
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