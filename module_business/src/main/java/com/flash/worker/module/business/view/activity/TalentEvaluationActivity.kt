package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.parm.CommentEmployerParm
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TalentWaitCommentInfo
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_talent_evaluation.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentEvaluationActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentEvaluationActivity: BaseActivity(),View.OnClickListener,RadioGroup.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null

    var mTalentWaitCommentInfo: TalentWaitCommentInfo? = null

    var mLabel: Int = 1//默认很好

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: TalentWaitCommentInfo?) {
            var intent = Intent(activity, TalentEvaluationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_talent_evaluation

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvEvaluationRules.setOnClickListener(this)
        mTvSubmitEvaluation.setOnClickListener(this)

        mRgEvaluation.setOnCheckedChangeListener(this)
    }

    fun initData (intent: Intent?) {
        val data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data != null) {
            mTalentWaitCommentInfo = (data as TalentWaitCommentInfo)

            var company = mTalentWaitCommentInfo?.employerName

            var identity = ""
            if (mTalentWaitCommentInfo?.identity == 1) {
                identity = "企业"
            } else if (mTalentWaitCommentInfo?.identity == 2) {
                identity = "商户"
            } else if (mTalentWaitCommentInfo?.identity == 3) {
                identity = "个人"
            }

            mTvTip.text = "尊敬的人才：\n\t\t\t\t请给$company($identity)一个中肯的评价吧！"
        }
    }

    fun subscribeUi() {
        commentVM.commentEmployerData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    EvaluationSuccessActivity.intentStart(this,true)
                    UMengEventModule.report(this, BussinessTalentEvent.talent_evaluation_employer)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendCommentEmployerRequest () {
        var content = mEtEvaluation.text.toString()
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show("请输入评价内容")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CommentEmployerParm()
        body.anonymous = mChkCheck.isChecked
        body.label = mLabel
        body.content = content
        body.employerReleaseId = mTalentWaitCommentInfo?.employerReleaseId
        body.jobOrderId = mTalentWaitCommentInfo?.id

        commentVM.commentEmployer(token,body)
    }

    fun showEvaluationTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "确定退出评价吗？"
        commonTipDialog.mContent = "退出后已填写的内容将不被保存哟！"
        commonTipDialog.mCancelText = "退出"
        commonTipDialog.mOkText = "继续评价"
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
            R.id.mIvBack -> {
                showEvaluationTipDlg()
            }
            R.id.mTvEvaluationRules -> {
                EvaluationRulesActivity.intentStart(this)
                UMengEventModule.report(this, BussinessTalentEvent.talent_view_evaluation_rules)
            }
            R.id.mTvSubmitEvaluation -> {
                sendCommentEmployerRequest()
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.mRbVeryGood -> {
                mLabel = 1
            }
            R.id.mRbGeneral -> {
                mLabel = 2
            }
            R.id.mRbVeryBad -> {
                mLabel = 3
            }
        }
    }

}