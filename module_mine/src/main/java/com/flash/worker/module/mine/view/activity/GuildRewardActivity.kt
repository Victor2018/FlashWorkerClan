package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.interfaces.OnAmySportsVoucherReceiveListener
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.NavigationUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.AmySportsVoucherReceiveDialog
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.GuildRedEnvelopeInfoData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReceiveCouponParm
import com.flash.worker.lib.coremodel.data.req.GuildCouponReq
import com.flash.worker.lib.coremodel.data.req.GuildRedEnvelopeInfoReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildRedEnvelopeVM
import com.flash.worker.lib.coremodel.viewmodel.UserCouponVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildCouponAdapter
import com.flash.worker.module.mine.view.dialog.RewardReceiveDialog
import com.flash.worker.module.mine.view.interfaces.OnRewardReceiveListener
import kotlinx.android.synthetic.main.activity_guild_reward.*

class GuildRewardActivity : BaseActivity(),View.OnClickListener, OnRewardReceiveListener,
        AdapterView.OnItemClickListener, OnAmySportsVoucherReceiveListener,
        SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, GuildRewardActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val guildRedEnvelopeVM: GuildRedEnvelopeVM by viewModels {
        InjectorUtils.provideGuildRedEnvelopeVMFactory(this)
    }

    private val userCouponVM: UserCouponVM by viewModels {
        InjectorUtils.provideUserCouponVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildRedEnvelopeInfoReq: GuildRedEnvelopeInfoReq? = null
    var mGuildCouponAdapter: GuildCouponAdapter? = null
    var currentPosition = 0

    override fun getLayoutResource() = R.layout.activity_guild_reward

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(this)

        subscribeUi()

        mGuildCouponAdapter = GuildCouponAdapter(this,this)
        mRvGuildCoupon.adapter = mGuildCouponAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mIvGuildRedEnvelope.setOnClickListener(this)
        mTvReceiveDescription.setOnClickListener(this)
    }

    fun initData () {
        sendRedEnvelopeInfoRequest()
        sendGuildCouponRequest()
    }

    fun subscribeUi() {
        guildRedEnvelopeVM.redEnvelopeInfoData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showRedEnvelopeInfoData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildRedEnvelopeVM.receiveRedEnvelopeData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("领取成功")
                    sendRedEnvelopeInfoRequest()
                    UMengEventModule.report(this, MineEvent.receive_guild_red_envelope)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })

        userCouponVM.guildCouponData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildCouponData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userCouponVM.receiveCouponData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    mGuildCouponAdapter?.removeItem(currentPosition)
                    mGuildCouponAdapter?.notifyItemRemoved(currentPosition)
                    showReceiveAmySportVoucherSuccessDlg()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("155305",it.code)) {
                        showReceiveAmySportVoucherFailDlg()
                        return@Observer
                    }
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendRedEnvelopeInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mSrlRefresh.isRefreshing = true
        val token = App.get().getLoginReq()?.data?.token

        guildRedEnvelopeVM.fetchRedEnvelopeInfo(token)
    }

    fun sendReceiveRedEnvelopeInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        guildRedEnvelopeVM.receiveRedEnvelope(token)
    }

    fun sendGuildCouponRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mSrlRefresh.isRefreshing = true

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userCouponVM.fetchGuildCoupon(token)
    }

    fun sendReceiveCouponRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        var body = ReceiveCouponParm()
        body.type = 20

        userCouponVM.receiveCoupon(token,body)
    }

    fun showRedEnvelopeInfoData (data: GuildRedEnvelopeInfoReq) {
        mGuildRedEnvelopeInfoReq = data
        mTvRemainQty.text = "剩余：${data.data?.remainQty}份"
        when (data.data?.status) {
            1 -> {//待领取
                mIvGuildRedEnvelope.visibility = View.VISIBLE
                mTvRemainQty.visibility = View.VISIBLE
                mTvReceiveDescription.visibility = View.VISIBLE
                mIvGuildRedEnvelope.setImageResource(R.mipmap.img_guild_red_envelope)
            }
            2 -> {//已领取
                mIvGuildRedEnvelope.visibility = View.VISIBLE
                mTvRemainQty.visibility = View.VISIBLE
                mTvReceiveDescription.visibility = View.VISIBLE
                mIvGuildRedEnvelope.setImageResource(R.mipmap.img_guild_red_envelope_received)
            }
            3 -> {//已结束（已领完）
                mIvGuildRedEnvelope.visibility = View.GONE
                mTvRemainQty.visibility = View.GONE
                mTvReceiveDescription.visibility = View.GONE
            }
            else -> {
                mIvGuildRedEnvelope.visibility = View.GONE
                mTvRemainQty.visibility = View.GONE
                mTvReceiveDescription.visibility = View.GONE
            }
        }
    }

    fun showGuildCouponData (data: GuildCouponReq?) {
        var remainQty = data?.data?.remainQty ?: 0
        mGuildCouponAdapter?.clear()
        for (i in 1 .. remainQty) {
            mGuildCouponAdapter?.add(i.toString())
        }
        mGuildCouponAdapter?.notifyDataSetChanged()
    }

    fun showRewardReceiveDlg (data: GuildRedEnvelopeInfoData?) {
        var mRewardReceiveDialog = RewardReceiveDialog(this)
        mRewardReceiveDialog.mOnRewardReceiveListener = this
        mRewardReceiveDialog.mGuildRedEnvelopeInfoData = data

        mRewardReceiveDialog.show()
    }

    fun showAmySportVoucherReceiveDlg () {
        var mAmySportsVoucherReceiveDialog = AmySportsVoucherReceiveDialog(this)
        mAmySportsVoucherReceiveDialog?.mOnAmySportsVoucherReceiveListener = this
        mAmySportsVoucherReceiveDialog?.show()
    }

    fun showReceiveAmySportVoucherSuccessDlg () {
        val mCommonTipDialog = CommonTipDialog(this)
        mCommonTipDialog?.mTitle = "温馨提示"
        mCommonTipDialog?.mContent = "领取成功，请前往卡券包使用"
        mCommonTipDialog?.mCancelText = "取消"
        mCommonTipDialog?.mOkText = "前往使用"
        mCommonTipDialog?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goCouponActivity(this@GuildRewardActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mCommonTipDialog?.show()
    }

    fun showReceiveAmySportVoucherFailDlg () {
        val mCommonTipDialog = CommonTipDialog(this)
        mCommonTipDialog?.mTitle = "温馨提示"
        mCommonTipDialog?.mContent = "已领取，请前往卡券包使用"
        mCommonTipDialog?.mCancelText = "取消"
        mCommonTipDialog?.mOkText = "前往使用"
        mCommonTipDialog?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goCouponActivity(this@GuildRewardActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        mCommonTipDialog?.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvGuildRedEnvelope -> {
                if (mGuildRedEnvelopeInfoReq?.data?.status == 1) {
                    showRewardReceiveDlg(mGuildRedEnvelopeInfoReq?.data)
                }
            }
            R.id.mTvReceiveDescription -> {
                RewardReceiveDescriptionActivity.intentStart(this)
            }
        }
    }

    override fun OnRewardReceive() {
        sendReceiveRedEnvelopeInfoRequest()
    }

    override fun OnAmySportsVoucherReceive() {
        sendReceiveCouponRequest()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        showAmySportVoucherReceiveDlg()
    }

    override fun onRefresh() {
        initData()
    }


}