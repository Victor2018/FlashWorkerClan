package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.BussinessEmployerEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.EmployerUnReadStatusReq
import com.flash.worker.lib.coremodel.data.req.InviteNumReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.EmployerJobVM
import com.flash.worker.lib.coremodel.viewmodel.JobInviteVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.*
import kotlinx.android.synthetic.main.fragment_employer.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: EmployerFragment
 * Author: Victor
 * Date: 2020/12/17 10:04
 * Description: 
 * -----------------------------------------------------------------
 */
class EmployerFragment :BaseFragment(), View.OnClickListener {

    companion object {
        fun newInstance(): EmployerFragment {
            //        Bundle bundle = new Bundle()
//        bundle.putSerializable(Constant.INTENT_DATA_KEY,data)
//        fragment.setArguments(bundle);
            return EmployerFragment()
        }
    }

    private val jobInviteVM: JobInviteVM by activityViewModels {
        InjectorUtils.provideJobInviteVMFactory(this)
    }

    private val employerJobVM: EmployerJobVM by activityViewModels {
        InjectorUtils.provideEmployerJobVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.fragment_employer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mTvEmployment.setOnClickListener(this)
        mTvInvitationSent.setOnClickListener(this)
        mTvTalentCollection.setOnClickListener(this)
        mTvEmployerProfile.setOnClickListener(this)
        mTvEmployerCredit.setOnClickListener(this)
        mTvEmploymentExpenses.setOnClickListener(this)
        mTvPendingAdmission.setOnClickListener(this)
        mTvToBeStarted.setOnClickListener(this)
        mRvWaitComment.setOnClickListener(this)
        mTvFinish.setOnClickListener(this)
        mTvEvaluationCenter.setOnClickListener(this)
        mTvComplaintHandling.setOnClickListener(this)
    }

    fun subscribeUi() {
        jobInviteVM.inviteNumData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showInviteNumData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        employerJobVM.unReadStatusData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showUnReadStatusData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun initData () {
        sendInviteNumRequest()
    }

    fun sendInviteNumRequest () {
        if (!App.get().hasLogin()) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        jobInviteVM.fetchInviteNum(token)
    }

    fun sendUnReadStatusRequest () {
        if (!App.get().hasLogin()) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        employerJobVM.fetchUnReadStatus(token)
    }

    fun showInviteNumData (data: InviteNumReq) {
        var receivedInviteNum = data.data?.sendInviteNum ?: 0
        mTvInvitationSentCount.text = receivedInviteNum.toString()

        if (receivedInviteNum > 100) {
            mTvInvitationSentCount.text = "···"
        }

        if (receivedInviteNum > 0) {
            mTvInvitationSentCount.visibility = View.VISIBLE
        } else {
            mTvInvitationSentCount.visibility = View.GONE
        }

        sendUnReadStatusRequest()
    }

    fun showUnReadStatusData(data: EmployerUnReadStatusReq) {
        var waitEmploymentListIsRead = data.data?.waitEmploymentListIsRead ?: false
        var employmentListIsRead = data.data?.employmentListIsRead ?: false
        var disputeListIsRead = data.data?.disputeListIsRead ?: false
        if (waitEmploymentListIsRead) {
            mViewEmployingTip.visibility = View.GONE
        } else {
            mViewEmployingTip.visibility = View.VISIBLE
        }
        if (employmentListIsRead) {
            mViewStartingTip.visibility = View.GONE
        } else {
            mViewStartingTip.visibility = View.VISIBLE
        }
        if (disputeListIsRead) {
            mViewComplaintsTip.visibility = View.GONE
        } else {
            mViewComplaintsTip.visibility = View.VISIBLE
        }

        var waitEmploymentListCount = data.data?.waitEmploymentListCount ?: 0
        if (waitEmploymentListCount > 0) {
            if (waitEmploymentListCount > 99) {
                mTvPendingAdmission.text = "雇用中(99+)"
            } else {
                mTvPendingAdmission.text = "雇用中(${waitEmploymentListCount})"
            }
        } else {
            mTvPendingAdmission.text = "雇用中"
        }

        var employmentListCount = data.data?.employmentListCount ?: 0
        if (employmentListCount > 0) {
            if (employmentListCount > 99) {
                mTvToBeStarted.text = "进行中(99+)"
            } else {
                mTvToBeStarted.text = "进行中(${employmentListCount})"
            }
        } else {
            mTvToBeStarted.text = "进行中"
        }

        var waitCommentListCount = data.data?.waitCommentListCount ?: 0
        if (waitCommentListCount > 0) {
            if (waitCommentListCount > 99) {
                mRvWaitComment.text = "待评价(99+)"
            } else {
                mRvWaitComment.text = "待评价(${waitCommentListCount})"
            }
        } else {
            mRvWaitComment.text = "待评价"
        }
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvEmployment -> {
                HireReleaseActivity.intentStart(activity as AppCompatActivity,0)
            }
            R.id.mTvInvitationSent -> {
                InvitationSentActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_invitation)
            }
            R.id.mTvTalentCollection -> {
                TalentFavoriteActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_talent_collection)
            }
            R.id.mTvEmployerProfile -> {
                MyEmployerActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvEmployerCredit -> {
                CreditScoreActivity.intentStart(activity as AppCompatActivity,true)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_credit)
            }
            R.id.mTvEmploymentExpenses -> {
                SalaryExpenseActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_expense)
            }
            R.id.mTvPendingAdmission -> {
                MyEmploymentActivity.intentStart(activity as AppCompatActivity,0)
            }
            R.id.mTvToBeStarted -> {
                MyEmploymentActivity.intentStart(activity as AppCompatActivity,1)
            }
            R.id.mRvWaitComment -> {
                MyEmploymentActivity.intentStart(activity as AppCompatActivity,2)
            }
            R.id.mTvFinish -> {
                MyEmploymentActivity.intentStart(activity as AppCompatActivity,3)
            }
            R.id.mTvEvaluationCenter -> {
                EmployerEvaluationCenterActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_evaluation_center)
            }
            R.id.mTvComplaintHandling -> {
                EmployerDisputeHandlingActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessEmployerEvent.employer_view_complaint_dispute)
            }
        }
    }
}