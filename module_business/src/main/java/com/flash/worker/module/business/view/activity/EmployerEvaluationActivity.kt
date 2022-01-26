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
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentInfo
import com.flash.worker.lib.coremodel.data.bean.EmployerWaitCommentUserInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.CommentAllTalentParm
import com.flash.worker.lib.coremodel.data.parm.CommentTalentParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommentVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import kotlinx.android.synthetic.main.activity_employer_evaluation.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerEvaluationActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerEvaluationActivity: BaseActivity(),View.OnClickListener,RadioGroup.OnCheckedChangeListener {

    var mLoadingDialog: LoadingDialog? = null

    var mEmployerWaitCommentInfo: EmployerWaitCommentInfo? = null
    var waitCommentUsers: ArrayList<EmployerWaitCommentUserInfo>? = null

    var mLabel: Int = 1//默认很好

    private val commentVM: CommentVM by viewModels {
        InjectorUtils.provideCommentVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: EmployerWaitCommentInfo?,
                          datas: ArrayList<EmployerWaitCommentUserInfo>?) {
            var intent = Intent(activity, EmployerEvaluationActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            intent.putExtra(Constant.WAIT_COMMENT_USER_KEY,datas)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_employer_evaluation

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
        mEmployerWaitCommentInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployerWaitCommentInfo?
        waitCommentUsers = intent?.getSerializableExtra(Constant.WAIT_COMMENT_USER_KEY)
                as ArrayList<EmployerWaitCommentUserInfo>?

        if (waitCommentUsers == null || waitCommentUsers?.size == 0) {
            mTvTip.text = String.format("尊敬的雇主：\n\t\t\t\t请给人才一个中肯的评价吧！")
        } else {
            if (waitCommentUsers?.size == 1) {
                mTvTip.text = String.format("尊敬的雇主：\n\t\t\t\t请给%s一个中肯的评价吧！",
                    waitCommentUsers?.get(0)?.username)
            } else {
                mTvTip.text = String.format("尊敬的雇主：\n\t\t\t\t请给人才一个中肯的评价吧！\n" +
                        "\t\t\t\t本次评价人数：%d人",waitCommentUsers?.size)
            }
        }

    }

    fun subscribeUi() {
        commentVM.commentAllTalentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    EvaluationSuccessActivity.intentStart(this,false)
                    LiveDataBus.send(BusinessActions.REFRESH_E_WAIT_COMMENT_USER)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        commentVM.commentTalentData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    EvaluationSuccessActivity.intentStart(this,false)
                    LiveDataBus.send(BusinessActions.REFRESH_E_WAIT_COMMENT_USER)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendCommentAllTalentRequest () {
        var content = mEtEvaluation.text.toString()
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show("请输入评价内容")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CommentAllTalentParm()
        body.anonymous = mChkCheck.isChecked
        body.label = mLabel
        body.content = content
        body.employerReleaseId = mEmployerWaitCommentInfo?.employerReleaseId

        commentVM.commentAllTalent(token,body)
    }

    fun sendCommentTalentRequest () {
        var content = mEtEvaluation.text.toString()
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show("请输入评价内容")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = CommentTalentParm()
        body.anonymous = mChkCheck.isChecked
        body.label = mLabel
        body.content = content
        body.employerReleaseId = mEmployerWaitCommentInfo?.employerReleaseId
        body.jobOrderIds = getJobOrderIds()

        commentVM.commentTalent(token,body)
    }

    fun getJobOrderIds (): List<String> {
        var jobOrderIds = ArrayList<String>()
        waitCommentUsers?.forEach {
            it.jobOrderId?.let { it1 -> jobOrderIds.add(it1) }
        }

        return jobOrderIds
    }

    fun showEvaluationTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "确定退出评价吗？"
        commonTipDialog.mContent = "退出后已填写的内容将不被保存哟"
        commonTipDialog.mCancelText = "退出评价"
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
            }
            R.id.mTvSubmitEvaluation -> {
                if (waitCommentUsers == null || waitCommentUsers?.size == 0) {
                    sendCommentAllTalentRequest()
                } else {
                    sendCommentTalentRequest()
                }
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