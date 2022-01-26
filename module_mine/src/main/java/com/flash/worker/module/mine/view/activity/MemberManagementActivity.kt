package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.util.ClipboardUtil
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.KeyBoardUtil
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.parm.GuildMemberParm
import com.flash.worker.lib.coremodel.data.parm.RemoveMemberParm
import com.flash.worker.lib.coremodel.data.req.GuildMemberReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildMemberAdapter
import com.flash.worker.module.mine.view.dialog.ExpelledMemberDialog
import com.flash.worker.module.mine.view.holder.GuildMemberContentHolder
import com.flash.worker.module.mine.view.interfaces.OnMemberExpelledListener
import kotlinx.android.synthetic.main.activity_guild_hall.*
import kotlinx.android.synthetic.main.activity_member_management.*
import kotlinx.android.synthetic.main.activity_member_management.mEtSearch
import kotlinx.android.synthetic.main.activity_member_management.mIvBack
import kotlinx.android.synthetic.main.activity_member_management.mIvClear
import kotlinx.android.synthetic.main.activity_member_management.mSrlRefresh
import kotlinx.android.synthetic.main.activity_member_management.mTvNoData

class MemberManagementActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener, OnMemberExpelledListener,
    TextView.OnEditorActionListener, TextWatcher {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?) {
            var intent = Intent(activity, MemberManagementActivity::class.java)
            intent.putExtra(Constant.GUILD_ID_KEY,guildId)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mGuildMemberAdapter: GuildMemberAdapter? = null

    var guildId: String? = null
    var currentPage: Int = 1
    var currentPosition: Int = -1
    var keywords: String? = null

    var mExpelledMemberDialog: ExpelledMemberDialog? = null

    override fun getLayoutResource() = R.layout.activity_member_management

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        mLoadingDialog = LoadingDialog(this)

        mGuildMemberAdapter = GuildMemberAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mGuildMemberAdapter,mRvMember)

        mRvMember.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mIvClear.setOnClickListener(this)

        mRvMember.setLoadMoreListener(this)

        mEtSearch.addTextChangedListener(this)
        mEtSearch.setOnEditorActionListener(this)
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.GUILD_ID_KEY)
        sendGuildMemberRequest()
    }

    fun subscribeUi () {
        guildVM.guildMemberData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildMemberData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildVM.removeMemberData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("已开除成员")
                    mGuildMemberAdapter?.removeItem(currentPosition)
                    mGuildMemberAdapter?.notifyItemRemoved(currentPosition)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendGuildMemberRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }

        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = GuildMemberParm()
        body.guildId = guildId
        body.pageNum = currentPage
        body.keywords = keywords

        guildVM.fetchGuildMember(token,body)
    }

    fun sendRemoveMemberRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        val body = RemoveMemberParm()
        body.guildId = guildId
        body.userId = mGuildMemberAdapter?.getItem(currentPosition)?.userId

        guildVM.removeMember(token,body)
    }

    fun showGuildMemberData (datas: GuildMemberReq) {
        KeyBoardUtil.hideKeyBoard(this,mEtSearch)
        mGuildMemberAdapter?.showData(datas.data,mTvNoData,mRvMember,currentPage)
    }

    fun showExpelledMemberDlg () {
        if (mExpelledMemberDialog == null) {
            mExpelledMemberDialog = ExpelledMemberDialog(this)
            mExpelledMemberDialog?.mOnMemberExpelledListener = this
        }

        mExpelledMemberDialog?.show()
    }

    fun showExpelledMemberTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "确定开除该成员吗?"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确定"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendRemoveMemberRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvClear -> {
                keywords = null
                mEtSearch.setText("")
                sendGuildMemberRequest()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (id) {
            GuildMemberContentHolder.ONITEM_COPY_CLICK -> {
                val userId = mGuildMemberAdapter?.getItem(position)?.userId
                ClipboardUtil.copy(this,Constant.SGZ_USER_ID,userId)
                ToastUtils.show("已复制到剪贴板")
            }
            GuildMemberContentHolder.ONITEM_CLICK -> {
                MemberDetailActivity.intentStart(this, mGuildMemberAdapter?.getItem(position))
            }
            GuildMemberContentHolder.ONITEM_LONG_CLICK -> {
                showExpelledMemberDlg()
            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        mGuildMemberAdapter?.clear()
        mGuildMemberAdapter?.setFooterVisible(false)
        mGuildMemberAdapter?.notifyDataSetChanged()

        mRvMember.setHasMore(false)

        sendGuildMemberRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendGuildMemberRequest()
    }

    override fun OnMemberExpelled() {
        showExpelledMemberTipDlg()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            currentPage = 1
            keywords = mEtSearch.text.toString()
            sendGuildMemberRequest()
            return true
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        var text = mEtSearch.text.toString()
        if (TextUtils.isEmpty(text)) {
            mIvClear.visibility = View.GONE
        } else {
            mIvClear.visibility = View.VISIBLE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}