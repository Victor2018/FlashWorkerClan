package com.flash.worker.module.message.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.event.MessageEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.AbsPopWindow
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.holder.ContentViewHolder
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.SystemNoticeUnreadReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.SystemNoticeVM
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.coremodel.viewmodel.factory.UserVMFactory
import com.flash.worker.lib.im.NimMessageManager
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.flash.worker.module.message.interfaces.OnMessageMoreListener
import com.flash.worker.module.message.interfaces.OnRecentContactMoreListener
import com.flash.worker.module.message.view.activity.ChatActivity
import com.flash.worker.module.message.view.activity.SystemNoticeActivity
import com.flash.worker.module.message.view.adapter.RecentContactAdapter
import com.flash.worker.module.message.view.dialog.RecentContactMoreDialog
import com.flash.worker.module.message.view.dialog.MessageMorePopWindow
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import kotlinx.android.synthetic.main.fragment_message.*
import java.util.*
import kotlin.collections.ArrayList


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageFragment
 * Author: Victor
 * Date: 2020/11/27 16:14
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.MessageFgt)
class MessageFragment: BaseFragment(),  SwipeRefreshLayout.OnRefreshListener,
    AdapterView.OnItemClickListener,View.OnClickListener, OnMessageMoreListener,
        TextView.OnEditorActionListener,TextWatcher, OnRecentContactMoreListener {

    companion object {

        fun newInstance(): MessageFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): MessageFragment {
            val fragment = MessageFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var systemNoticeVM: SystemNoticeVM
    private lateinit var userVM: UserVM

    var mLoadingDialog: LoadingDialog? = null
    var mRecentContactAdapter: RecentContactAdapter? = null
    var mRecentContactRunnable: RecentContactRunnable? = null

    var currentPosition: Int = 0
    var systemUnreadNum: Int = 0

    override fun getLayoutResource(): Int {
        return R.layout.fragment_message
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        getRecentContactData()
    }

    override fun onResume() {
        super.onResume()
        sendUnreadNumRequest()
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(activity!!)

        systemNoticeVM = ViewModelProvider(this, InjectorUtils.provideSystemNoticeVMFactory(this))
            .get(SystemNoticeVM::class.java)

        userVM = ViewModelProvider(this, UserVMFactory(this))
            .get(UserVM::class.java)

        subscribeUi()
        subscribeEvent()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mRecentContactAdapter = RecentContactAdapter(activity!!,this)
        
        val animatorAdapter = AlphaAnimatorAdapter(mRecentContactAdapter!!,mRvRecentContact)
        mRvRecentContact.adapter = animatorAdapter

        mRecentContactRunnable = RecentContactRunnable()

        mIvClear.setOnClickListener(this)
        mIvMore.setOnClickListener(this)
        mClSysMessage.setOnClickListener(this)

        mEtSearch.setOnEditorActionListener(this)
        mEtSearch.addTextChangedListener(this)

    }

    fun subscribeUi() {
        systemNoticeVM.unReadNumData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showUnreadNumData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        systemNoticeVM.markReadData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    UMengEventModule.report(activity, MessageEvent.mark_all_read)
                    clearUnreadNumData()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        userVM.imLoginInfoData.observeForever {
            when(it) {
                is HttpResult.Success -> {
                    var loginInfo = LoginInfo(it.value.data?.imAccid,it.value.data?.imToken)
                    NimMessageManager.instance.login(loginInfo)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        }
    }

    fun subscribeEvent() {

        LiveDataBus.with(IMActions.NEW_MESSAGE)
                .observe(this, Observer {
                    LiveDataBus.send(IMActions.REFRESH_IM_UNREAD_COUNT)
                })

        LiveDataBus.with(IMActions.MESSAGE_STATUS)
                .observe(this, Observer {
                    LiveDataBus.send(IMActions.REFRESH_IM_UNREAD_COUNT)
                    var message = it as IMMessage
                    val index: Int = getMessageIndex(message.uuid)
                    var count = mRecentContactAdapter?.getContentItemCount() ?: 0
                    if (index >= 0 && index < count) {
                        val item = mRecentContactAdapter?.getItem(index)
                        item?.msgStatus = message.status
                        mRecentContactAdapter?.notifyItemChanged(index)
                    }
                })

        LiveDataBus.with(IMActions.NEW_RECENT_CONTACT)
                .observeForever(this, Observer {
                    LiveDataBus.send(IMActions.REFRESH_IM_UNREAD_COUNT)
                    var recentContact = it as RecentContact
                    var recentContacts = ArrayList<RecentContact>()
                    recentContacts.add(recentContact)
                    refreshRecentContacts(recentContacts)
                })

        LiveDataBus.with(IMActions.NIM_LOGIN_SUCCESS)
                .observeForever(this, Observer {
                    getRecentContactData()
                })

        LiveDataBus.with(IMActions.CLEAR_RECENT_CONTACT)
                .observe(this, Observer {
                    mRecentContactAdapter?.getDatas()?.forEach {
                        NimMessageManager.instance.deleteRecentContact(it)
                    }
                    MainHandler.get().postDelayed({
                        mRecentContactAdapter?.clear()
                        mRecentContactAdapter?.notifyDataSetChanged()
                    }, 200)
                })

    }

    fun sendUnreadNumRequest () {
        if (!App.get().hasLogin()) return
        clearUnreadNumData()
        mSrlRefresh.isRefreshing = true
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        systemNoticeVM.fetchUnreadNum(token)
    }

    fun sendMarkReadRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            mSrlRefresh.isRefreshing = false
            return
        }
        Loger.e(TAG,"sendMarkReadRequest()-systemUnreadNum = $systemUnreadNum")
        if (systemUnreadNum <= 0) return

        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        systemNoticeVM.markRead(token)
    }

    fun showUnreadNumData (data: SystemNoticeUnreadReq) {
        systemUnreadNum = data.data?.unreadNum ?: 0
        LiveDataBus.send(IMActions.SYSTEM_NOTICE_UNREAD_COUNT,systemUnreadNum)

        if (systemUnreadNum > 0) {
            mTvUnReadCount.visibility = View.VISIBLE
            mTvUnReadCount.text = data.data?.unreadNum?.toString()
            mTvDate.text = data.data?.latestTime
            mTvContent.text = "[${data.data?.latestSummary}]"
        } else {
            mTvUnReadCount.visibility = View.GONE
            mTvDate.text = ""
            mTvContent.text = "[暂无新通知]"
        }
    }

    fun clearUnreadNumData () {
        mTvDate.text = ""
        mTvContent.text = "[暂无新通知]"
        mTvUnReadCount.visibility = View.GONE
        systemUnreadNum = 0
        LiveDataBus.send(IMActions.SYSTEM_NOTICE_UNREAD_COUNT,systemUnreadNum)
    }

    fun getRecentContactData () {
        loginNim()
        mSrlRefresh.isRefreshing = true
        mRecentContactAdapter?.clear()
        mRecentContactAdapter?.notifyDataSetChanged()
        MainHandler.get().postDelayed(mRecentContactRunnable, 300)
    }

    fun loginNim () {
        Loger.e(TAG,"loginNim()......")
        if (!NimMessageManager.instance.hasLogin()) {
            var loginData = App.get().getLoginReq()?.data
            sendImLoginInfoRequest(loginData?.userId)
        }
    }

    fun sendImLoginInfoRequest (userId: String?) {
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(userId)) return
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        userVM.fetchImLoginInfo(token,userId)
    }

    fun search () {
        var query = mEtSearch.text.toString()
        if (TextUtils.isEmpty(query)) {
            ToastUtils.show("请输入搜索关键字")
            return
        }
        var count = mRecentContactAdapter?.getContentItemCount() ?: 0
        var result =  ArrayList<RecentContact>()
        if (count > 0) {
            mRecentContactAdapter?.getDatas()?.forEach {
                val userInfo: NimUserInfo? = NimMessageManager.instance.getNimUserInfo(it?.contactId)
                var nickname = userInfo?.name ?: ""
                if (nickname.contains(query)) {
                    result.add(it)
                }
            }
        }

        if (result.size > 0) {
            mTvNoData.visibility = View.GONE
            mRvRecentContact.visibility = View.VISIBLE
            mRecentContactAdapter?.clear()
            mRecentContactAdapter?.add(result)
            mRecentContactAdapter?.notifyDataSetChanged()
        } else {
            mTvNoData.visibility = View.VISIBLE
            mRvRecentContact.visibility = View.GONE
            mRecentContactAdapter?.clear()
            mRecentContactAdapter?.notifyDataSetChanged()
        }

    }

    private fun refreshRecentContacts(result: List<RecentContact>?) {
        Loger.e(TAG,"refreshRecentContacts-result?.size = " + result?.size)
        if (result == null) {
            mTvNoData?.visibility = View.VISIBLE
            mRvRecentContact?.visibility = View.GONE
            mRecentContactAdapter?.clear()
            mRecentContactAdapter?.notifyDataSetChanged()
            return
        }
        if (result.size == 0) {
            mTvNoData?.visibility = View.VISIBLE
            mRvRecentContact?.visibility = View.GONE
            mRecentContactAdapter?.clear()
            mRecentContactAdapter?.notifyDataSetChanged()
            return
        }
        mTvNoData?.visibility = View.GONE
        mRvRecentContact?.visibility = View.VISIBLE
        var mNetId = App.get().getUserInfo()?.imAccid
        for (item in result) {
            val index = getRecentItemIndex(item.contactId)

            var count = mRecentContactAdapter?.getContentItemCount() ?: 0
            if (index >= 0 && index < count) {
                mRecentContactAdapter?.removeItem(index)
                mRecentContactAdapter?.add(index,item)
                Collections.swap(mRecentContactAdapter?.getDatas(), index, 0)
                mRecentContactAdapter?.notifyItemChanged(index)
            } else {
                if (!TextUtils.equals(item.contactId, mNetId)) {
                    mRecentContactAdapter?.add(item)
                }
            }
        }
        sortRecentContacts(mRecentContactAdapter?.getDatas())
        mRecentContactAdapter?.notifyDataSetChanged()
    }

    fun setMsgUnReadCountText (unreadCount: Int) {
       /* mTabMessage.getTabAt(0)?.customView?.mTvUnReadCount?.text = unreadCount.toString()
        if (unreadCount > 0) {
            mTabMessage.getTabAt(0)?.customView?.mTvUnReadCount?.visibility = View.VISIBLE
        } else {
            mTabMessage.getTabAt(0)?.customView?.mTvUnReadCount?.visibility = View.GONE
        }*/
    }

    fun setSysUnReadCountText (unreadCount: Int) {
       /* mTabMessage.getTabAt(1)?.customView?.mTvUnReadCount?.text = unreadCount.toString()
        if (unreadCount > 0) {
            mTabMessage.getTabAt(1)?.customView?.mTvUnReadCount?.visibility = View.VISIBLE
        } else {
            mTabMessage.getTabAt(1)?.customView?.mTvUnReadCount?.visibility = View.GONE
        }*/
    }

    private fun sortRecentContacts(list: List<RecentContact>?) {
        if (list?.size == 0) {
            return
        }
        Collections.sort(list, comp)
    }

    private val comp = Comparator { recent1: RecentContact, recent2: RecentContact ->
        /*// 先比较置顶tag
        val sticky = (recent1.tag and RecentContactTagUtil.RECENT_TAG_STICKY) - (o2.tag and RecentContactTagUtil.RECENT_TAG_STICKY)
        if (sticky != 0L) {
            return@Comparator if (sticky > 0) -1 else 1
        } else {
            val time = recent1.time - recent2.time
            return@Comparator if (time == 0L) 0 else if (time > 0) -1 else 1
        }*/


        // 先比较置顶tag
        val isStickTop1 = NIMClient.getService(MsgService::class.java)
            .isStickTopSession(recent1.getContactId(), recent1.getSessionType())
        val isStickTop2 = NIMClient.getService(MsgService::class.java)
            .isStickTopSession(recent2.getContactId(), recent2.getSessionType())
        return@Comparator if (isStickTop1 xor isStickTop2) {
            if (isStickTop1) -1 else 1
        } else {
            val time: Long = recent1.getTime() - recent2.getTime()
            if (time == 0L) 0 else if (time > 0) -1 else 1
        }
    }


    private fun getMessageIndex(uuid: String): Int {
        var count = mRecentContactAdapter?.getContentItemCount() ?: 0
        for (i in 0 until count) {
            val item = mRecentContactAdapter?.getItem(i)
            if (TextUtils.equals(item?.recentMessageId, uuid)) {
                return i
            }
        }
        return -1
    }

    fun getRecentItemIndex(contactId: String?): Int {
        var count = mRecentContactAdapter?.getContentItemCount() ?: 0
        for (i in 0 until count) {
            val item = mRecentContactAdapter?.getItem(i)
            if (TextUtils.equals(item?.contactId, contactId)) {
                return i
            }
        }
        return -1
    }

    override fun handleBackEvent(): Boolean {
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (id) {
            ContentViewHolder.ONITEM_CLICK -> {
                var data = mRecentContactAdapter?.getItem(position)
                val sessionType = data?.sessionType
                val account = data?.contactId

                NimMessageManager.instance.clearUnreadCount(account, sessionType)
                ChatActivity.intentStart(activity as AppCompatActivity,account)
            }
            ContentViewHolder.ONITEM_LONG_CLICK -> {
                var data = mRecentContactAdapter?.getItem(position)
                var isStickTopSession =  NimMessageManager.instance.isStickTopSession(data)
                var recentContactMoreDialog = RecentContactMoreDialog(activity!!,isStickTopSession)
                recentContactMoreDialog.mOnRecentContactMoreListener = this
                recentContactMoreDialog.show()
            }
        }

    }

    override fun onRefresh() {
        getRecentContactData()
        sendUnreadNumRequest()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClear -> {
                mEtSearch.setText("")
                KeyBoardUtil.hideKeyBoard(activity,mEtSearch)
                getRecentContactData()
                sendUnreadNumRequest()
            }
            R.id.mIvMore -> {
                val xOffset = DensityUtil.dip2px(activity!!,ResUtils.getDimenPixRes(R.dimen.dp_50) * -1.0f)
                val yOffset = DensityUtil.dip2px(activity!!,ResUtils.getDimenPixRes(R.dimen.dp_0).toFloat())
                var messageMorePopWindow = MessageMorePopWindow(activity)
                messageMorePopWindow.mOnMessageMoreListener = this
                messageMorePopWindow.show(mIvMore, AbsPopWindow.LocationGravity.BOTTOM_CENTER,xOffset, yOffset)

              /*  var memberAccounts = ArrayList<String>()
                memberAccounts.add("10103271")
                memberAccounts.add("108853061618564732")
                memberAccounts.add("108942421633759608")
                NimMessageUtil.createAdvancedTeam(memberAccounts)*/
            }
            R.id.mClSysMessage -> {
                systemUnreadNum = 0
                LiveDataBus.send(IMActions.SYSTEM_NOTICE_UNREAD_COUNT,systemUnreadNum)
                SystemNoticeActivity.intentStart(activity as AppCompatActivity)
                UMengEventModule.report(activity, MessageEvent.view_system_notice)
            }
        }
    }

    override fun OnAllRead() {
        NimMessageManager.instance.clearAllUnreadCount()
        getRecentContactData()

        sendMarkReadRequest()
    }

    override fun OnDelAllContact() {
        NimMessageManager.instance.clearAllUnreadCount()
        LiveDataBus.send(IMActions.CLEAR_RECENT_CONTACT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainHandler.get().removeCallbacks(mRecentContactRunnable)
    }

    inner class RecentContactRunnable : Runnable {
        override fun run() {
            NimMessageManager.instance.getRecentContact(object :
                    RequestCallbackWrapper<List<RecentContact>>() {
                override fun onResult(
                        code: Int,
                        result: List<RecentContact>?,
                        exception: Throwable?
                ) {
                    val isFinishing = activity?.isFinishing ?: false
                    if (isFinishing) return

                    mSrlRefresh?.isRefreshing = false
                    if (code != ResponseCode.RES_SUCCESS.toInt() || result == null) {
                        mRecentContactAdapter?.clear()
                        mRecentContactAdapter?.notifyDataSetChanged()
                        return
                    }
                    val recentContacts = JsonUtils.toJSONString(result)
                    Loger.e(TAG, "onResult-recentContacts = $recentContacts")
                    refreshRecentContacts(result)
                }
            })
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search()
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable?) {
        var text = mEtSearch.text.toString()
        if (TextUtils.isEmpty(text)) {
            mIvClear.visibility = View.GONE
        } else {
            mIvClear.visibility = View.VISIBLE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun OnRecentContactSticky() {
        var recent = mRecentContactAdapter?.getItem(currentPosition)
        stickyRecentContact(recent)
    }

    override fun OnRecentContactDelete() {
        //先清除未读数据再删除会话
        var recent = mRecentContactAdapter?.getItem(currentPosition)

        if (NimMessageManager.instance.isStickTopSession(recent)) {
            NimMessageManager.instance.removeStickTopSession(recent)
        }

        val account = recent?.getContactId()
        val sessionType = recent?.sessionType
        NimMessageManager.instance.clearUnreadCount(account, sessionType)

        NimMessageManager.instance.deleteRecentContact(recent)

        MainHandler.get().postDelayed({
            mRecentContactAdapter?.removeItem(currentPosition)
            mRecentContactAdapter?.notifyItemRemoved(currentPosition)

            UMengEventModule.report(activity, MessageEvent.delete_chat_history)
        }, 200)
    }


    /**
     * 置顶会话处理
     */

    fun stickyRecentContact (recent: RecentContact?) {
        Loger.e(TAG,"stickyRecentContact()......")
        mLoadingDialog?.show()
        if (NimMessageManager.instance.isStickTopSession(recent)) {
            NimMessageManager.instance.removeStickTopSession(recent,object : RequestCallbackWrapper<Void>(){
                override fun onResult(code: Int, result: Void?, exception: Throwable?) {
                    mLoadingDialog?.dismiss()
                    if (ResponseCode.RES_SUCCESS.toInt() == code) {
                        sortRecentContacts(mRecentContactAdapter?.getDatas())
                        mRecentContactAdapter?.notifyDataSetChanged()
                    }
                }

            })
        } else {
            NimMessageManager.instance.addStickTopSession(recent,object: RequestCallbackWrapper<StickTopSessionInfo>(){
                override fun onResult(
                    code: Int,
                    result: StickTopSessionInfo?,
                    exception: Throwable?
                ) {
                    mLoadingDialog?.dismiss()
                    if (ResponseCode.RES_SUCCESS.toInt() == code) {
                        sortRecentContacts(mRecentContactAdapter?.getDatas())
                        mRecentContactAdapter?.notifyDataSetChanged()
                    }
                }
            })
        }
    }

}