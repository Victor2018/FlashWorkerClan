package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ApplyEstablishGuildParm
import com.flash.worker.lib.coremodel.http.api.HtmlApi
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_apply_establish_guild.*

class ApplyEstablishGuildActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ApplyEstablishGuildActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.activity_apply_establish_guild

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mCvGuidanceNotes.setOnClickListener(this)
        mCvSignApplyForm.setOnClickListener(this)
        mCvApplyRecord.setOnClickListener(this)
    }

    fun subscribeUi() {
        guildVM.checkApplyData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        SignApplyFormActivity.intentStart(this)
                    } else {
                        if (it.value.data?.msgType == 1) {
                            showCheckApplyTipDlg(it.value.data?.msg)
                        } else {
                            showCheckApplyCommonTipDlg(it.value.data?.msg)
                        }
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendCheckApplyRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token
        guildVM.checkApply(token)
    }

    fun showCheckApplyTipDlg (msg: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = msg
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "去到首页"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                LiveDataBus.send(MineActions.BACK_GUILD_HALL_ACT)
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
                onBackPressed()
            }

        }
        commonTipDialog.show()
    }

    fun showCheckApplyCommonTipDlg (msg: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = msg
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mCvGuidanceNotes -> {
                WebActivity.intentStart(this,"公会成立须知",HtmlApi.GUILD_RULES)
            }
            R.id.mCvSignApplyForm -> {
                sendCheckApplyRequest()
            }
            R.id.mCvApplyRecord -> {
                ApplyRecordActivity.intentStart(this)
            }
        }
    }
}