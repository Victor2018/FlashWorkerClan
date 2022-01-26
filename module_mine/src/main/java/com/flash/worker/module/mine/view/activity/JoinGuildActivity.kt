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
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnCityPickerListener
import com.flash.worker.lib.common.interfaces.OnLocationListener
import com.flash.worker.lib.common.util.AmapLocationUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.dialog.CityPickerDialog
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.*
import com.flash.worker.lib.coremodel.data.parm.AreaTreeParm
import com.flash.worker.lib.coremodel.data.parm.SearchGuildParm
import com.flash.worker.lib.coremodel.data.req.SearchGuildReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.lib.coremodel.viewmodel.GuildRedEnvelopeVM
import com.flash.worker.lib.coremodel.viewmodel.GuildVM
import com.flash.worker.lib.livedatabus.action.MineActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.SearchGuildAdapter
import com.flash.worker.module.mine.view.dialog.RewardReceiveDialog
import com.flash.worker.module.mine.view.interfaces.OnRewardReceiveListener
import kotlinx.android.synthetic.main.activity_join_guild.*

@Route(path = ARouterPath.JoinGuildAct)
class JoinGuildActivity : BaseActivity(), View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.OnLoadMoreListener,TextWatcher,
        TextView.OnEditorActionListener, OnCityPickerListener, OnRewardReceiveListener,
        OnLocationListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, JoinGuildActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val guildVM: GuildVM by viewModels {
        InjectorUtils.provideGuildVMFactory(this)
    }

    private val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    private val guildRedEnvelopeVM: GuildRedEnvelopeVM by viewModels {
        InjectorUtils.provideGuildRedEnvelopeVMFactory(this)
    }

    var mLoadingDialog: LoadingDialog? = null
    var mSearchGuildAdapter: SearchGuildAdapter? = null

    var mCityPickerDialog: CityPickerDialog? = null
    var provinceData: List<ProvinceInfo>? = null

    var currentPage: Int = 1

    override fun getLayoutResource() = R.layout.activity_join_guild

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)

        mSearchGuildAdapter = SearchGuildAdapter(this,this)
        mRvGuild.adapter = mSearchGuildAdapter

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

    fun initData () {
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

        LiveDataBus.with(MineActions.BACK_JOIN_GUILD_ACT)
                .observe(this, Observer {
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

        guildRedEnvelopeVM.redEnvelopeInfoData.observe(this, Observer {
            mLoadingDialog?.dismiss()
            when(it) {
                is HttpResult.Success -> {
                    showRewardReceiveDlg(it.value.data)
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

        var keyword = mEtSearch.text.toString()

        var body = SearchGuildParm()
        body.pageNum = currentPage
        body.guildCity = App.get().mGuildCity
        body.keywords = keyword

        guildVM.searchGuild(token,body)
    }

    fun sendAreaTreeRequest () {
        mLoadingDialog?.show()

        var body = AreaTreeParm()
        body.level = 3
        commonVM.fetchAreaTreeData(body)
    }

    fun sendReceiveRedEnvelopeInfoRequest () {
        if (!App.get().hasLogin()) {
            ToastUtils.show("请先登录")
            return
        }

        mLoadingDialog?.show()
        val token = App.get().getLoginReq()?.data?.token

        guildRedEnvelopeVM.receiveRedEnvelope(token)
    }

    fun showSearchGuildData (data: SearchGuildReq) {
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

    fun showRewardReceiveDlg (data: GuildRedEnvelopeInfoData?) {
        if (data?.status == 1) {
            var mRewardReceiveDialog = RewardReceiveDialog(this)
            mRewardReceiveDialog.mOnRewardReceiveListener = this
            mRewardReceiveDialog.mGuildRedEnvelopeInfoData = data

            mRewardReceiveDialog.show()
        }
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
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var guildId = mSearchGuildAdapter?.getItem(position)?.guildId
        GuildDetailActivity.intentStart(this,guildId,2)
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

    override fun OnRewardReceive() {
        sendReceiveRedEnvelopeInfoRequest()
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

}