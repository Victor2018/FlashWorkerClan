package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.MineEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.GuildDetailData
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.UpdateGuildRegulationParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_regulation_manage.*

class RegulationManageActivity : BaseActivity(),View.OnClickListener, TextWatcher {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: GuildDetailData?) {
            var intent = Intent(activity, RegulationManageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildDetailData: GuildDetailData? = null

    override fun getLayoutResource() = R.layout.activity_regulation_manage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)

        mEtRegulation.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        mGuildDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as GuildDetailData?
        mEtRegulation.setText(mGuildDetailData?.guildRules)
    }

    fun subscribeUi() {
        guildVM.updateGuildRegulationData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("公会制度修改成功")
                    UMengEventModule.report(this, MineEvent.edit_guild_rules)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendUpdateGuildRegulationRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var guildRules = mEtRegulation.text.toString()
        if (TextUtils.isEmpty(guildRules)) {
            ToastUtils.show("请输入公会制度")
            return
        }

        mLoadingDialog?.show()

        val body = UpdateGuildRegulationParm()
        body.guildId = mGuildDetailData?.guildId
        body.guildRules = guildRules

        guildVM.updateGuildRegulation(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvConfirm -> {
                sendUpdateGuildRegulationRequest()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvRegulationCount.text = "${s?.length}/500"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}