package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.etfilter.MoneyInputFilter
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.EmployerSettlementOrderInfo
import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import com.flash.worker.lib.coremodel.data.parm.RewardParm
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.interfaces.OnRewardLabelSelectListener
import com.flash.worker.module.business.view.adapter.RewardLabelSelectAdapter
import kotlinx.android.synthetic.main.activity_reward.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: RewardActivity
 * Author: Victor
 * Date: 2020/12/17 20:41
 * Description: 
 * -----------------------------------------------------------------
 */
class RewardActivity: BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
        OnRewardLabelSelectListener {

    var mLoadingDialog: LoadingDialog? = null
    var passwordErrorCount: Int = 0//密码输入错误次数

    var mRewardLabelSelectAdapter: RewardLabelSelectAdapter? = null

    var mEmployerSettlementOrderInfo: EmployerSettlementOrderInfo? = null

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: EmployerSettlementOrderInfo?) {
            var intent = Intent(activity, RewardActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutResource() = R.layout.activity_reward

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        var filter = MoneyInputFilter()
        filter.setMaxValue(1000000.0)
        mEtRewardAmount.filters = arrayOf(filter)

        mRewardLabelSelectAdapter = RewardLabelSelectAdapter(this,this)
        mRvRewardReason.adapter = mRewardLabelSelectAdapter

        mIvBack.setOnClickListener(this)
        mTvRewardReason.setOnClickListener(this)
        mTvGiveUp.setOnClickListener(this)
        mTvReward.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mEmployerSettlementOrderInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as EmployerSettlementOrderInfo?

        tv_confirm_tip.text = "感谢您对 ${mEmployerSettlementOrderInfo?.username} 的认可！"
    }

    fun subscribeEvent() {
        LiveDataBus.with(BusinessActions.ADD_REWARD_REASON)
            .observe(this, Observer {
                var matters = it as ArrayList<RewardLabelInfo>

                mRewardLabelSelectAdapter?.clear()
                mRewardLabelSelectAdapter?.add(matters)
                mRewardLabelSelectAdapter?.notifyDataSetChanged()
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvRewardReason -> {
                var datas = mRewardLabelSelectAdapter?.getDatas()
                        as ArrayList<RewardLabelInfo>
                RewardReasonActivity.intentStart(this,datas)
            }
            R.id.mTvGiveUp -> {
                onBackPressed()
            }
            R.id.mTvReward -> {
                var rewardAmount = mEtRewardAmount.text.toString()
                if (TextUtils.isEmpty(rewardAmount)) {
                    ToastUtils.show("请输入奖励金额")
                    return
                }
                if (rewardAmount.toInt() < 1) {
                    ToastUtils.show("奖励金额至少1元")
                    return
                }

                if (mRewardLabelSelectAdapter?.getDatas()?.size == 0) {
                    ToastUtils.show("请选择奖励理由")
                    return
                }
                var body = RewardParm()
                body.rewardAmount = rewardAmount.toInt()
                body.settlementOrderId = mEmployerSettlementOrderInfo?.settlementOrderId
                body.rewardLabel = getRewardLabel()
                EncourageActivity.intentStart(this,body)

                onBackPressed()
            }
        }
    }

    override fun OnRewardLabelSelect(data: RewardLabelInfo?) {
        if (!hasAddRewardLabel(data)) {
            mRewardLabelSelectAdapter?.add(data)
            mRewardLabelSelectAdapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mIvRemoveReward -> {
                mRewardLabelSelectAdapter?.removeItem(position)
                mRewardLabelSelectAdapter?.notifyItemRemoved(position)
            }
            else -> {

            }
        }
    }

    fun hasAddRewardLabel (data: RewardLabelInfo?): Boolean {
        mRewardLabelSelectAdapter?.getDatas()?.forEach {
            if (TextUtils.equals(it.id, data?.id)) {
                return true
            }
        }
        return false
    }

    fun getRewardLabel () : List<String> {
        var rewardLableIds = ArrayList<String>()
        mRewardLabelSelectAdapter?.getDatas()?.forEach {
            it.name?.let { it1 -> rewardLableIds.add(it1) }
        }
        return rewardLableIds
    }

}