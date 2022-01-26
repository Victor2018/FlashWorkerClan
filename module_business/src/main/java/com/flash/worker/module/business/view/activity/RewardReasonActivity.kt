package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.RewardLabelInfo
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.business.R
import com.flash.worker.module.business.view.adapter.RewardLabelAdapter
import kotlinx.android.synthetic.main.activity_reward_reason.*

class RewardReasonActivity : BaseActivity(),View.OnClickListener, TextWatcher,
        CompoundButton.OnCheckedChangeListener,AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: ArrayList<RewardLabelInfo>?) {
            var intent = Intent(activity, RewardReasonActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonViewModelFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mRewardLabelAdapter: RewardLabelAdapter? = null
    var rewardLabelList = ArrayList<RewardLabelInfo>()

    override fun getLayoutResource() = R.layout.activity_reward_reason

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mRewardLabelAdapter = RewardLabelAdapter(this,this)
        mRvRewardReason.adapter = mRewardLabelAdapter

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mChkOther.setOnCheckedChangeListener(this)
        mEtReportContent.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        rewardLabelList = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
                as ArrayList<RewardLabelInfo>

        mRewardLabelAdapter?.checkMap?.clear()
        rewardLabelList.forEach {
            if (TextUtils.equals("-1",it.id)) {//其他奖励理由
                mEtReportContent.setText(it.name)
                mChkOther.isChecked = true
            } else {
                mRewardLabelAdapter?.checkMap?.put(it.id ?: "",it)
                mChkOther.isChecked = false
            }
        }

        sendRewardLabelRequest()
    }

    fun subscribeUi() {
        commonVM.rewardLabelData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mRewardLabelAdapter?.clear()
                    mRewardLabelAdapter?.add(it.value.data)
                    mRewardLabelAdapter?.notifyDataSetChanged()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

    }

    fun sendRewardLabelRequest () {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        commonVM.fetchRewardLabel(token)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                var other = mEtReportContent.text.toString()
                if (mChkOther.isChecked) {
                    if (TextUtils.isEmpty(other)) {
                        ToastUtils.show("请输入其他奖励理由~")
                        return
                    }
                }

                LiveDataBus.send(BusinessActions.ADD_REWARD_REASON,getRewardLabels())
                onBackPressed()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvReportContentCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkOther -> {
                if (isChecked) {
                    mEtReportContent.visibility = View.VISIBLE
                    mTvReportContentCount.visibility = View.VISIBLE
                } else {
                    mEtReportContent.visibility = View.GONE
                    mTvReportContentCount.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var data = mRewardLabelAdapter?.getItem(position)
        when (view?.id) {
            R.id.mChkRewardLabel -> {
                var isChecked = mRewardLabelAdapter?.isItemChecked(data) ?: false
                if (isChecked) {
                    mRewardLabelAdapter?.checkMap?.remove(data?.id)
                } else {
                    mRewardLabelAdapter?.checkMap?.put(data?.id!!,
                        mRewardLabelAdapter?.getItem(position)!!)
                }
                mRewardLabelAdapter?.notifyDataSetChanged()
            }
            else -> {

            }
        }
    }

    fun getRewardLabels(): List<RewardLabelInfo> {
        var rewardReasonList = ArrayList<RewardLabelInfo>()
        mRewardLabelAdapter?.checkMap?.forEach {
            rewardReasonList.add(it.value)
        }
        if (mChkOther.isChecked) {
            var other = RewardLabelInfo()
            other.id = "-1"
            other.name = "其他:" + mEtReportContent.text.toString()
            rewardReasonList.add(other)
        }
        return rewardReasonList
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}