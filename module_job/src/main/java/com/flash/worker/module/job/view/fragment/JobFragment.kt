package com.flash.worker.module.job.view.fragment

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
import com.flash.worker.lib.common.data.JobDetailAction
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.provider.LoginService
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.WebActivity
import com.flash.worker.lib.common.view.adapter.*
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.holder.BannerImageHolderView
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SearchEmployerReleaseParm
import com.flash.worker.lib.coremodel.data.req.AnnouncementReq
import com.flash.worker.lib.coremodel.data.req.BannerReq
import com.flash.worker.lib.coremodel.data.req.SearchEmployerReleaseReq
import com.flash.worker.lib.coremodel.data.req.TalentTypeReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.*
import com.flash.worker.lib.coremodel.viewmodel.factory.HomeVMFactory
import com.flash.worker.lib.livedatabus.action.HireActions
import com.flash.worker.lib.livedatabus.action.HomeActions
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.job.R
import com.flash.worker.module.job.view.activity.JobDetailActivity
import com.flash.worker.module.job.view.adapter.SearchJobReleaseAdapter
import com.sunfusheng.marqueeview.MarqueeView
import com.flash.worker.module.job.view.activity.JobSearchActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_job.*
import kotlinx.android.synthetic.main.fragment_job_header.*
import kotlinx.android.synthetic.main.fragment_job_content.*
import kotlinx.android.synthetic.main.job_complex_menu.view.*
import kotlinx.android.synthetic.main.job_filter_menu.*
import kotlinx.android.synthetic.main.job_talent_type_menu.view.*
import kotlinx.android.synthetic.main.job_filter_menu.view.*
import java.net.URLEncoder


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: JobFragment
 * Author: Victor
 * Date: 2020/11/27 16:09
 * Description: 
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.JobFgt)
class JobFragment: BaseFragment(),  SwipeRefreshLayout.OnRefreshListener,View.OnClickListener,
    AdapterView.OnItemClickListener, LMRecyclerView.OnLoadMoreListener, OnCityPickerListener,
    OnDropDownMenuClickListener, MarqueeView.OnItemClickListener, CompoundButton.OnCheckedChangeListener,
    AppBarLayout.OnOffsetChangedListener, OnLocationListener {

    private lateinit var homeVM: HomeVM
    private lateinit var commonVM: CommonVM

    @Autowired(name = ARouterPath.loginService)
    @JvmField
    var loginService: LoginService? = null

    var mLoadingDialog: LoadingDialog? = null
    var mSearchJobReleaseAdapter: SearchJobReleaseAdapter? = null
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

    var mSearchEmployerReleaseParm: SearchEmployerReleaseParm? = null

    var cityAreaData: List<AreaInfo>? = null

    companion object {

        fun newInstance(): JobFragment {
            return newInstance(0)
        }
        fun newInstance(id: Int): JobFragment {
            val fragment = JobFragment()
            val bundle = Bundle()
            bundle.putInt(ID_KEY, id)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_job
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialize()
        initData()
    }

    override fun onResume() {
        super.onResume()
        if (!App.get().isLocationPermissionDeclined && !App.get().isLocationPermissionPermanentlyDenied) {
            AmapLocationUtil.instance.getLocation(activity!!,this)
        }
    }

    fun initialize () {
        homeVM = ViewModelProvider(
            this, HomeVMFactory(this))
            .get(HomeVM::class.java)

        commonVM = ViewModelProvider(
            this, InjectorUtils.provideCommonVMFactory(this))
            .get(CommonVM::class.java)

        mLoadingDialog = LoadingDialog(activity!!)

        subscribeUi()
        subscribeEvent()

        headers = ResUtils.getStringArrayRes(R.array.hire_job_menu_titles)

        talentTypeView = layoutInflater.inflate(R.layout.job_talent_type_menu, null)
        complexView = layoutInflater.inflate(R.layout.job_complex_menu, null)
        filterView = layoutInflater.inflate(R.layout.job_filter_menu, null)
        filterView?.mTvReset?.setOnClickListener(this)
        filterView?.mTvConfirm?.setOnClickListener(this)
        filterView?.mChkEnterprise?.setOnCheckedChangeListener(this)
        filterView?.mChkPersonal?.setOnCheckedChangeListener(this)
        filterView?.mChkMerchant?.setOnCheckedChangeListener(this)
        filterView?.mChkDayKSettlement?.setOnCheckedChangeListener(this)
        filterView?.mChkWeekSettlement?.setOnCheckedChangeListener(this)
        filterView?.mChkPieceSettlement?.setOnCheckedChangeListener(this)

        mTalentTypeAdapter = TalentTypeAdapter(activity!!,this)
        talentTypeView?.mRvTalentType?.adapter = mTalentTypeAdapter

        mTalentAdapter = TalentAdapter(activity!!,this)
        talentTypeView?.mRvTalent?.adapter = mTalentAdapter

        mComplexAdapter = ComplexAdapter(activity!!,this)
        complexView?.mRvComplex?.adapter = mComplexAdapter

        var complexData = ResUtils.getStringArrayRes(R.array.job_complex_titles)
        mComplexAdapter?.clear()
        mComplexAdapter?.add(complexData?.toList())
        mComplexAdapter?.notifyDataSetChanged()

        mWorkAreaAdapter = WorkAreaAdapter(activity!!,this)
        filterView?.mRvWorkArea?.adapter = mWorkAreaAdapter

        popupViews.clear()
        popupViews.add(talentTypeView!!)
        popupViews.add(complexView!!)
        popupViews.add(filterView!!)

        contentView = layoutInflater.inflate(R.layout.fragment_job_content, null)
        //init dropdownview
        mJobDropDownMenu.tabMenuView?.removeAllViews()
        mJobDropDownMenu.popupMenuViews?.removeAllViews()
        mJobDropDownMenu.setDropDownMenu(headers?.toList()!!, popupViews, contentView)
        mJobDropDownMenu.mOnDropDownMenuClickListener = this

        mSearchJobReleaseAdapter = SearchJobReleaseAdapter(activity!!,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSearchJobReleaseAdapter,mRvJob)
        mRvJob.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mJobNoticeView.setOnItemClickListener(this)
        mRvJob.setLoadMoreListener(this)

        mTvLocationCity.setOnClickListener(this)
        mTvSearch.setOnClickListener(this)

        appbar.addOnOffsetChangedListener(this)
    }

    fun initData() {
        provinceData = App.get().getCityData()

        sendTalentTypeRequest()
        sendBannerRequest()
        sendAnnouncementRequest()

        initJobData()
    }

    fun initJobData () {
        currentPage = 1
        mTvLocationCity.text = App.get().mCity
        sendSearchEmployerReleaseRequest(mSearchEmployerReleaseParm)
        sendCityAreaRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(HireActions.HIRE_CITY_CHANGED)
            .observeForever(this, Observer {
                currentPage = 1
                mSearchJobReleaseAdapter?.clear()
                mSearchJobReleaseAdapter?.setFooterVisible(false)
                mSearchJobReleaseAdapter?.notifyDataSetChanged()

                mRvJob.setHasMore(false)
                initData()
            })

        LiveDataBus.with(HireActions.REFRESH_HIRE_RELEASE)
            .observe(this, Observer {
                currentPage = 1
                mSearchJobReleaseAdapter?.clear()
                mSearchJobReleaseAdapter?.setFooterVisible(false)
                mSearchJobReleaseAdapter?.notifyDataSetChanged()

                mRvJob.setHasMore(false)
                initData()
            })
    }

    fun subscribeUi() {
        homeVM.searchEmployerReleaseData.observe(viewLifecycleOwner, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSearchEmployerReleaseData(it.value)
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
    }

    fun sendSearchEmployerReleaseRequest (body: SearchEmployerReleaseParm?) {
        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }

        mSearchEmployerReleaseParm = body

        if (mSearchEmployerReleaseParm == null) {
            mSearchEmployerReleaseParm = SearchEmployerReleaseParm()
        }
        mSearchEmployerReleaseParm?.workCity = App.get().mCity
        mSearchEmployerReleaseParm?.pageNum = currentPage

        homeVM.searchEmployerRelease(mSearchEmployerReleaseParm)

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

    fun sendCityAreaRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        commonVM.fetchAreaTreeData(token, URLEncoder.encode(App.get().mCity,"UTF-8"))
    }

    fun sendBannerRequest () {
        homeVM.fetchBanner(2)
    }

    fun sendAnnouncementRequest () {
        homeVM.fetchAnnouncement()
    }

    fun showSearchEmployerReleaseData (datas: SearchEmployerReleaseReq) {
        mSearchJobReleaseAdapter?.showData(datas.data,mTvNoData,mRvJob,currentPage)
    }

    fun showTalentTypeData (data: TalentTypeReq) {
        Loger.e(TAG,"showTalentTypeData- data = " + JsonUtils.toJSONString(data))
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

        mJobNoticeView.startWithList(data.data)
    }

    fun resetFilterAction () {
        mSearchEmployerReleaseParm?.type = null
        mSearchEmployerReleaseParm?.identityRequirement = null
        mSearchEmployerReleaseParm?.isAtHome = null
        mSearchEmployerReleaseParm?.identity = null
        mSearchEmployerReleaseParm?.minPrice = null
        mSearchEmployerReleaseParm?.maxPrice = null
        mSearchEmployerReleaseParm?.settlementMethod = null
        mSearchEmployerReleaseParm?.workDistrict = null

        filterView?.mChkUrgent?.isChecked = false
        filterView?.mChkStudent?.isChecked = false
        filterView?.mChkDoAtHome?.isChecked = false

        filterView?.mChkEnterprise?.isChecked = false
        filterView?.mChkPersonal?.isChecked = false
        filterView?.mChkMerchant?.isChecked = false

        filterView?.mChkDayKSettlement?.isChecked = false
        filterView?.mChkWeekSettlement?.isChecked = false
        filterView?.mChkPieceSettlement?.isChecked = false

        filterView?.mEtStartUnitPrice?.text?.clear()
        filterView?.mEtEndUnitPrice?.text?.clear()
        mWorkAreaAdapter?.checkPosition = -1
        mWorkAreaAdapter?.notifyDataSetChanged()
        //重置公会筛选条件
    }

    fun filterAction () {
        currentPage = 1
        mSearchJobReleaseAdapter?.clear()
        mSearchJobReleaseAdapter?.setFooterVisible(false)
        mSearchJobReleaseAdapter?.notifyDataSetChanged()
        mRvJob.setHasMore(false)

        if (mSearchEmployerReleaseParm == null) {
            mSearchEmployerReleaseParm = SearchEmployerReleaseParm()
        }

        if (mChkUrgent.isChecked) {
            mSearchEmployerReleaseParm?.type = 2
        } else {
            mSearchEmployerReleaseParm?.type = null
        }

        if (mChkStudent.isChecked) {
            mSearchEmployerReleaseParm?.identityRequirement = 2
        } else {
            mSearchEmployerReleaseParm?.identityRequirement = null
        }

        if (mChkDoAtHome.isChecked) {
            mSearchEmployerReleaseParm?.isAtHome = true
        } else {
            mSearchEmployerReleaseParm?.isAtHome = null
        }

        if (mChkEnterprise.isChecked) {
            mSearchEmployerReleaseParm?.identity = 1
        } else if (mChkPersonal.isChecked) {
            mSearchEmployerReleaseParm?.identity = 3
        } else if (mChkMerchant.isChecked) {
            mSearchEmployerReleaseParm?.identity = 2
        } else {
            mSearchEmployerReleaseParm?.identity = null
        }

        var startUnitPrice: String = filterView?.mEtStartUnitPrice?.text.toString()
        if (!TextUtils.isEmpty(startUnitPrice)) {
            mSearchEmployerReleaseParm?.minPrice = startUnitPrice.toInt()
        } else {
            mSearchEmployerReleaseParm?.minPrice = null
        }

        var endUnitPrice: String = filterView?.mEtEndUnitPrice?.text.toString()
        if (!TextUtils.isEmpty(endUnitPrice)) {
            mSearchEmployerReleaseParm?.maxPrice = endUnitPrice.toInt()
        } else {
            mSearchEmployerReleaseParm?.maxPrice = null
        }

        if (mChkDayKSettlement.isChecked) {
            mSearchEmployerReleaseParm?.settlementMethod = 1
        } else if (mChkWeekSettlement.isChecked) {
            mSearchEmployerReleaseParm?.settlementMethod = 2
        } else if (mChkPieceSettlement.isChecked) {
            mSearchEmployerReleaseParm?.settlementMethod = 3
        } else {
            mSearchEmployerReleaseParm?.settlementMethod = null
        }

        var checkPosition = mWorkAreaAdapter?.checkPosition ?: -1
        if (checkPosition >= 0) {
            mSearchEmployerReleaseParm?.workDistrict = mWorkAreaAdapter?.getItem(checkPosition)?.name
        } else {
            mSearchEmployerReleaseParm?.workDistrict = null
        }

        sendSearchEmployerReleaseRequest(mSearchEmployerReleaseParm)
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

    override fun handleBackEvent(): Boolean {
        var isShowing = mJobDropDownMenu?.isShowing() ?: false
        if (isShowing) {
            mJobDropDownMenu?.closeMenu()
            return true
        }
        return false
    }

    override fun freshFragData() {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mClJobReleaseCell -> {
                JobDetailActivity.intentStart(activity as AppCompatActivity,
                    mSearchJobReleaseAdapter?.getItem(position)?.releaseId,
                    null,null, JobDetailAction.NORMAL)
            }
            R.id.mIvTalentType -> {
                mTalentTypeAdapter?.checkPosition = position
                mTalentTypeAdapter?.notifyDataSetChanged()

                showTalentCellData(position)
            }
            R.id.mTvTalentCell -> {
                var talentData = mTalentAdapter?.getItem(id.toInt())
                var talentCellData = talentData?.childs?.get(position)

                if (mSearchEmployerReleaseParm == null) {
                    mSearchEmployerReleaseParm = SearchEmployerReleaseParm()
                }
                if (mTalentAdapter?.checkCellId == talentCellData?.id) {
                    mTalentTypeAdapter?.checkPosition = -1
                    mTalentTypeAdapter?.notifyDataSetChanged()

                    mTalentAdapter?.checkCellId = ""
                    mTalentAdapter?.notifyDataSetChanged()

                    mSearchEmployerReleaseParm?.jobCategoryId = null

                } else {
                    mTalentAdapter?.checkCellId = talentCellData?.id
                    mTalentAdapter?.notifyDataSetChanged()
                    mSearchEmployerReleaseParm?.jobCategoryId = talentCellData?.id
                }

                currentPage = 1
                mSearchJobReleaseAdapter?.clear()
                mSearchJobReleaseAdapter?.setFooterVisible(false)
                mSearchJobReleaseAdapter?.notifyDataSetChanged()
                mRvJob.setHasMore(false)

                sendSearchEmployerReleaseRequest(mSearchEmployerReleaseParm)

                mJobDropDownMenu.closeMenu()
            }
            R.id.mClComplexCell -> {
                currentPage = 1

                mSearchJobReleaseAdapter?.clear()
                mSearchJobReleaseAdapter?.setFooterVisible(false)
                mSearchJobReleaseAdapter?.notifyDataSetChanged()
                mRvJob.setHasMore(false)

                if (mSearchEmployerReleaseParm == null) {
                    mSearchEmployerReleaseParm = SearchEmployerReleaseParm()
                }
                mSearchEmployerReleaseParm?.sortType = getSortTypeByPosition(position)
                sendSearchEmployerReleaseRequest(mSearchEmployerReleaseParm)

                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()
                mJobDropDownMenu.closeMenu()
            }
            R.id.mTvWorkArea -> {
                if (mWorkAreaAdapter?.checkPosition == position) {
                    mWorkAreaAdapter?.checkPosition = -1
                } else {
                    mWorkAreaAdapter?.checkPosition = position
                }
                mWorkAreaAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mTvLocationCity -> {
                if (mJobDropDownMenu.isShowing()) {
                    mJobDropDownMenu.closeMenu()
                }
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mTvSearch -> {
                JobSearchActivity.intentStart(activity as AppCompatActivity)
            }
            R.id.mTvReset -> {
                resetFilterAction()
            }
            R.id.mTvConfirm -> {
                filterAction()
                mJobDropDownMenu.closeMenu()
            }
        }
    }

    override fun onRefresh() {
        initFilterMenu()
        currentPage = 1
        mSearchJobReleaseAdapter?.clear()
        mSearchJobReleaseAdapter?.setFooterVisible(false)
        mSearchJobReleaseAdapter?.notifyDataSetChanged()

        mRvJob.setHasMore(false)

        var body = SearchEmployerReleaseParm()

        sendBannerRequest()
        sendAnnouncementRequest()
        sendSearchEmployerReleaseRequest(body)
    }

    override fun OnLoadMore() {
        currentPage++
        sendSearchEmployerReleaseRequest(mSearchEmployerReleaseParm)
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
            0 -> {//开工日期(由近到远)
                sortType = "jobStartTimeAsc"
            }
            1 -> {//信用降序
                sortType = "creditDesc"
            }
            2 -> {//单价降序
                sortType = "priceDesc"
            }
            3 -> {//单价升序
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
            LiveDataBus.send(JobActions.JOB_CITY_CHANGED)
        }
        App.get().setCity(city?.name)
        currentPage = 1
        mTvLocationCity.text = city?.name
        var body = SearchEmployerReleaseParm()
        body.workCity = city?.name

        sendSearchEmployerReleaseRequest(body)
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
        if (mJobDropDownMenu.isShowing()) {
            mJobDropDownMenu.closeMenu()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mChkEnterprise -> {
                if (isChecked) {
                    filterView?.mChkPersonal?.isChecked = false
                    filterView?.mChkMerchant?.isChecked = false
                }
            }
            R.id.mChkPersonal -> {
                if (isChecked) {
                    filterView?.mChkEnterprise?.isChecked = false
                    filterView?.mChkMerchant?.isChecked = false
                }
            }
            R.id.mChkMerchant -> {
                if (isChecked) {
                    filterView?.mChkEnterprise?.isChecked = false
                    filterView?.mChkPersonal?.isChecked = false
                }
            }
            R.id.mChkDayKSettlement -> {
                if (isChecked) {
                    filterView?.mChkWeekSettlement?.isChecked = false
                    filterView?.mChkPieceSettlement?.isChecked = false
                }
            }
            R.id.mChkWeekSettlement -> {
                if (isChecked) {
                    filterView?.mChkDayKSettlement?.isChecked = false
                    filterView?.mChkPieceSettlement?.isChecked = false
                }
            }
            R.id.mChkPieceSettlement -> {
                if (isChecked) {
                    filterView?.mChkDayKSettlement?.isChecked = false
                    filterView?.mChkWeekSettlement?.isChecked = false
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
            initJobData()
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        App.get().isLocationPermissionPermanentlyDenied = true
        initJobData()
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        super.onPermissionDeclined(permissionName)
        App.get().isLocationPermissionDeclined = true
        initJobData()
    }

    override fun onPermissionNeedExplanation(p0: String) {
        super.onPermissionNeedExplanation(p0)
        initJobData()
    }

    override fun OnLocation(location: AMapLocation?,errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......${location?.city}")
        if (errorCode == AmapLocationUtil.GPS_NOT_OPEN) {
            LiveDataBus.send(HomeActions.GPS_NOT_OPEN)
            return
        }

        if (errorCode == AmapLocationUtil.NO_LOCATION_PERMISSION) {
            if (!App.get().isLocationPermissionDeclined && !App.get().isLocationPermissionPermanentlyDenied) {
                requestPermission(MULTI_PERMISSIONS)
            }
        }

        if (!TextUtils.equals(App.get().mCity,location?.city)) {
            showChangeCityView(location?.city)
        }
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