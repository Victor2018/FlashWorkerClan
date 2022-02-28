package com.flash.worker.module.hire.view.fragment

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.bigkoo.convenientbanner.ConvenientBanner
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.TalentDetailAction
import com.flash.worker.lib.common.interfaces.*
import com.flash.worker.lib.common.provider.LoginService
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.adapter.*
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.GuildRedEnvelopeTipDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.holder.BannerImageHolderView
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SearchTalentReleaseParm
import com.flash.worker.lib.coremodel.data.req.AnnouncementReq
import com.flash.worker.lib.coremodel.data.req.BannerReq
import com.flash.worker.lib.coremodel.data.req.SearchTalentReleaseReq
import com.flash.worker.lib.coremodel.data.req.TalentTypeReq
import com.flash.worker.lib.coremodel.util.InjectorUtils.provideCommonVMFactory
import com.flash.worker.lib.coremodel.util.InjectorUtils.provideGuildRedEnvelopeVMFactory
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.GuildRedEnvelopeVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.coremodel.viewmodel.HomeVM
import com.flash.worker.lib.coremodel.viewmodel.factory.GuildVMFactory
import com.flash.worker.lib.coremodel.viewmodel.factory.HomeVMFactory
import com.flash.worker.lib.livedatabus.action.*
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.hire.R
import com.flash.worker.module.hire.view.activity.TalentDetailActivity
import com.flash.worker.module.hire.view.activity.TalentSearchActivity
import com.flash.worker.module.hire.view.adapter.SearchTalentReleaseAdapter
import com.google.android.material.appbar.AppBarLayout
import com.sunfusheng.marqueeview.MarqueeView
import kotlinx.android.synthetic.main.fragment_hire.*
import kotlinx.android.synthetic.main.fragment_hire_header.*
import kotlinx.android.synthetic.main.fragment_hire_content.*
import kotlinx.android.synthetic.main.hire_complex_menu.view.*
import kotlinx.android.synthetic.main.hire_filter_menu.view.*
import kotlinx.android.synthetic.main.hire_talent_type_menu.view.*
import java.net.URLEncoder
import kotlin.collections.ArrayList


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HireFragment
 * Author: Victor
 * Date: 2020/11/27 16:01
 * Description:
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.HireFgt)
class HireFragment: BaseFragment(),  SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
    AdapterView.OnItemClickListener, LMRecyclerView.OnLoadMoreListener, OnCityPickerListener,
    OnDropDownMenuClickListener, MarqueeView.OnItemClickListener, CompoundButton.OnCheckedChangeListener,
    AppBarLayout.OnOffsetChangedListener, OnLocationListener, OnJoinGuildListener {

    private lateinit var homeVM: HomeVM
    private lateinit var commonVM: CommonVM
    private lateinit var guildRedEnvelopeVM: GuildRedEnvelopeVM
    private lateinit var guildVM: GuildVM

    @Autowired(name = ARouterPath.loginService)
    @JvmField
    var loginService: LoginService? = null

    var mLoadingDialog: LoadingDialog? = null
    var mSearchTalentReleaseAdapter: SearchTalentReleaseAdapter? = null
    var currentPage = 1

    var headers: Array<String>? = null
    var popupViews: MutableList<View> = ArrayList()

    var contentView: View? = null
    var talentTypeView: View? = null
    var complexView: View? = null
    var filterView: View? = null

    var mTalentTypeAdapter: TalentTypeAdapter? = null
    var mTalentAdapter: TalentAdapter? = null
    var mComplexAdapter: ComplexAdapter? = null
    var mWorkAreaAdapter: WorkAreaAdapter? = null

    var mCityPickerDialog: CityPickerDialog? = null

    var provinceData: List<ProvinceInfo>? = null

    var mSearchTalentReleaseParm: SearchTalentReleaseParm? = null

    var cityAreaData: List<AreaInfo>? = null

    var mGuildRedEnvelopeTipDialog: GuildRedEnvelopeTipDialog? = null
    var currentGuildId: String? = null

    companion object {
        fun newInstance(): HireFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): HireFragment {
            val fragment = HireFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_hire
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    override fun onResume() {
        super.onResume()

        if (App.get().hasLocation()) {
            if (!TextUtils.isEmpty(App.get().getUserInfo()?.username)) {
                sendGuildRedEnvelopeTipsRequest()
            }
        } else {
            if (!App.get().isLocationPermissionDeclined && !App.get().isLocationPermissionPermanentlyDenied) {
                AmapLocationUtil.instance.getLocation(activity!!,this)
            }
        }
    }

    fun initialize () {
        homeVM = ViewModelProvider(this, HomeVMFactory(this))
            .get(HomeVM::class.java)

        commonVM = ViewModelProvider(this, provideCommonVMFactory(this))
            .get(CommonVM::class.java)

        guildRedEnvelopeVM = ViewModelProvider(this, provideGuildRedEnvelopeVMFactory(this))
            .get(GuildRedEnvelopeVM::class.java)

        guildVM = ViewModelProvider(this, GuildVMFactory(this))
            .get(GuildVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()
        subscribeEvent()

        headers = ResUtils.getStringArrayRes(R.array.hire_job_menu_titles)

        talentTypeView = layoutInflater.inflate(R.layout.hire_talent_type_menu, null)
        complexView = layoutInflater.inflate(R.layout.hire_complex_menu, null)
        filterView = layoutInflater.inflate(R.layout.hire_filter_menu, null)
        filterView?.mTvReset?.setOnClickListener(this)
        filterView?.mTvConfirm?.setOnClickListener(this)
        filterView?.mChkMale?.setOnCheckedChangeListener(this)
        filterView?.mChkFemale?.setOnCheckedChangeListener(this)


        mTalentTypeAdapter = TalentTypeAdapter(activity!!, this)
        talentTypeView?.mRvTalentType?.adapter = mTalentTypeAdapter

        mTalentAdapter = TalentAdapter(activity!!, this)
        talentTypeView?.mRvTalent?.adapter = mTalentAdapter

        mComplexAdapter = ComplexAdapter(activity!!, this)
        complexView?.mRvComplex?.adapter = mComplexAdapter

        var complexData = ResUtils.getStringArrayRes(R.array.hire_complex_titles)
        mComplexAdapter?.clear()
        mComplexAdapter?.add(complexData?.toList())
        mComplexAdapter?.notifyDataSetChanged()

        mWorkAreaAdapter = WorkAreaAdapter(activity!!, this)
        filterView?.mRvWorkArea?.adapter = mWorkAreaAdapter

        popupViews.clear()
        popupViews.add(talentTypeView!!)
        popupViews.add(complexView!!)
        popupViews.add(filterView!!)

        contentView = layoutInflater.inflate(R.layout.fragment_hire_content, null)
        //init dropdownview
        mHireDropDownMenu.tabMenuView?.removeAllViews()
        mHireDropDownMenu.popupMenuViews?.removeAllViews()
        mHireDropDownMenu.setDropDownMenu(headers?.toList()!!, popupViews, contentView)
        mHireDropDownMenu.mOnDropDownMenuClickListener = this

        mSearchTalentReleaseAdapter = SearchTalentReleaseAdapter(activity!!,this)

        val animatorAdapter = AlphaAnimatorAdapter(mSearchTalentReleaseAdapter,mRvHire)
        mRvHire.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mHireNoticeView.setOnItemClickListener(this)
        mRvHire.setLoadMoreListener(this)

        mTvLocationCity.setOnClickListener(this)
        mTvSearch.setOnClickListener(this)
        appbar.addOnOffsetChangedListener(this)
    }

    fun initData () {
//        var message = loginService?.toLoginView("18813938928",423099).toString()
//
//        Loger.e(TAG,"initData-message = $message")
//        ToastUtils.showShort(message)

        provinceData = App.get().getCityData()

        sendTalentTypeRequest()
        sendBannerRequest()
        sendAnnouncementRequest()

        initTalentData()
    }

    fun initTalentData () {
        currentPage = 1
        mTvLocationCity.text = App.get().mCity
        sendSearchTalentReleaseRequest(mSearchTalentReleaseParm)
        sendCityAreaRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(TalentActions.CHECK_GUILD_RED_ENVELOPE)
            .observe(this, Observer {
                AmapLocationUtil.instance.getLocation(activity!!,this)
            })

        LiveDataBus.with(JobActions.JOB_CITY_CHANGED)
            .observeForever(this, Observer {
                currentPage = 1
                mSearchTalentReleaseAdapter?.clear()
                mSearchTalentReleaseAdapter?.setFooterVisible(false)
                mSearchTalentReleaseAdapter?.notifyDataSetChanged()

                mRvHire.setHasMore(false)
                initData()
            })

        LiveDataBus.with(JobActions.REFRESH_TALENT_RELEASE)
            .observe(this, Observer {
                currentPage = 1
                mSearchTalentReleaseAdapter?.clear()
                mSearchTalentReleaseAdapter?.setFooterVisible(false)
                mSearchTalentReleaseAdapter?.notifyDataSetChanged()

                mRvHire.setHasMore(false)
                initData()
            })
    }

    fun subscribeUi() {
        homeVM.searchTalentReleaseData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSearchTalentReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        homeVM.bannerData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showBannerData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        homeVM.announcementData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showAnnouncementData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commonVM.talentTypeData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is HttpResult.Success -> {
                    showTalentTypeData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commonVM.areaTreeData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    getCityPickerDialog(it.value.data)?.show()
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commonVM.cityAreaData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is HttpResult.Success -> {
                    if (!TextUtils.equals(mWorkAreaAdapter?.currentCity,App.get().mCity)) {
                        mWorkAreaAdapter?.checkPosition = -1
                        mWorkAreaAdapter?.currentCity = App.get().mCity
                        cityAreaData = it.value.data
                        mWorkAreaAdapter?.clear()
                        mWorkAreaAdapter?.add(cityAreaData)
                        mWorkAreaAdapter?.notifyDataSetChanged()
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildRedEnvelopeVM.guildRedEnvelopeTipsData.observe(viewLifecycleOwner, Observer {
            when(it) {
                is HttpResult.Success -> {
                    var isShowPopup = it.value.data?.isShowPopup ?: false
                    //只有定位城市是是深圳并且没有提示过才展示公会红包弹窗
                    if (AmapLocationUtil.instance.mLocation != null && isShowPopup) {
                        val locationCity = AmapLocationUtil.instance.mLocation?.city
                        if (TextUtils.equals("深圳市",locationCity)) {
                            val userId = App.get().getUserInfo()?.userId
                            val needShowGuildRedEnvelopeTip = ConfigLocal.needShowGuildRedEnvelopeGuide(userId)
                            //如果没有设置闪工名则不弹出加入工会领红包的弹窗
                            if (TextUtils.isEmpty(App.get().getUserInfo()?.username)) return@Observer

                            if (needShowGuildRedEnvelopeTip) {
                                showGuildRedEnvelopeTipDialog()
                                ConfigLocal.updateShowGuildRedEnvelopeGuide(userId,false)
                            }
                        }
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        guildVM.myGuildInfoData.observe(viewLifecycleOwner, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    val guildId = it.value.data?.guildId
                    val memberType = it.value.data?.memberType ?: 2

                    if (TextUtils.equals(currentGuildId,guildId)) {
                        if (it.value.data?.memberType == 1) {//会长
                            NavigationUtils.goPresidentGuildActivity(activity as AppCompatActivity,it.value.data)
                        } else  if (it.value.data?.memberType == 2) {//成员
                            NavigationUtils.goMyGuildActivity(activity as AppCompatActivity,it.value.data)
                        }
                    } else {
                        NavigationUtils.goGuildDetailActivity(activity!!,currentGuildId,memberType)
                    }
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun sendSearchTalentReleaseRequest (body: SearchTalentReleaseParm?) {
        Loger.e(TAG,"sendSearchTalentReleaseRequest-currentPage = $currentPage")
        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }

        mSearchTalentReleaseParm = body

        if (mSearchTalentReleaseParm == null) {
            mSearchTalentReleaseParm = SearchTalentReleaseParm()
        }
        mSearchTalentReleaseParm?.workCity = App.get().mCity
        mSearchTalentReleaseParm?.pageNum = currentPage

        Loger.e(TAG,"sendSearchTalentReleaseRequest-App.get().mCity = " + App.get().mCity)

        homeVM.searchTalentRelease(mSearchTalentReleaseParm)
    }

    fun sendTalentTypeRequest () {
        commonVM.fetchTalentTypeData()
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendBannerRequest () {
        homeVM.fetchBanner(1)
    }

    fun sendAnnouncementRequest () {
        homeVM.fetchAnnouncement()
    }

    fun sendCityAreaRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        commonVM.fetchAreaTreeData(token,URLEncoder.encode(App.get().mCity,"UTF-8"))
    }

    fun sendGuildRedEnvelopeTipsRequest () {
        Loger.e(TAG,"sendGuildRedEnvelopeTipsRequest()......")
        if (!App.get().hasLogin()) return
        if (TextUtils.isEmpty(App.get().getUserInfo()?.username)) {
            Loger.e(TAG,"sendGuildRedEnvelopeTipsRequest()......username is empty")
            return
        }

        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        guildRedEnvelopeVM.fetchGuildRedEnvelopeTips(token)
    }

    fun sendMyGuildInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }
        mLoadingDialog?.show()
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token
        guildVM.fetchMyGuildInfo(token)
    }

    fun showSearchTalentReleaseData (datas: SearchTalentReleaseReq) {
        mSearchTalentReleaseAdapter?.showData(datas.data,mTvNoData,mRvHire,currentPage)
    }

    fun showTalentTypeData (data: TalentTypeReq) {
        mTalentTypeAdapter?.clear()
        mTalentTypeAdapter?.add(data.data)
        mTalentTypeAdapter?.notifyDataSetChanged()

        showTalentCellData(0)
    }

    fun showTalentCellData (parentPosition: Int) {
        if (mTalentTypeAdapter?.getItem(parentPosition)?.childs == null) {
            mTalentAdapter?.clear()
            mTalentAdapter?.notifyDataSetChanged()
            return
        }
        if (mTalentTypeAdapter?.getItem(parentPosition)?.childs?.size == 0) {
            mTalentAdapter?.clear()
            mTalentAdapter?.notifyDataSetChanged()
            return
        }

        mTalentAdapter?.clear()
        mTalentAdapter?.add(mTalentTypeAdapter?.getItem(parentPosition)?.childs)
        mTalentAdapter?.notifyDataSetChanged()
    }

    fun showBannerData(data: BannerReq) {
        if (data == null) {
            convenientBanner.visibility = View.GONE
            return
        }
        if (data.data == null) {
            convenientBanner.visibility = View.GONE
            return
        }
        if (data.data?.size == 0) {
            convenientBanner.visibility = View.GONE
            return
        }

        convenientBanner.visibility = View.VISIBLE

        var bannerView =
            rootView?.findViewById<ConvenientBanner<BannerInfo>>((R.id.convenientBanner))

        var indicator = ArrayList<Int>()
        var bannerCount = data.data?.size ?: 0
        if (bannerCount > 1) {
            bannerView?.startTurning(5000)
            indicator.add(R.mipmap.ic_page_indicator)
            indicator.add(R.mipmap.ic_page_indicator_focused)
        } else {
            indicator.add(0)
            indicator.add(0)
        }

        bannerView?.setPages(object : CBViewHolderCreator {
            override fun createHolder(itemView: View?) : BannerImageHolderView {
                return BannerImageHolderView(itemView)
            }

            override fun getLayoutId(): Int {
                return R.layout.home_banner_cell
            }
        } ,data.data) //设置指示器的方向
            //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
            ?.setPageIndicator(indicator.toIntArray())
            ?.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
            ?.setOnItemClickListener {
                var jumpLinkType = data.data?.get(it)?.jumpLinkType
                var jumpLinkUrl = data.data?.get(it)?.jumpLinkUrl
                if (TextUtils.equals("APP",jumpLinkType)) {
                    if (TextUtils.equals("coupon",jumpLinkUrl)) {
                        LiveDataBus.send(HomeActions.SHOW_AMY_SPORT_VOUCHER_RECEIVE_DLG)
                    }
                    if (TextUtils.equals("task",jumpLinkUrl)) {
                        NavigationUtils.goTaskSearchActivity(activity as AppCompatActivity)
                    }
                } else if (TextUtils.equals("WEB",jumpLinkType)){
                    if (!TextUtils.isEmpty(data.data?.get(it)?.jumpLinkUrl)) {
                        WebActivity.intentStart(activity!!,"",data.data?.get(it)?.jumpLinkUrl)
                    }
                }
            }.apply {
                bannerView?.setCanLoop(bannerCount > 1)
            }

    }

    fun showAnnouncementData (data: AnnouncementReq) {
        if (data == null) return
        if (data.data == null) return
        if (data.data?.size == 0) return

        mHireNoticeView.startWithList(data.data)
    }

    fun resetFilterAction () {
        mSearchTalentReleaseParm?.sex = null
        mSearchTalentReleaseParm?.minAge = null
        mSearchTalentReleaseParm?.maxAge = null
        mSearchTalentReleaseParm?.minPrice = null
        mSearchTalentReleaseParm?.maxAge = null
        mSearchTalentReleaseParm?.workDistrict = null

        filterView?.mChkMale?.isChecked = false
        filterView?.mChkFemale?.isChecked = false
        filterView?.mEtStartAge?.text?.clear()
        filterView?.mEtEndAge?.text?.clear()
        filterView?.mEtStartUnitPrice?.text?.clear()
        filterView?.mEtEndUnitPrice?.text?.clear()
        mWorkAreaAdapter?.checkPosition = -1
        mWorkAreaAdapter?.notifyDataSetChanged()
    }

    fun filterAction () {
        currentPage = 1
        mSearchTalentReleaseAdapter?.clear()
        mSearchTalentReleaseAdapter?.setFooterVisible(false)
        mSearchTalentReleaseAdapter?.notifyDataSetChanged()
        mRvHire.setHasMore(false)

        if (mSearchTalentReleaseParm == null) {
            mSearchTalentReleaseParm = SearchTalentReleaseParm()
        }
        if (filterView?.mChkMale?.isChecked!!) {
            mSearchTalentReleaseParm?.sex = 1
        } else if (filterView?.mChkFemale?.isChecked!!) {
            mSearchTalentReleaseParm?.sex = 0
        } else {
            mSearchTalentReleaseParm?.sex = null
        }

        var startAge: String = filterView?.mEtStartAge?.text.toString()
        if (!TextUtils.isEmpty(startAge)) {
            mSearchTalentReleaseParm?.minAge = startAge.toInt()
        } else {
            mSearchTalentReleaseParm?.minAge = null
        }

        var endAge: String = filterView?.mEtEndAge?.text.toString()
        if (!TextUtils.isEmpty(endAge)) {
            mSearchTalentReleaseParm?.maxAge = endAge.toInt()
        } else {
            mSearchTalentReleaseParm?.maxAge = null
        }

        var startUnitPrice: String = filterView?.mEtStartUnitPrice?.text.toString()
        if (!TextUtils.isEmpty(startUnitPrice)) {
            mSearchTalentReleaseParm?.minPrice = startUnitPrice.toInt()
        } else {
            mSearchTalentReleaseParm?.minPrice = null
        }

        var endUnitPrice: String = filterView?.mEtEndUnitPrice?.text.toString()
        if (!TextUtils.isEmpty(endUnitPrice)) {
            mSearchTalentReleaseParm?.maxPrice = endUnitPrice.toInt()
        } else {
            mSearchTalentReleaseParm?.maxPrice = null
        }

        var checkPosition = mWorkAreaAdapter?.checkPosition ?: -1
        if (checkPosition >= 0) {
            mSearchTalentReleaseParm?.workDistrict = mWorkAreaAdapter?.getItem(checkPosition)?.name
        } else {
            mSearchTalentReleaseParm?.workDistrict = null
        }

        sendSearchTalentReleaseRequest(mSearchTalentReleaseParm)
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {
        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(activity!!)
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.showAreaPicker = false
            mCityPickerDialog?.mOnCityPickerListener = this
            provinceData = data
        }

        return mCityPickerDialog
    }

    fun showGuildRedEnvelopeTipDialog() {
        if (mGuildRedEnvelopeTipDialog == null) {
            mGuildRedEnvelopeTipDialog = GuildRedEnvelopeTipDialog(activity!!)
            mGuildRedEnvelopeTipDialog?.mOnJoinGuildListener = this
        }
        mGuildRedEnvelopeTipDialog?.show()
    }

    override fun handleBackEvent(): Boolean {
        var isShowing = mHireDropDownMenu?.isShowing() ?: false
        if (isShowing) {
            mHireDropDownMenu?.closeMenu()
            return true
        }
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mClTalentReleaseCell -> {
                TalentDetailActivity.intentStart(activity as AppCompatActivity,
                    mSearchTalentReleaseAdapter?.getItem(position)?.releaseId, TalentDetailAction.NORMAL)
            }
            R.id.mIvTalentType -> {
                mTalentTypeAdapter?.checkPosition = position
                mTalentTypeAdapter?.notifyDataSetChanged()

                showTalentCellData(position)
            }
            R.id.mTvTalentCell -> {
                var talentData = mTalentAdapter?.getItem(id.toInt())
                var talentCellData = talentData?.childs?.get(position)

                if (mSearchTalentReleaseParm == null) {
                    mSearchTalentReleaseParm = SearchTalentReleaseParm()
                }
                if (mTalentAdapter?.checkCellId == talentCellData?.id) {
                    mTalentTypeAdapter?.checkPosition = -1
                    mTalentTypeAdapter?.notifyDataSetChanged()

                    mTalentAdapter?.checkCellId = ""
                    mTalentAdapter?.notifyDataSetChanged()

                    mSearchTalentReleaseParm?.jobCategoryId = null
                } else {
                    mTalentAdapter?.checkCellId = talentCellData?.id
                    mTalentAdapter?.notifyDataSetChanged()
                    mSearchTalentReleaseParm?.jobCategoryId = talentCellData?.id
                }

                currentPage = 1
                mSearchTalentReleaseAdapter?.clear()
                mSearchTalentReleaseAdapter?.setFooterVisible(false)
                mSearchTalentReleaseAdapter?.notifyDataSetChanged()
                mRvHire.setHasMore(false)

                sendSearchTalentReleaseRequest(mSearchTalentReleaseParm)

                mHireDropDownMenu.closeMenu()
            }
            R.id.mClComplexCell -> {
                currentPage = 1
                mSearchTalentReleaseAdapter?.clear()
                mSearchTalentReleaseAdapter?.setFooterVisible(false)
                mSearchTalentReleaseAdapter?.notifyDataSetChanged()
                mRvHire.setHasMore(false)

                if (mSearchTalentReleaseParm == null) {
                    mSearchTalentReleaseParm = SearchTalentReleaseParm()
                }
                mSearchTalentReleaseParm?.sortType = getSortTypeByPosition(position)
                sendSearchTalentReleaseRequest(mSearchTalentReleaseParm)

                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()
                mHireDropDownMenu.closeMenu()
            }
            R.id.mTvWorkArea -> {
                if (mWorkAreaAdapter?.checkPosition == position) {
                    mWorkAreaAdapter?.checkPosition = -1
                } else {
                    mWorkAreaAdapter?.checkPosition = position
                }
                mWorkAreaAdapter?.notifyDataSetChanged()
            }
            R.id.mTvTalentGuild -> {
                currentGuildId = mSearchTalentReleaseAdapter?.getItem(position)?.guildId
                sendMyGuildInfoRequest()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mTvLocationCity -> {
                if (mHireDropDownMenu.isShowing()) {
                    mHireDropDownMenu.closeMenu()
                }
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mTvReset -> {
                resetFilterAction()
            }
            R.id.mTvConfirm -> {
                filterAction()
                mHireDropDownMenu.closeMenu()
            }
            R.id.mTvSearch -> {
                TalentSearchActivity.intentStart(activity as AppCompatActivity)
            }
        }
    }

    override fun onRefresh() {
        initFilterMenu()
        currentPage = 1
        mSearchTalentReleaseAdapter?.clear()
        mSearchTalentReleaseAdapter?.setFooterVisible(false)
        mSearchTalentReleaseAdapter?.notifyDataSetChanged()

        mRvHire.setHasMore(false)

        var body = SearchTalentReleaseParm()
        body.workCity = App.get().mCity

        sendBannerRequest()
        sendAnnouncementRequest()
        sendSearchTalentReleaseRequest(body)
        sendGuildRedEnvelopeTipsRequest()
    }


    override fun OnLoadMore() {
        currentPage++
        sendSearchTalentReleaseRequest(mSearchTalentReleaseParm)
    }

    fun initFilterMenu () {
        mTalentTypeAdapter?.checkPosition = -1
        mTalentTypeAdapter?.notifyDataSetChanged()

        mTalentAdapter?.checkCellId = ""

        mTalentAdapter?.clear()
        mTalentAdapter?.add(mTalentTypeAdapter?.getItem(0)?.childs)
        mTalentAdapter?.notifyDataSetChanged()

        mComplexAdapter?.checkPosition = -1
        mComplexAdapter?.notifyDataSetChanged()

        resetFilterAction()
    }

    fun getSortTypeByPosition (position: Int): String {
        var sortType = ""
        when (position) {
            0 -> {//信用降序
                sortType = "creditDesc"
            }
            1 -> {//年龄降序
                sortType = "ageDesc"
            }
            2 -> {//年龄升序
                sortType = "ageAsc"
            }
            3 -> {//单价降序
                sortType = "priceDesc"
            }
            4 -> {//单价升序
                sortType = "priceAsc"
            }
        }
        return sortType
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return

        App.get().isSelectCity = true

        App.get().mProvince = province?.name ?: App.get().defaultProvince
        if (!TextUtils.equals(city?.name,App.get().mCity)) {
            LiveDataBus.send(HireActions.HIRE_CITY_CHANGED)
        }
        App.get().setCity(city?.name)
        currentPage = 1
        mTvLocationCity.text = city?.name
        var body = SearchTalentReleaseParm()
        body.workCity = city?.name

        sendSearchTalentReleaseRequest(body)
    }

    override fun OnDropDownMenuClick(position: Int) {
        when (position) {
            0 -> {

            }
            1 -> {

            }
            2 -> {
                sendCityAreaRequest()
            }
        }
    }

    override fun onItemClick(position: Int, textView: TextView?) {
    }

    override fun onPause() {
        super.onPause()
        if (mHireDropDownMenu.isShowing()) {
            mHireDropDownMenu.closeMenu()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkMale -> {
                if (isChecked) {
                    filterView?.mChkFemale?.isChecked = false
                }
            }
            R.id.mChkFemale -> {
                if (isChecked) {
                    filterView?.mChkMale?.isChecked = false
                }
            }
        }
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

    override fun onPermissionGranted(permissionName: Array<out String>) {
        super.onPermissionGranted(permissionName)
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (!App.get().hasLocation()) {
                AmapLocationUtil.instance.getLocation(activity!!,this)
            }
        } else {
            initTalentData()
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        App.get().isLocationPermissionPermanentlyDenied = true
        initTalentData()
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        super.onPermissionDeclined(permissionName)
        App.get().isLocationPermissionDeclined = true
        initTalentData()
    }

    override fun onPermissionNeedExplanation(p0: String) {
        super.onPermissionNeedExplanation(p0)
        initTalentData()
    }

    override fun OnLocation(location: AMapLocation?, errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......location = ${location?.city}")

        if (errorCode == AmapLocationUtil.GPS_NOT_OPEN) {
            LiveDataBus.send(HomeActions.GPS_NOT_OPEN)
            return
        }

        if (errorCode == AmapLocationUtil.NO_LOCATION_PERMISSION) {
            if (!App.get().isLocationPermissionDeclined && !App.get().isLocationPermissionPermanentlyDenied) {
                requestPermission(MULTI_PERMISSIONS)
            }
        }

        sendGuildRedEnvelopeTipsRequest()

        if (!TextUtils.equals(App.get().mCity,location?.city)) {
            showChangeCityView(location?.city)
        }
    }

    override fun OnJoinGuild() {
        NavigationUtils.goJoinGuildActivity(activity as AppCompatActivity)
    }

    fun showChangeCityView (locationCity: String?) {
        if (TextUtils.isEmpty(locationCity)) return
        if (!isVisibleToUser) return
        if (App.get().isSelectCity) return//手动切换城市不弹出城市切换
        if (App.get().hasShowChangeCity) return
        App.get().hasShowChangeCity = true

        LiveDataBus.send(HomeActions.LOCATION_SUCCESSED,locationCity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AmapLocationUtil.instance.removeLocationListener(this)
    }


}