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
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.GuildApplyRecordParm
import com.flash.worker.lib.coremodel.data.req.GuildApplyRecordReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildApplyRecordAdapter
import kotlinx.android.synthetic.main.activity_apply_record.*


class ApplyRecordActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, ApplyRecordActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildApplyRecordAdapter: GuildApplyRecordAdapter? = null

    var currentPage: Int = 1

    override fun getLayoutResource() = R.layout.activity_apply_record

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mGuildApplyRecordAdapter = GuildApplyRecordAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mGuildApplyRecordAdapter,mRvApplyRecord)

        mRvApplyRecord.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)

        mRvApplyRecord.setLoadMoreListener(this)
    }

    fun initData () {
        sendGuildApplyRecordRequest()
    }

    fun subscribeUi () {
        guildVM.applyRecordData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildApplyRecordData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendGuildApplyRecordRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = GuildApplyRecordParm()
        body.pageNum = currentPage

        guildVM.fetchApplyRecord(token,body)
    }

    fun showGuildApplyRecordData (data: GuildApplyRecordReq?) {
        mGuildApplyRecordAdapter?.showData(data?.data,mTvNoData,mRvApplyRecord,currentPage)
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
        mGuildApplyRecordAdapter?.clear()
        mGuildApplyRecordAdapter?.setFooterVisible(false)
        mGuildApplyRecordAdapter?.notifyDataSetChanged()

        mRvApplyRecord.setHasMore(false)

        sendGuildApplyRecordRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendGuildApplyRecordRequest()
    }


}