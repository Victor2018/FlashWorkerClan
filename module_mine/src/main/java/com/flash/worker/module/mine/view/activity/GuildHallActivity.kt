package com.flash.worker.module.mine.view.activity

import android.Manifest
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
import com.amap.api.location.AMapLocation
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.module.LocationHelper
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.AreaInfo
import com.flash.worker.lib.coremodel.data.bean.CityInfo
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.ProvinceInfo
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SearchGuildParm
import com.flash.worker.lib.coremodel.data.req.SearchGuildReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.SearchGuildAdapter
import kotlinx.android.synthetic.main.activity_guild_hall.*


class GuildHallActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener,TextWatcher,
        TextView.OnEditorActionListener, OnCityPickerListener, OnLocationListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,memberType: Int) {
            var intent = Intent(activity, GuildHallActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,memberType)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mSearchGuildAdapter: SearchGuildAdapter? = null

    var mCityPickerDialog: CityPickerDialog? = null
    var provinceData: List<ProvinceInfo>? = null

    var currentPage: Int = 1

    var memberType: Int = 2//1,会长；2，成员
    var keywords: String? = null

    override fun getLayoutResource() = R.layout.activity_guild_hall

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mSearchGuildAdapter = SearchGuildAdapter(this,this)

        val animatorAdapter
                = AlphaAnimatorAdapter(mSearchGuildAdapter,mRvGuild)

        mRvGuild.adapter = animatorAdapter

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mIvBack.setOnClickListener(this)
        mTvCity.setOnClickListener(this)
        mBtnEstablishGuild.setOnClickListener(this)
        mIvClear.setOnClickListener(this)

        mRvGuild.setLoadMoreListener(this)

        mEtSearch.addTextChangedListener(this)
        mEtSearch.setOnEditorActionListener(this)
    }

    fun initData (intent: Intent?) {
        memberType = intent?.getIntExtra(Constant.INTENT_DATA_KEY,2) ?: 2

        if (memberType == 1) {//会长
            mBtnEstablishGuild.visibility = View.GONE
        } else if (memberType == 2) {//成员
            mBtnEstablishGuild.visibility = View.VISIBLE
        }

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(MULTI_PERMISSIONS)
        } else {
            AmapLocationUtil.instance.getLocation(this,this)
        }
    }

    fun initGuildData () {
        mTvCity.text = App.get().mGuildCity
        sendSearchGuildRequest()
    }

    fun subscribeEvent() {
        LiveDataBus.with(MineActions.BACK_GUILD_HALL_ACT)
                .observe(this, Observer {
                    LiveDataBus.send(MineActions.VIEW_HIRE)
                    finish()
                })

    }

    fun subscribeUi () {
        guildVM.searchGuildData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSearchGuildData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })

        commonVM.areaTreeData.observe(this, Observer {
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
    }

    fun sendSearchGuildRequest () {
        val loginReq = App.get().getLoginReq()
        val token = loginReq?.data?.token

        var body = SearchGuildParm()
        body.pageNum = currentPage
        body.guildCity = App.get().mGuildCity
        body.keywords = keywords

        guildVM.searchGuild(token,body)
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun showSearchGuildData (data: SearchGuildReq) {
        KeyBoardUtil.hideKeyBoard(this,mEtSearch)
        mSearchGuildAdapter?.showData(data.data,mTvNoData,mRvGuild,currentPage)
    }

    fun getCityPickerDialog(data: List<ProvinceInfo>?): CityPickerDialog? {
        if (mCityPickerDialog == null) {
            mCityPickerDialog = CityPickerDialog(this)
            mCityPickerDialog?.provinceDatas = data
            mCityPickerDialog?.showAreaPicker = false
            mCityPickerDialog?.mOnCityPickerListener = this
            provinceData = data
        }

        return mCityPickerDialog
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
            R.id.mTvCity -> {
                if (provinceData == null || provinceData?.size == 0) {
                    sendAreaTreeRequest()
                    return
                }
                getCityPickerDialog(provinceData)?.show()
            }
            R.id.mBtnEstablishGuild -> {
                ApplyEstablishGuildActivity.intentStart(this)
            }
            R.id.mIvClear -> {
                mEtSearch.setText("")
                keywords = null
                sendSearchGuildRequest()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var guildId = mSearchGuildAdapter?.getItem(position)?.guildId
        GuildDetailActivity.intentStart(this,guildId,memberType)
    }

    override fun onRefresh() {
        currentPage = 1
        mSearchGuildAdapter?.clear()
        mSearchGuildAdapter?.setFooterVisible(false)
        mSearchGuildAdapter?.notifyDataSetChanged()

        mRvGuild.setHasMore(false)

        sendSearchGuildRequest()
    }

    override fun OnLoadMore() {
        currentPage++
        sendSearchGuildRequest()
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

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            currentPage = 1
            keywords = mEtSearch.text.toString()
            sendSearchGuildRequest()
            return true
        }
        return false
    }

    override fun OnCityPicker(province: ProvinceInfo?, city: CityInfo?, area: AreaInfo?) {
        if (province == null && city == null && area == null) return

        App.get().isSelectGuildCity = true

        App.get().mProvince = province?.name ?: App.get().defaultProvince
        App.get().setGuildCity(city?.name)
        mTvCity.text = city?.name
        currentPage = 1
        sendSearchGuildRequest()
    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
        super.onPermissionGranted(permissionName)
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AmapLocationUtil.instance.getLocation(this,this)
        } else {
            initGuildData()
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        initGuildData()
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        super.onPermissionDeclined(permissionName)
        initGuildData()
    }

    override fun onPermissionNeedExplanation(p0: String) {
        super.onPermissionNeedExplanation(p0)
        initGuildData()
    }

    override fun OnLocation(location: AMapLocation?,errorCode: Int, error: String?) {
        Loger.e(TAG,"OnLocation()......${location?.city}")
        if (errorCode == AmapLocationUtil.NO_LOCATION_PERMISSION) return
        //手动选择不更新app中的公会城市
        if (!App.get().isSelectGuildCity) {
            App.get().setGuildCity(location?.city)
        }

        initGuildData()
    }

    override fun onDestroy() {
        super.onDestroy()
        AmapLocationUtil.instance.removeLocationListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

}