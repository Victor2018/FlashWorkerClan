package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UpdateEmergencyContactParm
import com.flash.worker.lib.coremodel.data.bean.UserInfo
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_edit_emergency_contact.*

class EditEmergencyContactActivity : BaseActivity(),View.OnClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mUserInfo: UserInfo? = null

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EditEmergencyContactActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_edit_emergency_contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialzie()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialzie () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvComplete.setOnClickListener(this)

    }

    fun initData () {
        mUserInfo = App.get().getUserInfo()
        mEtEmergencyContact.setText(mUserInfo?.contactPhone)
    }

    fun subscribeUi() {
        userVM.updateEmergencyContactData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("紧急联系人电话修改成功")
                    mUserInfo?.contactPhone = mEtEmergencyContact.text.toString()
                    App.get().setUserInfo(mUserInfo)

                    UMengEventModule.report(this, MineEvent.edit_emergency_contact)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendUpdateEmergencyContactRequest () {
        var phone = mEtEmergencyContact.text.toString()
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("请输入紧急联系人电话")
            return
        }
        mLoadingDialog?.show()
        val token =  App.get().getLoginReq()?.data?.token

        var body = UpdateEmergencyContactParm()
        body.contactPhone = phone

        userVM.updateEmergencyContact(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvComplete -> {
                sendUpdateEmergencyContactRequest()
            }
        }
    }


}