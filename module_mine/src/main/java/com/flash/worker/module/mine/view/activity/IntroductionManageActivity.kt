package com.flash.worker.module.mine.view.activity

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
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.module.OssUploadModule
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.GuildDetailData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.UpdateGuildIntroductionParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildImageAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_fill_guild_introduction.*
import kotlinx.android.synthetic.main.frag_introduction_manage.*

class IntroductionManageActivity : BaseActivity(),View.OnClickListener, TextWatcher, OnUploadListener,
    AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: GuildDetailData?) {
            var intent = Intent(activity, IntroductionManageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val fileVM: FileVM by viewModels {
        InjectorUtils.provideFileVMFactory(this)
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildDetailData: GuildDetailData? = null
    var mGuildImageAdapter: GuildImageAdapter? = null

    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    override fun getLayoutResource() = R.layout.activity_introduction_manage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mGuildImageAdapter = GuildImageAdapter(this,this)
        mRvImage.adapter = mGuildImageAdapter

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mEtWorkIntroduction.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        mGuildDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as GuildDetailData?
        mEtWorkIntroduction.setText(mGuildDetailData?.guildProfile)

        mGuildImageAdapter?.clear()
        mGuildImageAdapter?.add(getPics(mGuildDetailData?.profilePics))
        mGuildImageAdapter?.notifyDataSetChanged()

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

        guildVM.updateGuildIntroductionData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("公会简介修改成功")

                    UMengEventModule.report(this, MineEvent.edit_guild_introduction)
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

    fun sendUpdateGuildIntroductionRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var guildProfile = mEtWorkIntroduction.text.toString()
        if (TextUtils.isEmpty(guildProfile)) {
            ToastUtils.show("请输入公会简介")
            return
        }

        mLoadingDialog?.show()

        val body = UpdateGuildIntroductionParm()
        body.guildId = mGuildDetailData?.guildId
        body.guildProfile = guildProfile
        body.profilePics = getGuildPics()

        guildVM.updateGuildIntroduction(token,body)
    }

    fun getPics(urls: List<String>?): ArrayList<WorkPicInfo> {
        var pics = ArrayList<WorkPicInfo>()
        urls?.let {
            urls?.forEach {
                var item = WorkPicInfo()
                item.pic = it
                pics.add(item)
            }
        }
        if (pics.size < 3) {
            pics.add(WorkPicInfo())
        }
        return pics
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
               sendUpdateGuildIntroductionRequest()
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var count = mGuildImageAdapter?.itemCount ?: 0
        when (view?.id) {
            R.id.mClCertificateRoot -> {
                if (position == count - 1) {
                    var maxCount = 4 - count
                    if (maxCount == 0) {
                        ToastUtils.show("最多添加3张")
                        return
                    }

                    PictureSelectorUtil.selectMedia(this,false,true,false,maxCount)
                }
            }
            R.id.mIvRelatedCertificateDel -> {
                mGuildImageAdapter?.removeItem(position)

                var lastPosition = count - 1
                var isAddIcon = TextUtils.isEmpty(mGuildImageAdapter?.getItem(lastPosition)?.pic)

                if (!isAddIcon && count < 4) {
                    mGuildImageAdapter?.add(WorkPicInfo())
                }
                mGuildImageAdapter?.notifyItemRemoved(position)
            }
        }
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
        return guildPics
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

    override fun OnUpload(progress: Int, url: String?, complete: Boolean) {
        mLoadingDialog?.dismiss()
        if (complete) {
            if (progress > 0) {
                Loger.e(TAG,"url = $url")

                var data = WorkPicInfo()
                data.pic = url
                var count = mGuildImageAdapter?.getContentItemCount() ?: 0
                mGuildImageAdapter?.removeItem(count - 1)
                mGuildImageAdapter?.add(data)
                if (count < 3) {
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

    override fun onDestroy() {
        super.onDestroy()
        OssUploadModule.instance.onDestroy()
    }
}