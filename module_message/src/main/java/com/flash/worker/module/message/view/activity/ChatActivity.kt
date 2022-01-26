package com.flash.worker.module.message.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.event.MessageEvent
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.anim.SlideInBottomAnimatorAdapter
import com.flash.worker.lib.coremodel.data.bean.HomeEmployerDetailData
import com.flash.worker.lib.coremodel.data.bean.HomeTalentDetailData
import com.flash.worker.lib.coremodel.data.bean.TalentResumeDetialData
import com.flash.worker.lib.coremodel.data.bean.TaskDetailData
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.UserVM
import com.flash.worker.lib.im.*
import com.flash.worker.lib.livedatabus.action.IMActions
import com.flash.worker.lib.livedatabus.action.LoginActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.message.R
import com.flash.worker.module.message.data.LocationMessageBean
import com.flash.worker.module.message.view.adapter.MessageAdapter
import com.flash.worker.module.message.view.fragment.BottomCommonWordsFragment
import com.flash.worker.module.message.view.fragment.BottomEmojiFragment
import com.flash.worker.module.message.view.fragment.BottomMoreFragment
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.rv_message_location_send_cell.view.*
import java.util.*


@Route(path = ARouterPath.ChatAct)
class ChatActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,TextWatcher,
        View.OnTouchListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,account: String?,msg: String? = null) {
            var intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(Constant.NIM_ACCOUNT_KEY,account)
            intent.putExtra(Constant.INTENT_DATA_KEY,msg)
            activity.startActivity(intent)
        }
    }

    var userAccount: String? = null
    var msg: String? = null
    var mMessageAdapter: MessageAdapter? = null
    var currentBottomFragment: BaseFragment? = null
    var isFirstPage: Boolean = true

    override fun getLayoutResource() = R.layout.activity_chat

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    override fun onResume() {
        super.onResume()
        var teamNick = TeamNotificationUtil.getTeamNickName(userAccount)
        if (TextUtils.isEmpty(teamNick)) {
            NimMessageManager.instance.setChattingAccount(userAccount, SessionTypeEnum.P2P)
        } else {
            NimMessageManager.instance.setChattingAccount(userAccount, SessionTypeEnum.Team)
        }
    }

    override fun onPause() {
        super.onPause()
        NimMessageManager.instance.setChattingAccountNone()
    }

    fun initialize () {
        subscribeEvent()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mMessageAdapter = MessageAdapter(this,this)

        val animatorAdapter
                = SlideInBottomAnimatorAdapter(mMessageAdapter,mRvMessage)

        mRvMessage.adapter = animatorAdapter

        mIvBack.setOnClickListener(this)
        mIvCommonWords.setOnClickListener(this)
        mIvEmoji.setOnClickListener(this)
        mIvAdd.setOnClickListener(this)
        mBtnSend.setOnClickListener(this)

        mEtMessage.setOnTouchListener(this)

        mEtMessage.addTextChangedListener(this)

        if (!App.get().hasLogin()) {
            LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
            onBackPressed()
            return
        }
    }

    fun initData (intent: Intent?) {
        userAccount = intent?.getStringExtra(Constant.NIM_ACCOUNT_KEY)
        msg = intent?.getStringExtra(Constant.INTENT_DATA_KEY)

        Loger.e(TAG,"initData-userAccount = $userAccount")
        Loger.e(TAG,"initData-msg = $msg")

        if (!TextUtils.isEmpty(userAccount)) {
            mMessageAdapter?.clear()
            mMessageAdapter?.notifyDataSetChanged()
            loadHistoryMessage()

            refreshNimUserInfo()
        }

        if (!TextUtils.isEmpty(msg)) {
            mEtMessage.setText(msg)
            sendTxtMessage(null)
        }

    }

    fun loadHistoryMessage () {
        var anchor = getAnchor()
        NimMessageManager.instance.getLocalHistoryMessage(userAccount,anchor, historyMessageCallback)
    }

    fun subscribeEvent() {
        LiveDataBus.with(Constant.Action.COLLAPSE_INPUT_PANEL).observe(this,
            Observer {
                KeyBoardUtil.hideKeyBoard(this,mEtMessage)
                mFlBottomPanel.visibility = View.GONE

                mIvCommonWords.setImageResource(R.mipmap.ic_message_common)
                mIvEmoji.setImageResource(R.mipmap.ic_emoji)
        })

        LiveDataBus.with(IMActions.NEW_MESSAGE)
            .observe(this, Observer {
                var message = it as IMMessage
                if (NimMessageUtil.isMyMessage(message,userAccount)) {
                    refreshMessage(message,false)
                    refreshNimUserInfo()
                }
            })

        LiveDataBus.with(IMActions.SEND_IMAGE_MESSAGE)
            .observe(this, Observer {
                var path = it.toString()
                sendImgMsg(path)
            })

        LiveDataBus.with(IMActions.SEND_RESUME_MESSAGE)
            .observe(this, Observer {
                var data: TalentResumeDetialData = it as TalentResumeDetialData
                sendResumeMsg(data)
            })

        LiveDataBus.with(IMActions.SEND_JOB_MESSAGE)
            .observe(this, Observer {
                var data: HomeEmployerDetailData = it as HomeEmployerDetailData
                sendJobMsg(data)
            })

        LiveDataBus.with(IMActions.SEND_TASK_MESSAGE)
            .observe(this, Observer {
                var data: TaskDetailData = it as TaskDetailData
                sendTaskMsg(data)
            })

        LiveDataBus.with(IMActions.SEND_LOCATION_MESSAGE)
            .observe(this, Observer {
                var data: LocationMessageBean = it as LocationMessageBean
                sendLocationMsg(data)
            })

        LiveDataBus.with(IMActions.SEND_COMMON_WORDS_MESSAGE)
            .observe(this, Observer {
                var data: String = it as String
                sendTxtMessage(data)
            })

        LiveDataBus.with(IMActions.REFRESH_MESSAGE_STATUS)
            .observe(this, Observer {
                var message = it as IMMessage
                val index: Int = getMessageIndex(message.uuid)
                var count = mMessageAdapter?.getContentItemCount() ?: 0
                if (index >= 0 && index < count) {
                    val item = mMessageAdapter?.getItem(index)
                    item?.status = message.status
                    mMessageAdapter?.notifyItemChanged(index)
                }
            })
    }

    fun refreshNimUserInfo () {
        var teamNick = TeamNotificationUtil.getTeamNickName(userAccount)
        if (TextUtils.isEmpty(teamNick)) {
            NimMessageManager.instance.setNimNickNameAvatar(
                this, userAccount, mTvNickName, null,R.mipmap.ic_avatar)
        } else {
            var memberCount = TeamNotificationUtil.getTeamMemberCount(userAccount)
            mTvNickName.text = "$teamNick($memberCount)"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mIvCommonWords -> {
                mIvEmoji.setImageResource(R.mipmap.ic_emoji)
                if (mFlBottomPanel.visibility == View.GONE) {
                    KeyBoardUtil.hideKeyBoard(this,mEtMessage)

                    MainHandler.get().postDelayed({
                        mFlBottomPanel.visibility = View.VISIBLE
                    }, 200)

                    var commonWordsFrag = BottomCommonWordsFragment.newInstance()
                    switchBottomPanelFragment(commonWordsFrag)

                    mIvCommonWords.setImageResource(R.mipmap.ic_message_input)
                } else {
                    if (currentBottomFragment is BottomCommonWordsFragment) {
                        mFlBottomPanel.visibility = View.GONE

                        KeyBoardUtil.showKeyBoard(this,mEtMessage)

                        mIvCommonWords.setImageResource(R.mipmap.ic_message_common)

                    } else {
                        var commonWordsFrag = BottomCommonWordsFragment.newInstance()
                        switchBottomPanelFragment(commonWordsFrag)

                        mIvCommonWords.setImageResource(R.mipmap.ic_message_input)
                    }

                }
                scrollToLastPosition()
            }
            R.id.mIvEmoji -> {
                mIvCommonWords.setImageResource(R.mipmap.ic_message_common)
                if (mFlBottomPanel.visibility == View.GONE) {
                    KeyBoardUtil.hideKeyBoard(this,mEtMessage)

                    MainHandler.get().postDelayed({
                        mFlBottomPanel.visibility = View.VISIBLE
                    }, 200)

                    var emojiFrag = BottomEmojiFragment.newInstance()
                    emojiFrag.mEvMessage = mEtMessage
                    switchBottomPanelFragment(emojiFrag)

                    mIvEmoji.setImageResource(R.mipmap.ic_keyboard)
                } else {
                    if (currentBottomFragment is BottomEmojiFragment) {
                        mFlBottomPanel.visibility = View.GONE

                        KeyBoardUtil.showKeyBoard(this,mEtMessage)

                        mIvEmoji.setImageResource(R.mipmap.ic_emoji)

                    } else {
                        var emojiFrag = BottomEmojiFragment.newInstance()
                        emojiFrag.mEvMessage = mEtMessage
                        switchBottomPanelFragment(emojiFrag)

                        mIvEmoji.setImageResource(R.mipmap.ic_keyboard)
                    }

                }
                scrollToLastPosition()
            }
            R.id.mIvAdd -> {
                mIvEmoji.setImageResource(R.mipmap.ic_emoji)

                if (mFlBottomPanel.visibility == View.GONE) {
                    KeyBoardUtil.hideKeyBoard(this,mEtMessage)

                    MainHandler.get().postDelayed({
                        mFlBottomPanel.visibility = View.VISIBLE
                    }, 200)

                    var moreFrag = BottomMoreFragment.newInstance(userAccount)
                    switchBottomPanelFragment(moreFrag)

                } else {
                    if (currentBottomFragment is BottomMoreFragment) {
                        mFlBottomPanel.visibility = View.GONE

                        KeyBoardUtil.showKeyBoard(this,mEtMessage)
                    } else {
                        var moreFrag = BottomMoreFragment.newInstance(userAccount)
                        switchBottomPanelFragment(moreFrag)
                    }
                }
                scrollToLastPosition()
            }
            R.id.mBtnSend -> {
                sendTxtMessage(null)
            }
        }
    }

    fun sendTxtMessage (commonWords: String?) {
        Loger.e(TAG,"sendTxtMessage()......")
        if (!App.get().hasLogin()) {
            LiveDataBus.send(LoginActions.GO_ONE_KEY_LOGIN)
            return
        }
        var message = mEtMessage.text.toString()

        if (!TextUtils.isEmpty(commonWords)) {
            message = commonWords ?: ""
        }
        if (TextUtils.isEmpty(message)) {
            ToastUtils.show(R.string.message_enter_content_tip)
            return
        }

        Loger.e(TAG,"sendTxtMessage()......message = $message")
        val imMessage = NimMessageUtil.sendTxtMessage(userAccount,message)

        refreshMessage(imMessage,true)
    }

    fun sendImgMsg(imgPath: String?) {
        if (!NetworkUtils.isNetworkAvailable(this))  {
            ToastUtils.showShort(R.string.network_error)
            return
        }
        val imMessage = NimMessageUtil.sendImageMessage(userAccount, imgPath)
        refreshMessage(imMessage,true)

        UMengEventModule.report(this, MessageEvent.send_image_message)
    }

    fun sendResumeMsg(data: TalentResumeDetialData?) {
        if (!NetworkUtils.isNetworkAvailable(this))  {
            ToastUtils.showShort(R.string.network_error)
            return
        }
        val imMessage = NimMessageUtil.sendResumeMessage(userAccount, JsonUtils.toJSONString(data))
        refreshMessage(imMessage,true)

        UMengEventModule.report(this, MessageEvent.send_resume_message)
    }

    fun sendJobMsg(data: HomeEmployerDetailData?) {
        if (!NetworkUtils.isNetworkAvailable(this))  {
            ToastUtils.showShort(R.string.network_error)
            return
        }
        val imMessage = NimMessageUtil.sendJobMessage(userAccount,
            JsonUtils.toJSONString(data),"[岗位邀请]")
        refreshMessage(imMessage,true)

        UMengEventModule.report(this, MessageEvent.send_job_message)
    }

    fun sendTaskMsg(data: TaskDetailData?) {
        if (!NetworkUtils.isNetworkAvailable(this))  {
            ToastUtils.showShort(R.string.network_error)
            return
        }
        val imMessage = NimMessageUtil.sendTaskMessage(userAccount,
            JsonUtils.toJSONString(data),"[任务邀请]")
        refreshMessage(imMessage,true)
    }

    fun sendLocationMsg(data: LocationMessageBean?) {
        if (!NetworkUtils.isNetworkAvailable(this))  {
            ToastUtils.showShort(R.string.network_error)
            return
        }
        val imMessage = NimMessageUtil.sendLocationMessage(
            userAccount,data?.latitude,data?.longitude,data?.address)
        refreshMessage(imMessage,true)
    }

    private fun refreshMessage(nimMessage: IMMessage?,clearInput: Boolean) {
        Loger.e(TAG,"refreshMessage-clearInput = $clearInput")
        mMessageAdapter?.add(nimMessage)
        mMessageAdapter?.notifyDataSetChanged()
        if (clearInput) {
            mEtMessage.setText("")
        } else {
            if (NimMessageUtil.isTeamChat(userAccount)) {
                var isScrollBottom = mRvMessage?.isScrollBottom() ?: false
                Loger.e(TAG,"refreshMessage-isScrollBottom = $isScrollBottom")
                if (!isScrollBottom) return//群聊时如果用户在查看聊天记录不滚动到底部
            }
        }
        scrollToLastPosition()
    }

    private fun addMessageItem(messageList: List<IMMessage>?) {
        if (messageList == null) return
        if (messageList?.size == 0) return

        for (message in messageList) {
            if (NimMessageUtil.isMyMessage(message,userAccount)) {
                if (isFirstPage) {
                    mMessageAdapter?.add(message)
                } else {
                    mMessageAdapter?.add(0,message)
                }
                //发送处理已读回执
                if (message.msgType == MsgTypeEnum.text
                    || message.msgType == MsgTypeEnum.image
                    || message.msgType == MsgTypeEnum.video
                    || message.msgType == MsgTypeEnum.location
                ) {
//                    NimMessageManager.instance.sendIsRemoteRead(message, message.fromAccount)
                }
            }
        }
        mMessageAdapter?.notifyDataSetChanged()

        if (isFirstPage) {
            scrollToLastPosition()
        } else {
            scrollToPosition(messageList?.size + 1)
        }
    }

    /**
     * 滚动到最后一个
     */
    private fun scrollToLastPosition() {
        MainHandler.get().postDelayed({
            var contentCount = mMessageAdapter?.getContentItemCount() ?: 0
            val position = Math.max(0, contentCount - 1)
            mRvMessage.scrollToPosition(position)
        },200)
    }
    /**
     * 滚动指定位置
     */
    private fun scrollToPosition(position: Int) {
        MainHandler.get().postDelayed({
            mRvMessage.scrollToPosition(position)
        },200)
    }

    private fun getAnchor(): IMMessage? {
        var itemCount = mMessageAdapter?.getContentItemCount() ?: 0
        isFirstPage = itemCount == 0

        if (itemCount == 0) {
            val newTime = System.currentTimeMillis()

            var sessionType = SessionTypeEnum.P2P
            var teamNick = TeamNotificationUtil.getTeamNickName(userAccount)
            if (!TextUtils.isEmpty(teamNick)) {
                sessionType = SessionTypeEnum.Team
            }
            return MessageBuilder.createEmptyMessage(userAccount, sessionType, newTime)
        } else {
            return mMessageAdapter?.getItem(0)
        }
    }

    private fun getMessageIndex(uuid: String): Int {
        var count = mMessageAdapter?.getContentItemCount() ?: 0
        for (i in 0 until count) {
            val item = mMessageAdapter?.getItem(i)
            if (TextUtils.equals(item?.uuid, uuid)) {
                return i
            }
        }
        return -1
    }

    private val historyMessageCallback: RequestCallback<List<IMMessage>> =
        object : RequestCallbackWrapper<List<IMMessage>>() {
            override fun onResult(
                code: Int,
                messages: List<IMMessage>?,
                exception: Throwable?
            ) {
                Loger.e(TAG, "getHistoryMessage-onResult-messages = " + JsonUtils.toJSONString(messages))
                if (code == ResponseCode.RES_SUCCESS.toInt() && messages != null) {
                    if (isFirstPage) {
                        Collections.reverse(messages) // 倒序排列
                    }
                    addMessageItem(messages)
                    mSrlRefresh.isRefreshing = false
                }
            }
        }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mCivAvatar -> {
//                NavigationUtils.goTalentResumeDetailActivity(this,"")
                var message = mMessageAdapter?.getItem(position)
                var isReceivedMessage = NimMessageUtil.isReceivedMessage(message)
                if (isReceivedMessage) {
                    MessageUserDetailActivity.intentStart(this,message?.fromAccount)
                }
            }
            R.id.mIvMessage -> {
                var imageUrl = ""
                val msgAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (msgAttachment is ImageAttachment) {
                    val imageAttachment = msgAttachment
                    var path = imageAttachment.path
                    if (TextUtils.isEmpty(path)) {
                        path = imageAttachment.url
                    }
                    imageUrl = path
                }

                ViewMessageImageActivity.intentStart(this,imageUrl,
                    view?.findViewById<ImageView>(R.id.mIvMessage),
                    ResUtils.getStringRes(R.string.message_image_transition_name))
            }
            R.id.mRlJob -> {
                val msgAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (msgAttachment is JobAttachment) {
                    var jobJson = msgAttachment.jobData ?: ""
                    if (!TextUtils.isEmpty(jobJson)) {
                        val employerDetailData =
                            JsonUtils.parseObject(jobJson, HomeEmployerDetailData::class.java)
                        var releaseId = employerDetailData?.id
                        NavigationUtils.goJobDetailActivity(this,releaseId,
                            null,null, JobDetailAction.NORMAL)
                    }
                }
            }
            R.id.mRlTask -> {
                val msgAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (msgAttachment is TaskAttachment) {
                    var taskJson = msgAttachment.taskData ?: ""
                    if (!TextUtils.isEmpty(taskJson)) {
                        val taskDetailData =
                            JsonUtils.parseObject(taskJson, TaskDetailData::class.java)
                        var releaseId = taskDetailData?.releaseId
                        NavigationUtils.goTaskDetailActivity(this,releaseId,
                            null,null, TaskDetailAction.NORMAL)
                    }
                }
            }
            R.id.mRlResume -> {
                val msgAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (msgAttachment is ResumeAttachment) {
                    var resumeJson = msgAttachment.resumeData ?: ""
                    if (!TextUtils.isEmpty(resumeJson)) {
                        val resumeDetailData =
                            JsonUtils.parseObject(resumeJson, TalentResumeDetialData::class.java)

                        var resumeId = resumeDetailData?.resumeId
                        NavigationUtils.goTalentResumeDetailActivity(this,resumeId)
                    }
                }
            }
            R.id.mRlTalentRelease -> {
                val msgAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (msgAttachment is TalentReleaseAttachment) {
                    var talentReleaseJson = msgAttachment.talentReleaseData ?: ""
                    if (!TextUtils.isEmpty(talentReleaseJson)) {
                        val resumeDetailData =
                            JsonUtils.parseObject(talentReleaseJson, HomeTalentDetailData::class.java)

                        var releaseId = resumeDetailData?.talentReleaseInfo?.releaseId
                        NavigationUtils.goTalentDetailActivity(this,releaseId, TalentDetailAction.NORMAL)
                    }
                }
            }
            R.id.mCvLocation -> {
                val locationAttachment = mMessageAdapter?.getItem(position)?.attachment
                if (locationAttachment is LocationAttachment) {
                    NavigationUtils.goViewLocationActivity(this,locationAttachment)
                }

            }
            R.id.mIvReSend -> {
                reSendMessage(position)
            }
            else -> {
            }
        }
    }

    fun reSendMessage (position: Int) {
        Loger.e(TAG,"reSendMessage()......")

        var message = mMessageAdapter?.getItem(position)
        message?.status = MsgStatusEnum.sending
        NimMessageManager.instance.deleteChattingHistory(message)

        mMessageAdapter?.removeItem(position)
        mMessageAdapter?.notifyItemRemoved(position)

        val imMessage = NimMessageUtil.reSendMessage(message)
        refreshMessage(imMessage,true)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mFlBottomPanel.visibility = View.GONE

            mIvEmoji.setImageResource(R.mipmap.ic_emoji)
            mIvCommonWords.setImageResource(R.mipmap.ic_message_common)

            scrollToLastPosition()
        }
        return false
    }

    fun switchBottomPanelFragment(fragment: BaseFragment) {
        if (isFinishing) return
        if (currentBottomFragment != null) {
            if (!fragment.javaClass?.name.equals(currentBottomFragment?.javaClass?.name)) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mFlBottomPanel, fragment).commitAllowingStateLoss()
                currentBottomFragment = fragment
            }
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mFlBottomPanel, fragment).commitAllowingStateLoss()
            currentBottomFragment = fragment
        }
    }

    override fun afterTextChanged(s: Editable?) {
        var length = s?.length ?: 0
        if (length > 0) {
            mIvAdd.visibility = View.GONE
            mBtnSend.visibility = View.VISIBLE
        } else {
            mIvAdd.visibility = View.VISIBLE
            mBtnSend.visibility = View.GONE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onRefresh() {
        loadHistoryMessage()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}