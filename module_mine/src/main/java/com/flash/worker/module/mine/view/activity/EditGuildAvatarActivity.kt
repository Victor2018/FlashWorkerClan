package com.flash.worker.module.mine.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UpdateGuildAvatarParm
import com.flash.worker.lib.coremodel.data.req.UploadConfigReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.FileVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_edit_guild_avatar.*

class EditGuildAvatarActivity : BaseActivity(),View.OnClickListener, OnUploadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?,avatarUrl: String?) {
            var intent = Intent(activity, EditGuildAvatarActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,guildId)
            intent.putExtra(Constant.AVATAR_URL_KEY,avatarUrl)

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
    var uploadConfigReq: UploadConfigReq? = null
    var selectList: MutableList<LocalMedia> = ArrayList()

    var guildId: String? = null
    var avatarUrl: String? = null

    override fun getLayoutResource() = R.layout.activity_edit_guild_avatar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvEditAvatar.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        avatarUrl = intent?.getStringExtra(Constant.AVATAR_URL_KEY)

        ImageUtils.instance.loadImage(this,mCivAvatar,avatarUrl,R.mipmap.ic_president_avatar)
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
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        guildVM.updateGuildAvatarData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("公会头像修改成功")
                    ImageUtils.instance.loadImage(this,mCivAvatar,avatarUrl)
                    LiveDataBus.send(MineActions.UPDATE_GUILD_AVATAR_SUCCESS,avatarUrl)

                    UMengEventModule.report(this, MineEvent.edit_guild_avatar)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
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

    fun sendUpdateGuildAvatarRequest (url: String?) {
        avatarUrl = url

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = UpdateGuildAvatarParm()
        body.guildId = guildId
        body.headpic = url

        guildVM.updateGuildAvatar(token,body)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvEditAvatar -> {
                PictureSelectorUtil.selectMedia(this,false,true,true,1)
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
                Loger.e(BaseFragment.TAG,"url = $url")
                sendUpdateGuildAvatarRequest(url)
            } else {
                ToastUtils.show("图片上传失败-error = $url")
            }
        }
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