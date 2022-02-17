package com.flash.worker.module.business.view.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.BussinessTalentEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.InviteNumReq
import com.flash.worker.lib.coremodel.data.req.TalentUnReadStatusReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.JobInviteVM
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.activity.*
import kotlinx.android.synthetic.main.fragment_talent.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: TalentFragment
 * Author: Victor
 * Date: 2020/12/17 10:04
 * Description: 
 * -----------------------------------------------------------------
 */
class TalentFragment :BaseFragment(),View.OnClickListener {

    companion object {
        fun newInstance(): TalentFragment {
            //        Bundle bundle = new Bundle()
//        bundle.putSerializable(Constant.INTENT_DATA_KEY,data)
//        fragment.setArguments(bundle)
            return TalentFragment()
        }
    }

    private val jobInviteVM: JobInviteVM by activityViewModels {
        InjectorUtils.provideJobInviteVMFactory(this)
    }

    private val talentJobVM: TalentJobVM by activityViewModels {
        InjectorUtils.provideTalentJobVMFactory(this)
    }

    override fun getLayoutResource() = R.layout.fragment_talent

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

        mTvTalent.setOnClickListener(this)
        mTvReceivedInvitation.setOnClickListener(this)
        mTvMyFocus.setOnClickListener(this)
        mTvMyResume.setOnClickListener(this)
        mTvMyCredit.setOnClickListener(this)
        mTvMyIncome.setOnClickListener(this)
        mTvPendingAdmission.setOnClickListener(this)
        mTvToBeStarted.setOnClickListener(this)
        mRvWaitComment.setOnClickListener(this)
        mTvFinish.setOnClickListener(this)
        mTvEvaluationCenter.setOnClickListener(this)
        mTvComplaints.setOnClickListener(this)

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

        talentJobVM.unReadStatusData.observe(viewLifecycleOwner, Observer {
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

        talentJobVM.fetchUnReadStatus(token)
    }

    fun showInviteNumData (data: InviteNumReq) {
        var receivedInviteNum = data.data?.receivedInviteNum ?: 0
        mTvReceivedInvitationCount.text = receivedInviteNum.toString()

        if (receivedInviteNum > 100) {
            mTvReceivedInvitationCount.text = "···"
        }

        if (receivedInviteNum > 0) {
            mTvReceivedInvitationCount.visibility = View.VISIBLE
        } else {
            mTvReceivedInvitationCount.visibility = View.GONE
        }

        sendUnReadStatusRequest()
    }

    fun showUnReadStatusData (data: TalentUnReadStatusReq) {
        var employmentListIsRead = data.data?.employmentListIsRead ?: false
        var disputeListIsRead = data.data?.disputeListIsRead ?: false
        if (employmentListIsRead) {
            mViewEmployingTip.visibility = View.GONE
        } else {
            mViewEmployingTip.visibility = View.VISIBLE
        }
        if (disputeListIsRead) {
            mViewComplaintsTip.visibility = View.GONE
        } else {
            mViewComplaintsTip.visibility = View.VISIBLE
        }

        var waitEmploymentListCount = data.data?.waitEmploymentListCount ?: 0
        if (waitEmploymentListCount > 0) {
            if (waitEmploymentListCount > 99) {
                mTvPendingAdmission.text = "待雇用(99+)"
            } else {
                mTvPendingAdmission.text = "待雇用(${waitEmploymentListCount})"
            }
        } else {
            mTvPendingAdmission.text = "待雇用"
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
            R.id.mTvTalent -> {
                TalentReleaseActivity.intentStart(activity as AppCompatActivity,0)
            }
            R.id.mTvReceivedInvitation -> {
                ReceivedInvitationActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvMyFocus -> {
                MyFollowActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessTalentEvent.talent_view_follow)
            }
            R.id.mTvMyResume -> {
                MyResumeActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvMyCredit -> {
                CreditScoreActivity.intentStart(activity as AppCompatActivity,false)
                UMengEventModule.report(activity, BussinessTalentEvent.talent_view_credit)
            }
            R.id.mTvMyIncome -> {
                MyIncomeActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessTalentEvent.talent_view_income)
            }
            R.id.mTvPendingAdmission -> {
                MyWorkActivity.intentStart(activity as AppCompatActivity,0)
            }
            R.id.mTvToBeStarted -> {
                MyWorkActivity.intentStart(activity as AppCompatActivity,1)
            }
            R.id.mRvWaitComment -> {
                MyWorkActivity.intentStart(activity as AppCompatActivity,2)
            }
            R.id.mTvFinish -> {
                MyWorkActivity.intentStart(activity as AppCompatActivity,3)
            }
            R.id.mTvEvaluationCenter -> {
                TalentEvaluationCenterActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessTalentEvent.talent_view_evaluation_center)
            }
            R.id.mTvComplaints -> {
                TalentDisputeHandlingActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, BussinessTalentEvent.talent_view_complaint_dispute)
            }
        }
    }
}