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
import com.flash.worker.lib.coremodel.data.parm.UpdateUserNameParm
import com.flash.worker.lib.coremodel.data.bean.UserInfo
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_edit_user_name.*

class EditUserNameActivity : BaseActivity(),View.OnClickListener {

    var mLoadingDialog: LoadingDialog? = null
    var mUserInfo: UserInfo? = null

    private val userVM: UserVM by viewModels {
        InjectorUtils.provideUserVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, EditUserNameActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_edit_user_name

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
        mEtUserName.setText(mUserInfo?.username)
    }

    fun subscribeUi() {
        userVM.updateUserNameData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("闪工名修改成功")
                    mUserInfo?.username = mEtUserName.text.toString()
                    App.get().setUserInfo(mUserInfo)

                    UMengEventModule.report(this, MineEvent.edit_name)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

    }

    fun sendUpdateUserNameRequest () {
        var userName = mEtUserName.text.toString()
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.show("请输入闪工名")
            return
        }

        if (userName.length < 2) {
            ToastUtils.show("闪工名为2~6个中文字符")
            return
        }

        mLoadingDialog?.show()

        val token =  App.get().getLoginReq()?.data?.token

        var body = UpdateUserNameParm()
        body.username = userName

        userVM.updateUserName(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvComplete -> {
                sendUpdateUserNameRequest()
            }
        }
    }


}