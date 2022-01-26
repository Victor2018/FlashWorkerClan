package com.flash.worker.module.business.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ToastUtils
import com.flash.worker.lib.common.view.adapter.TalentAdapter
import com.flash.worker.lib.common.view.adapter.TalentTypeAdapter
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.req.TalentTypeReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.CommonVM
import com.flash.worker.module.business.R
import com.flash.worker.lib.livedatabus.action.BusinessActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import kotlinx.android.synthetic.main.activity_talent_type.*
import kotlinx.android.synthetic.main.activity_talent_type.mIvBack
import kotlinx.android.synthetic.main.activity_talent_type.mSrlRefresh

class TalentTypeActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    var mTalentTypeAdapter: TalentTypeAdapter? = null
    var mTalentAdapter: TalentAdapter? = null

    var mTalentCellName: String? = null

    val commonVM: CommonVM by viewModels {
        InjectorUtils.provideCommonVMFactory(this)
    }

    companion object {
        fun  intentStart (activity: AppCompatActivity,talentTypeName: String) {
            var intent = Intent(activity, TalentTypeActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,talentTypeName)
            activity.startActivity(intent)
        }
    }


    override fun getLayoutResource() = R.layout.activity_talent_type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeUi()

        //设置 进度条的颜色变化，最多可以设置4种颜色
        mSrlRefresh.setColorSchemeResources(R.color.colorAccent)
        mSrlRefresh.setOnRefreshListener(this)

        mTalentTypeAdapter = TalentTypeAdapter(this,this)
        mRvTalentType.adapter = mTalentTypeAdapter

        mTalentAdapter = TalentAdapter(this,this)
        mRvTalent.adapter = mTalentAdapter

        mIvBack.setOnClickListener(this)
    }

    fun initData (intent: Intent?) {
        mTalentCellName = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        sendTalentTypeRequest()
    }

    fun subscribeUi() {
        commonVM.talentTypeData.observe(this, Observer {
            mSrlRefresh.isRefreshing = false
            when(it) {
                is HttpResult.Success -> {
                    showTalentTypeData(it.value)
                }
                is HttpResult.Error -> {
                    ToastUtils.show(it.message.toString())
                }
            }
        })
    }

    fun sendTalentTypeRequest () {
        commonVM.fetchTalentTypeData()
    }

    fun showTalentTypeData (data: TalentTypeReq) {
        var categoryPosition = 0
        var talentPosition = -1
        var talentCellPosition = -1

        var count = data.data?.size ?: 0
        if (data.data != null && count > 0) {
            for (i in 0 until count) {
                var category = data.data?.get(i)
                var categoryCount = category?.childs?.size ?: 0
                if (category?.childs != null && categoryCount > 0) {
                    for (j in 0 until categoryCount) {
                        var talent = category?.childs?.get(j)
                        var childsCount = talent?.childs?.size ?: 0
                        if (talent?.childs != null && childsCount > 0) {
                            for (k in 0 until childsCount) {
                                var talentCell = talent?.childs?.get(k)?.name
                                if (TextUtils.equals(mTalentCellName,talentCell)) {
                                    categoryPosition = i
                                    talentPosition = j
                                    talentCellPosition = k
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        mTalentTypeAdapter?.clear()
        mTalentTypeAdapter?.add(data.data)
        mTalentTypeAdapter?.checkPosition = categoryPosition
        mTalentTypeAdapter?.notifyDataSetChanged()

        showTalentCellData(categoryPosition, talentPosition,talentCellPosition)
    }

    fun showTalentCellData (parentPosition: Int,talentPosition: Int,talentCellPosition: Int) {
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
        if (talentPosition >= 0 &&talentCellPosition >= 0) {
            var categoryItem = mTalentTypeAdapter?.getItem(parentPosition)
            var talentItem = categoryItem?.childs?.get(talentPosition)
            var talentCellItem = talentItem?.childs?.get(talentCellPosition)
            mTalentAdapter?.checkCellId = talentCellItem?.id
        }
        mTalentAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.mIvTalentType -> {
                mTalentTypeAdapter?.checkPosition = position
                mTalentTypeAdapter?.notifyDataSetChanged()

                showTalentCellData(position,-1,-1)
            }
            R.id.mTvTalentCell -> {
                var talentData = mTalentAdapter?.getItem(id.toInt())
                LiveDataBus.send(BusinessActions.TALENT_TYPE,talentData?.childs?.get(position))
                onBackPressed()
            }
        }
    }

    override fun onRefresh() {
        sendTalentTypeRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}