package com.flash.worker.module.task.view.activity

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
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.data.TaskDetailAction
import com.flash.worker.lib.common.interfaces.OnDropDownMenuClickListener
import com.flash.worker.lib.common.util.KeyBoardUtil
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.BaseRecycleAdapter
import com.flash.worker.lib.common.view.adapter.ComplexAdapter
import com.flash.worker.lib.common.view.adapter.anim.AlphaAnimatorAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.LMRecyclerView
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.SearchTaskInfo
import com.flash.worker.lib.coremodel.data.parm.SearchTaskParm
import com.flash.worker.lib.coremodel.data.req.SearchTaskReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.util.WebConfig
import com.flash.worker.lib.coremodel.viewmodel.HomeVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.action.TaskActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import com.flash.worker.module.task.view.adapter.SearchTaskAdapter
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.act_task_search_content.*
import kotlinx.android.synthetic.main.activity_task_search.*
import kotlinx.android.synthetic.main.task_search_menu.view.*

@Route(path = ARouterPath.TaskSearchAct)
class TaskSearchActivity : BaseActivity(), OnDropDownMenuClickListener, TextWatcher,View.OnClickListener,
    AdapterView.OnItemClickListener, LMRecyclerView.OnLoadMoreListener,TextView.OnEditorActionListener,
    SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity) {
            var intent = Intent(activity, TaskSearchActivity::class.java)
            activity.startActivity(intent)
        }
    }

    var mSearchTaskAdapter: SearchTaskAdapter? = null

    var mSearchTaskParm: SearchTaskParm? = null
    var currentPage = 1
    var currentPosition: Int = -1
    var mSortType: String? = null
    var mSortOrder: String? = null

    private val homeVM: HomeVM by viewModels {
        InjectorUtils.provideHomeVMFactory(this)
    }

    var headers: Array<String>? = null
    var popupViews: MutableList<View> = ArrayList()
    var taskSearchView: View? = null
    var taskSearchContentView: View? = null
    var mComplexAdapter: ComplexAdapter? = null

    override fun getLayoutResource() = R.layout.activity_task_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        initData()
    }

    fun initialize () {
        subscribeUi()
        subscribeEvent()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mTvCancel.setOnClickListener(this)
        mIvClear.setOnClickListener(this)

        mEtSearch.setOnEditorActionListener(this)
        mEtSearch.addTextChangedListener(this)
        appbar.addOnOffsetChangedListener(this)

        headers = ResUtils.getStringArrayRes(R.array.task_search_menu)

        taskSearchView = layoutInflater.inflate(R.layout.task_search_menu, null)

        mComplexAdapter = ComplexAdapter(this,this)
        var complexData = ResUtils.getStringArrayRes(R.array.task_search_titles)
        mComplexAdapter?.add(complexData?.toList())
        taskSearchView?.mRvTaskSearchFilter?.adapter = mComplexAdapter

        popupViews.clear()
        popupViews.add(taskSearchView!!)
        taskSearchContentView = layoutInflater.inflate(R.layout.act_task_search_content, null)
        //init dropdownview
        mTaskSearchDropDownMenu.tabMenuView?.removeAllViews()
        mTaskSearchDropDownMenu.popupMenuViews?.removeAllViews()
        mTaskSearchDropDownMenu.isFillWidth = false
        mTaskSearchDropDownMenu.setDropDownMenu(headers?.toList()!!, popupViews, taskSearchContentView)
        mTaskSearchDropDownMenu.mOnDropDownMenuClickListener = this

        mSearchTaskAdapter = SearchTaskAdapter(this,this)

        val animatorAdapter = AlphaAnimatorAdapter(mSearchTaskAdapter,mRvSearch)

        mRvSearch?.adapter = animatorAdapter
        mRvSearch.setLoadMoreListener(this)

    }

    fun initData () {
        sendSearchTaskRequest("")
    }

    fun subscribeUi() {
        homeVM.searchTaskData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showSearchJobReleaseData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message)
                }
            }
        })
    }

    fun subscribeEvent () {
        LiveDataBus.with(TaskActions.REFRESH_TASK_LIST)
            .observe(this, Observer {
                initData()
            })
    }
    fun sendSearchTaskRequest (keyword: String?) {
        Loger.e(TAG,"sendSearchTaskRequest....")
        if (mSearchTaskParm == null) {
            mSearchTaskParm = SearchTaskParm()
            currentPage = 1
        }
        var query: String? = mEtSearch.text.toString()

        if (!TextUtils.isEmpty(keyword)) {
            query = keyword
        }

        mSearchTaskParm?.keywords = query
        mSearchTaskParm?.pageNum = currentPage
        mSearchTaskParm?.sortType = mSortType
        mSearchTaskParm?.sortOrder = mSortOrder

        if (currentPage == 1) {
            mSrlRefresh.isRefreshing = true
        }
        var token = App.get().getLoginReq()?.data?.token
        homeVM.searchTask(token,mSearchTaskParm)

    }

    fun showSearchJobReleaseData (datas: SearchTaskReq) {
        mSearchTaskAdapter?.showData(datas.data,mTvNoData,mRvSearch,currentPage)
    }

    fun showSearchNormalUI () {
        mTvNoData.visibility = View.GONE

        mSearchTaskAdapter?.clear()
        mSearchTaskAdapter?.notifyDataSetChanged()

        mSearchTaskAdapter?.setFooterVisible(false)
        mRvSearch.setHasMore(false)

        mEtSearch.setText("")
        KeyBoardUtil.hideKeyBoard(this,mEtSearch)
    }

    override fun onRefresh() {
        mComplexAdapter?.checkPosition = -1
        mComplexAdapter?.notifyDataSetChanged()
        currentPage = 1
        mSortType = null
        mSortOrder = null
        showSearchNormalUI()
        sendSearchTaskRequest("")
    }

    override fun OnLoadMore() {
        currentPage++
        sendSearchTaskRequest("")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                onBackPressed()
            }
            R.id.mIvClear -> {
                currentPage = 1
                mSortType = null
                mSortOrder = null
                showSearchNormalUI()
                sendSearchTaskRequest("")
            }
        }
    }

    override fun OnDropDownMenuClick(position: Int) {
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentPosition = position
        when (view?.id) {
            R.id.mClComplexCell -> {
                mComplexAdapter?.checkPosition = position
                mComplexAdapter?.notifyDataSetChanged()

                mTaskSearchDropDownMenu.closeMenu()

                getSortTypeByPosition(position)
                currentPage = 1
                mSearchTaskAdapter?.clear()
                mSearchTaskAdapter?.notifyDataSetChanged()

                sendSearchTaskRequest("")
            }
            else -> {
                var releaseId = mSearchTaskAdapter?.getItem(position)?.releaseId
                TaskDetailActivity.intentStart(this,releaseId,null,
                    null,TaskDetailAction.NORMAL)
            }
        }
    }

    fun getSortTypeByPosition (position: Int) {
        when (position) {
            0 -> {//信用降序
                mSortType = "credit"
                mSortOrder = "desc"
            }
            1 -> {//单价降序
                mSortType = "price"
                mSortOrder = "desc"
            }
            2 -> {//单价升序
                mSortType = "price"
                mSortOrder = "asc"
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mSearchTaskParm = null
            sendSearchTaskRequest("")
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

    override fun onBackPressed() {
        if (mTaskSearchDropDownMenu.isShowing()) {
            mTaskSearchDropDownMenu.closeMenu()
            return
        }
        super.onBackPressed()
    }

}