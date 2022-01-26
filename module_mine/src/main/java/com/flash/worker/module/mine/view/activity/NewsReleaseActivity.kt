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
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.ReleaseGuildNewsParm
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import kotlinx.android.synthetic.main.activity_news_release.*

class NewsReleaseActivity : BaseActivity(),View.OnClickListener, TextWatcher {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?) {
            var intent = Intent(activity, NewsReleaseActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var guildId: String? = null

    override fun getLayoutResource() = R.layout.activity_news_release

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mIvBack.setOnClickListener(this)
        mTvRelease.setOnClickListener(this)
        mEtNews.addTextChangedListener(this)
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
    }

    fun subscribeUi() {
        guildVM.releaseNewsData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("发布成功")
                    UMengEventModule.report(this, MineEvent.new_guild_news)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendReleaseNewsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        var content = mEtNews.text.toString()
        if (TextUtils.isEmpty(content)) {
            ToastUtils.show("请输入资讯内容")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        val body = ReleaseGuildNewsParm()
        body.guildId = guildId
        body.content = content

        guildVM.releaseNews(token,body)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvRelease -> {
                sendReleaseNewsRequest()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        mTvNewsCount.text = "${s?.length}/200"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}