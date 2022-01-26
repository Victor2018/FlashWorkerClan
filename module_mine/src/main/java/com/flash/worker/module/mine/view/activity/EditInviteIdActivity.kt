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
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UpdateEmergencyContactParm
import com.flash.worker.lib.coremodel.data.bean.UserInfo
import com.flash.worker.lib.coremodel.data.parm.UpdateInviteUserParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_edit_invite_id.*

class EditInviteIdActivity : BaseActivity(),View.OnClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mUserInfo: UserInfo? = null

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EditInviteIdActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_edit_invite_id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvComplete.setOnClickListener(this)

    }

    fun initData () {
        mUserInfo = App.get().getUserInfo()
        mEtInviteId.setText(mUserInfo?.inviterUserId)
    }

    fun subscribeUi() {
        userVM.updateInviteUserData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("修改成功")
                    mUserInfo?.inviterUserId = mEtInviteId.text.toString()
                    App.get().setUserInfo(mUserInfo)

                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendUpdateInviteUserRequest () {
        var inviteId = mEtInviteId.text.toString()
        if (TextUtils.isEmpty(inviteId)) {
            ToastUtils.show("请输入邀请人ID号")
            return
        }
        mLoadingDialog?.show()
        val token =  App.get().getLoginReq()?.data?.token

        var body = UpdateInviteUserParm()
        body.inviterUserId = inviteId

        userVM.updateInviteUser(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvComplete -> {
                sendUpdateInviteUserRequest()
            }
        }
    }


}