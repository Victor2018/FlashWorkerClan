package com.flash.worker.module.task.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.common.view.adapter.JobWorkPicAdapter
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.coremodel.data.bean.HttpResult
import com.flash.worker.lib.coremodel.data.bean.TaskSettledInfo
import com.flash.worker.lib.coremodel.data.bean.TaskSubmitDetailData
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.coremodel.data.parm.TaskSubmitDetailParm
import com.flash.worker.lib.coremodel.data.req.TaskSubmitDetailReq
import com.flash.worker.lib.coremodel.util.InjectorUtils
import com.flash.worker.lib.coremodel.viewmodel.TalentJobVM
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.task.R
import kotlinx.android.synthetic.main.activity_task_submit_detail.*

@Route(path = ARouterPath.TaskSubmitDetailAct)
class TaskSubmitDetailActivity : BaseActivity(),View.OnClickListener, AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: TaskSubmitDetailData?) {
            var intent = Intent(activity, TaskSubmitDetailActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var mLoadingDialog: LoadingDialog? = null
    var mJobWorkPicAdapter: JobWorkPicAdapter? = null
    var mTaskSubmitDetailData: TaskSubmitDetailData? = null
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    override fun getLayoutResource() = R.layout.activity_task_submit_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()

        mLoadingDialog = LoadingDialog(this)
        mJobWorkPicAdapter = JobWorkPicAdapter(this, this)
        mRvWorksPic.adapter = mJobWorkPicAdapter

        mIvBack.setOnClickListener(this)

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder: RecyclerView.ViewHolder = mRvWorksPic
                        .findViewHolderForAdapterPosition(currentWorkImagePositon) ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = selectedViewHolder.itemView.findViewById(R.id.mIvJobPic)
                }
            })
    }

    fun initData (intent: Intent?) {
        mTaskSubmitDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as TaskSubmitDetailData?
        showTaskSubmitDetailData()
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
            .observeForever(this, Observer {
                currentWorkImagePositon = it as Int
            })

    }

    fun showTaskSubmitDetailData () {
        mTvUserName.text = "人才 ：${mTaskSubmitDetailData?.username}"
        mTvUserId.text = "ID：${mTaskSubmitDetailData?.userId}"
        mTvSubmitTime.text = "提交时间：${mTaskSubmitDetailData?.submitTime}"

        mTvContent.text = mTaskSubmitDetailData?.content

        var picCount = mTaskSubmitDetailData?.pics?.size ?: 0
        if (picCount <= 0) {
            tv_pic.visibility = View.GONE
        }
        mJobWorkPicAdapter?.clear()
        mJobWorkPicAdapter?.add(getWorkPics(mTaskSubmitDetailData?.pics))
        mJobWorkPicAdapter?.notifyDataSetChanged()

    }


    fun getWorkPics (pics: List<String>?): List<WorkPicInfo>? {
        var datas = ArrayList<WorkPicInfo>()

        if (pics == null) return datas
        if (pics.size == 0) return datas

        for (url in pics) {
            var data = WorkPicInfo()
            data.pic = url
            datas.add(data)
        }
        return datas
    }

    fun getViewImageUrls(urls: List<WorkPicInfo>?): ArrayList<String> {
        var imgUrls = ArrayList<String>()
        urls?.let {
            urls?.forEach {
                it.pic?.let { it1 -> imgUrls.add(it1) }
            }
        }
        return imgUrls
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentWorkImagePositon = position
        when (view?.id) {
            R.id.mIvJobPic -> {
                ViewImageActivity.intentStart(this,
                    getViewImageUrls(mJobWorkPicAdapter?.getDatas()),
                    position,
                    view?.findViewById(R.id.mIvJobPic),
                    ResUtils.getStringRes(R.string.img_transition_name))
            }
            else -> {

            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvBack -> {
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}