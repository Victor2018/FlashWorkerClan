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
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.adapter.anim.SlideInBottomAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.GuildNewsParm
import com.flash.worker.lib.coremodel.data.req.GuildNewsReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildNewsViewAdapter
import kotlinx.android.synthetic.main.activity_guild_news_view.*


class GuildNewsViewActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?) {
            var intent = Intent(activity, GuildNewsViewActivity::class.java)
            intent.putExtra(Constant.GUILD_ID_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildNewsViewAdapter: GuildNewsViewAdapter? = null

    var guildId: String? = null
    var currentPage: Int = 1

    override fun getLayoutResource() = R.layout.activity_guild_news_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mGuildNewsViewAdapter = GuildNewsViewAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mGuildNewsViewAdapter,mRvNews)

        mRvNews.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

        mRvNews.setLoadMoreListener(this)
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.GUILD_ID_KEY)
        sendGuildNewsRequest()
    }

    fun subscribeUi () {
        guildVM.guildNewsData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildNewsData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendGuildNewsRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = GuildNewsParm()
        body.guildId = guildId
        body.pageNum = currentPage


        guildVM.fetchGuildNews(token,body)
    }
    fun showGuildNewsData (datas: GuildNewsReq) {
        mGuildNewsViewAdapter?.showData(datas.data,mTvNoData,mRvNews,currentPage)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onRefresh() {
        currentPage = 1
        mGuildNewsViewAdapter?.clear()
        mGuildNewsViewAdapter?.setFooterVisible(false)
        mGuildNewsViewAdapter?.notifyDataSetChanged()

        mRvNews.setHasMore(false)

        sendGuildNewsRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendGuildNewsRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}