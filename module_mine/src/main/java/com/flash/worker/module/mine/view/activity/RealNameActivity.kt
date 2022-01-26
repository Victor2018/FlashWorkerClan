package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.coremodel.util.AppUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.LatestVersionParm
import com.flash.worker.lib.coremodel.data.parm.RealNameParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UpdateVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_real_name.*

@Route(path = ARouterPath.RealNameAct)
class RealNameActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, RealNameActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.activity_real_name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvAuth.setOnClickListener(this)
    }

    fun subscribeUi () {
        userVM.realNameData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    val userInfo = App.get().getUserInfo()
                    userInfo?.realNameStatus = 1
                    App.get().setUserInfo(userInfo)
                    ToastUtils.show("认证成功")
                    LiveDataBus.send(JobActions.REFRESH_JOB_DETAIL)
                    UMengEventModule.report(this, BussinessTalentEvent.real_name_authentication_succeeded)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendRealNameRequest () {
        if (!App.get().hasLogin()) return

        var realName = mEtRealName.text.toString()
        if (TextUtils.isEmpty(realName)) {
            ToastUtils.show("请输入您的姓名")
            return
        }
        var idcard = mEtIdCard.text.toString()
        if (TextUtils.isEmpty(idcard)) {
            ToastUtils.show("请输入您的身份证号码")
            return
        }

        val token = App.get().getLoginReq()?.data?.token

        mLoadingDialog?.show()

        var body = RealNameParm()
        body.realName = realName
        body.idcard = idcard

        userVM.realName(token,body)
    }

    fun showBackTipDlg () {
        val userInfo = App.get().getUserInfo()
        if (userInfo?.realNameStatus == 1) {
            onBackPressed()
            return
        }
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您好，您还未进行实名认证，将不能发布信息哟！"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "继续认证"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                showBackTipDlg()
            }
            R.id.mTvAuth -> {
                sendRealNameRequest()
            }
        }
    }

}