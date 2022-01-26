package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.event.TalentEvent
import com.flash.worker.lib.common.interfaces.OnDialogOkCancelClickListener
import com.flash.worker.lib.common.module.UMengEventModule
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.dialog.CommonTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.JoinGuildParm
import com.flash.worker.lib.coremodel.data.req.GuildDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildImageAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.act_guild_detail_header.*
import kotlinx.android.synthetic.main.activity_guild_detail.*
import kotlinx.android.synthetic.main.activity_guild_detail.appbar
import kotlinx.android.synthetic.main.activity_guild_detail.mSrlRefresh

@Route(path = ARouterPath.GuildDetailAct)
class GuildDetailActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,guildId: String?,memberType: Int) {
            var intent = Intent(activity, GuildDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,guildId)
            intent.putExtra(Constant.MEMBER_TYPE_KEY,memberType)
            activity.startActivity(intent)
        }
    }

    var memberType: Int = 2//1,会长；2，成员

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var guildId: String? = null
    var mGuildImageAdapter: GuildImageAdapter? = null
    var mGuildDetailReq: GuildDetailReq? = null

    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    override fun getLayoutResource() = R.layout.activity_guild_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        setNavigationBarBottomPading = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mGuildImageAdapter = GuildImageAdapter(this,this)
        mGuildImageAdapter?.isPreview = true
        mRvImage.adapter = mGuildImageAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvGuildRegulation.setOnClickListener(this)
        mTvJoin.setOnClickListener(this)

        appbar.addOnOffsetChangedListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the ViewHolder for the clicked position.
                        val selectedViewHolder: RecyclerView.ViewHolder = mRvImage
                                .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvRelatedCertificate)
                    }
                })
    }

    fun initData (intent: Intent?) {
        guildId = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        memberType = intent?.getIntExtra(Constant.MEMBER_TYPE_KEY,2) ?: 2

        if (memberType == 1) {//会长
            mTvJoin.visibility = View.GONE
        } else if (memberType == 2) {//成员
            mTvJoin.visibility = View.VISIBLE
        }

        sendGuildDetailRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
                .observeForever(this, Observer {
                    currentWorkImagePositon = it as Int
                })

        LiveDataBus.with(JobActions.REFRESH_GUILD_DETAIL)
            .observeForever(this, Observer {
                sendGuildDetailRequest()
            })
    }

    fun subscribeUi() {
        guildVM.guildDetailData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showGuildDetailData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildVM.joinGuildData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    ToastUtils.show("恭喜您，已成功加入公会")
                    MyGuildActivity.intentStart(this,mGuildDetailReq?.data)
                    UMengEventModule.report(this, TalentEvent.talent_join_guild)
                    LiveDataBus.send(MineActions.BACK_JOIN_GUILD_ACT)
                    onBackPressed()
                }
                is HttpResult.Error -> {
                    if (TextUtils.equals("155005",it.code)) {
                        showHasJoinedGuildTipDlg(it.message)
                        return@Observer
                    }
                    if (TextUtils.equals("001050",it.code)) {
                        showHasJoinedGuildCommonTipDlg(it.message)
                        return@Observer
                    }
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendGuildDetailRequest () {
        if (!App.get().hasLogin()) {
            mSrlRefresh.isRefreshing = false
        }
        mSrlRefresh.isRefreshing = true
        val token = App.get().getLoginReq()?.data?.token
        guildVM.fetchGuildDetail(token,guildId)
    }

    fun sendJoinRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        if (mGuildDetailReq == null) {
            ToastUtils.show("数据错误，请重试")
            return
        }
        if (mGuildDetailReq?.data == null) {
            ToastUtils.show("数据错误，请重试")
            return
        }
        if (TextUtils.isEmpty(mGuildDetailReq?.data?.guildId)) {
            ToastUtils.show("数据错误，请重试")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        val body = JoinGuildParm()
        body.guildId = mGuildDetailReq?.data?.guildId

        guildVM.joinGuild(token,body)
    }

    fun showGuildDetailData (data: GuildDetailReq) {
        mGuildDetailReq = data
        ImageUtils.instance.loadImage(this,mCivAvatar,data.data?.headpic,R.mipmap.ic_president_avatar)
        mTvTitle.text = data.data?.guildName
        mTvPresidentName.text = "会长:${data.data?.ownerUsername}"

        var peopleNum = data?.data?.peopleNum ?: 0
        if (peopleNum < 100) {
            mTvPeopleCount.text = "人数：100以内"
        } else {
            mTvPeopleCount.text = "人数:${data.data?.peopleNum}"
        }

        mTvCity.text = "所在城市:${data.data?.guildCity}"

        mTvEstablishTime.text = "成立时间:${data.data?.establishTime}"
        mTvIntroduction.text = "公会简介:${data.data?.guildProfile}"

        mGuildImageAdapter?.clear()
        mGuildImageAdapter?.add(getPics(data.data?.profilePics))
        mGuildImageAdapter?.notifyDataSetChanged()
    }

    fun getPics(urls: List<String>?): ArrayList<WorkPicInfo> {
        var pics = ArrayList<WorkPicInfo>()
        urls?.let {
            urls?.forEach {
                var item = WorkPicInfo()
                item.pic = it
                pics.add(item)
            }
        }
       return pics
    }

    fun showJoinGuildTipDlg () {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才:\n\t\t\t\t请确认您要加入${mGuildDetailReq?.data?.guildName}！\n" +
                "\t\t\t\t每个人才只能加入一个公会，您可在退出后于每月1号加入新公会。"
        commonTipDialog.mCancelText = "取消"
        commonTipDialog.mOkText = "确认"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                sendJoinRequest()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showHasJoinedGuildCommonTipDlg (msg: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.cancelBtnVisible = View.GONE
        commonTipDialog.mContent = "尊敬的人才:\n\t\t\t\t$msg"
        commonTipDialog.mOkText = "返回"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                onBackPressed()
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun showHasJoinedGuildTipDlg (msg: String?) {
        var commonTipDialog = CommonTipDialog(this)
        commonTipDialog.contentGravity = Gravity.LEFT
        commonTipDialog.mTitle = "温馨提示"
        commonTipDialog.mContent = "尊敬的人才:\n\t\t\t\t$msg"
        commonTipDialog.mCancelText = "返回"
        commonTipDialog.mOkText = "接活发布"
        commonTipDialog.mOnDialogOkCancelClickListener = object : OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                NavigationUtils.goTalentNewReleaseActivity(this@GuildDetailActivity)
            }

            override fun OnDialogCancelClick() {
            }

        }
        commonTipDialog.show()
    }

    fun getViewImageUrls(urls: List<WorkPicInfo>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                var item = WorkPicInfo()
                it.pic?.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvGuildRegulation -> {
                var regulation = mGuildDetailReq?.data?.guildRules
                GuildRegulationActivity.intentStart(this,regulation)
            }
            R.id.mTvJoin -> {
                showJoinGuildTipDlg()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentWorkImagePositon = position
        ViewImageActivity.intentStart(this,
                getViewImageUrls(mGuildImageAdapter?.getDatas()),
                position,
                view?.findViewById(R.id.mIvRelatedCertificate),
                ResUtils.getStringRes(R.string.img_transition_name))
    }

    override fun onRefresh() {
        sendGuildDetailRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        var totalScrollRange = appBarLayout?.totalScrollRange ?: 0
        if (verticalOffset == 0) {
            //展开状态
            mSrlRefresh.isEnabled = true
        } else if (Math.abs(verticalOffset) >= totalScrollRange) {
            //折叠状态
            mSrlRefresh.isEnabled = false
        } else {
            //中间状态
        }
    }

}