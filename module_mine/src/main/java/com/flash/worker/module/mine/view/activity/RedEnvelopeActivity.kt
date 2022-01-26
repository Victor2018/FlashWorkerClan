package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.AcivityInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReceiveTalentRedEnvelopeParm
import com.flash.worker.lib.coremodel.data.req.ActivityInfoReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.TalentRedEnvelopeVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.RedEnvelopeAdapter
import com.flash.worker.module.mine.view.dialog.RedEnvelopeReceiveDialog
import com.flash.worker.module.mine.view.interfaces.OnRedEnvelopeReceiveListener
import kotlinx.android.synthetic.main.activity_red_envelope.*

class RedEnvelopeActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemClickListener,
        OnRedEnvelopeReceiveListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, RedEnvelopeActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val talentRedEnvelopeVM: TalentRedEnvelopeVM by viewModels {
        InjectorUtils.provideTalentRedEnvelopeVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mRedEnvelopeAdapter: RedEnvelopeAdapter? = null
    var mActivityInfoReq: ActivityInfoReq? = null

    override fun getLayoutResource() = R.layout.activity_red_envelope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)
        mRedEnvelopeAdapter = RedEnvelopeAdapter(this,this)
        mRvRedEnvelope.adapter = mRedEnvelopeAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)
        
        mIvBack.setOnClickListener(this)
        mTvReceivingInstructions.setOnClickListener(this)
    }

    fun initData () {
        sendActivityInfoRequest()
    }

    fun subscribeUi () {
        talentRedEnvelopeVM.activitytInfoData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showActivityInfoData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        talentRedEnvelopeVM.receiveTalentRedEnvelopeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("领取成功")
                    LiveDataBus.send(MineActions.REFRESH_RED_ENVELOPE_TIP)
                    mRedEnvelopeAdapter?.clear()
                    mRedEnvelopeAdapter?.setFooterVisible(false)
                    mRedEnvelopeAdapter?.notifyDataSetChanged()

                    mRvRedEnvelope.setHasMore(false)

                    sendActivityInfoRequest()

                    UMengEventModule.report(this, MineEvent.receive_red_envelope)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendActivityInfoRequest () {
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        talentRedEnvelopeVM.fetchActivityInfo(token)
    }

    fun sendReceiveRedEnvelopeRequest (configId: String?) {
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = ReceiveTalentRedEnvelopeParm()
        body.configId = configId

        talentRedEnvelopeVM.receiveTalentRedEnvelope(token,body)
    }

    fun showActivityInfoData (data: ActivityInfoReq) {
        mActivityInfoReq = data
        mTvTitle.text = data.data?.activityTitle

        mTvTotalReceiveCount.text = "已领取${data.data?.totalReceiveCount}个"
        mTvTotalReceiveAmount.text = "合计${data.data?.totalReceiveAmount}元"

        mTvValidDate.text = "活动有效期：${data.data?.activityStartTime}至${data.data?.activityEndTime}"

        if (data == null) {
            mTvNoData.visibility = View.VISIBLE
            mClContent.visibility = View.GONE
            mTvReceivingInstructions.visibility = View.GONE
            mRedEnvelopeAdapter?.setFooterVisible(false)
            mRedEnvelopeAdapter?.clear()
            mRedEnvelopeAdapter?.notifyDataSetChanged()
            mRvRedEnvelope.setHasMore(false)
            return
        }
        if (data.data == null) {
            mTvNoData.visibility = View.VISIBLE
            mClContent.visibility = View.GONE
            mTvReceivingInstructions.visibility = View.GONE
            mRedEnvelopeAdapter?.setFooterVisible(false)
            mRedEnvelopeAdapter?.clear()
            mRedEnvelopeAdapter?.notifyDataSetChanged()
            mRvRedEnvelope.setHasMore(false)
            return
        }
        if (data.data?.receiveInfos == null) {
            mTvNoData.visibility = View.VISIBLE
            mClContent.visibility = View.GONE
            mTvReceivingInstructions.visibility = View.GONE
            mRedEnvelopeAdapter?.setFooterVisible(false)
            mRedEnvelopeAdapter?.clear()
            mRedEnvelopeAdapter?.notifyDataSetChanged()
            mRvRedEnvelope.setHasMore(false)
            return
        }
        if (data.data?.receiveInfos?.size == 0) {
            mTvNoData.visibility = View.VISIBLE
            mClContent.visibility = View.GONE
            mTvReceivingInstructions.visibility = View.GONE
            mRedEnvelopeAdapter?.setFooterVisible(false)
            mRedEnvelopeAdapter?.clear()
            mRedEnvelopeAdapter?.notifyDataSetChanged()
            mRvRedEnvelope.setHasMore(false)
            return
        }
        mTvNoData.visibility = View.GONE
        mClContent.visibility = View.VISIBLE
        mTvReceivingInstructions.visibility = View.VISIBLE
        mRedEnvelopeAdapter?.clear()
        mRedEnvelopeAdapter?.add(data?.data?.receiveInfos)
        mRedEnvelopeAdapter?.notifyDataSetChanged()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack ->{
                onBackPressed()
            }
            R.id.mTvReceivingInstructions -> {
                WebActivity.intentStart(this,"领取说明",mActivityInfoReq?.data?.activityRulesUrl)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var receiveCount = mRedEnvelopeAdapter?.getItem(position)?.receiveCount
        var redPacketQty = mRedEnvelopeAdapter?.getItem(position)?.redPacketQty
        //红包不可领取
        if (receiveCount == redPacketQty) return

        var remainCount = mRedEnvelopeAdapter?.getItem(position)?.remainCount
        if (remainCount == 0) {
            showNoRedEnvelopeTipDlg()
            return
        }

        showReceiveRedEnvelopeDlg(mRedEnvelopeAdapter?.getItem(position))
    }

    fun showNoRedEnvelopeTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "抱歉，您暂未达到领取该红包的条件！请继续加油吧！"
        commonTipDialog.mOkText = "返回"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.show()
    }

    fun showReceiveRedEnvelopeDlg (data: AcivityInfo?) {
        var redEnvelopeReceiveDialog = RedEnvelopeReceiveDialog(this)
        redEnvelopeReceiveDialog.mAcivityInfo = data
        redEnvelopeReceiveDialog.mOnRedEnvelopeReceiveListener = this
        redEnvelopeReceiveDialog.show()
    }

    override fun onRefresh() {
        mRedEnvelopeAdapter?.clear()
        mRedEnvelopeAdapter?.setFooterVisible(false)
        mRedEnvelopeAdapter?.notifyDataSetChanged()

        mRvRedEnvelope.setHasMore(false)

        sendActivityInfoRequest()
    }


    override fun OnRedEnvelopeReceive(data: AcivityInfo?) {
        sendReceiveRedEnvelopeRequest(data?.configId)
    }
}