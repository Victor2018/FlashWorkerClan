package com.flash.worker.module.mine.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.PhoneUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.GuildVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.activity.CancelAccountActivity
import com.flash.worker.module.mine.view.activity.JoinGuildActivity
import com.flash.worker.module.mine.view.activity.MyGuildActivity
import com.flash.worker.module.mine.view.activity.PresidentGuildActivity
import kotlinx.android.synthetic.main.activity_cancel_account.*
import kotlinx.android.synthetic.main.frag_cancel_account_notes.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: CancelAccountNotesFragment
 * Author: Victor
 * Date: 2021/5/19 17:04
 * Description: 
 * -----------------------------------------------------------------
 */
class CancelAccountNotesFragment: BaseFragment(),View.OnClickListener {
    
    companion object {

        fun newInstance(): CancelAccountNotesFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): CancelAccountNotesFragment {
            val fragment = CancelAccountNotesFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var userVM: UserVM
    private lateinit var guildVM: GuildVM

    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.frag_cancel_account_notes

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        userVM = ViewModelProvider(this, UserVMFactory(this))
                .get(UserVM::class.java)

        guildVM = ViewModelProvider(this, GuildVMFactory(this))
            .get(GuildVM::class.java)

        subscribeUi()

        mLoadingDialog = LoadingDialog(activity!!)

        mTvNext.setOnClickListener(this)
    }

    fun initData () {
        val userInfo = App.get().getUserInfo()
        mTvTip.text = "将${PhoneUtil.blurPhone(userInfo?.phone)}所绑定的账号注销"
    }

    fun subscribeUi () {
        userVM.checkCancelAccountData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var status = it.value.data?.status ?: false
                    if (status) {
                        var parentAct = activity as CancelAccountActivity
                        parentAct.mVpCancelAccount.currentItem = 1
                    } else {
                        showCheckCancelAccountTipDlg(it.value.data?.msg)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildVM.myGuildInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    var memberType = it.value.data?.memberType ?: 0
                    if (memberType == 1) {
                        showPresidentCancelAccouontTipDlg()
                        return@Observer
                    }
                    sendCheckCancelAccountRequest()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendCheckCancelAccountRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.checkCancelAccount(token)
    }

    fun sendMyGuildInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        guildVM.fetchMyGuildInfo(token)
    }

    fun showPresidentCancelAccouontTipDlg () {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "您是公会会长,\n如需注销账号请联系客服!"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                activity?.onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showCheckCancelAccountTipDlg (msg: String?) {
        var commonTipDialog = CommonTipDialog(activity!!)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = msg
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                activity?.onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvNext -> {
                sendMyGuildInfoRequest()
            }
        }
    }

}