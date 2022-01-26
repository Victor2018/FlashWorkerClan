package com.flash.worker.module.mine.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.util.ResUtils
import com.flash.worker.lib.common.view.activity.ViewImageActivity
import com.flash.worker.lib.coremodel.data.bean.GuildDetailData
import com.flash.worker.lib.coremodel.data.bean.MyGuildData
import com.flash.worker.lib.coremodel.data.bean.WorkPicInfo
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.flash.worker.module.mine.R
import com.flash.worker.module.mine.view.adapter.GuildImageAdapter
import kotlinx.android.synthetic.main.activity_guild_introduction.*

class GuildIntroductionActivity : BaseActivity(),View.OnClickListener,AdapterView.OnItemClickListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity,data: MyGuildData?) {
            var intent = Intent(activity, GuildIntroductionActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
        fun  intentStart (activity: AppCompatActivity,data: GuildDetailData?) {
            var intent = Intent(activity, GuildIntroductionActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }
    }

    var mMyGuildData: MyGuildData? = null
    var mGuildDetailData: GuildDetailData? = null
    var mGuildImageAdapter: GuildImageAdapter? = null
    var currentWorkImagePositon = 0//当前工作情景图片浏览位置

    override fun getLayoutResource() = R.layout.activity_guild_introduction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        subscribeEvent()

        mGuildImageAdapter = GuildImageAdapter(this,this)
        mGuildImageAdapter?.isPreview = true
        mRvImage.adapter = mGuildImageAdapter

        mIvBack.setOnClickListener(this)

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
        var data = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY)
        if (data is MyGuildData) {
            mMyGuildData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as MyGuildData?
            mTvIntroduction.text = mMyGuildData?.guildProfile

            mGuildImageAdapter?.clear()
            mGuildImageAdapter?.add(getPics(mMyGuildData?.profilePics))
            mGuildImageAdapter?.notifyDataSetChanged()
        } else if(data is GuildDetailData) {
            mGuildDetailData = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as GuildDetailData?

            mTvIntroduction.text = mGuildDetailData?.guildProfile

            mGuildImageAdapter?.clear()
            mGuildImageAdapter?.add(getPics(mGuildDetailData?.profilePics))
            mGuildImageAdapter?.notifyDataSetChanged()
        }
    }

    fun subscribeEvent() {
        LiveDataBus.with(JobActions.REFRESH_IMAGE_VIEW_POSITION)
                .observeForever(this, Observer {
                    currentWorkImagePositon = it as Int
                })
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        currentWorkImagePositon = position

        var profilePics: List<String>? = null
        if (mGuildDetailData != null) {
            profilePics = mGuildDetailData?.profilePics
        } else if (mMyGuildData != null) {
            profilePics = mMyGuildData?.profilePics
        }

        ViewImageActivity.intentStart(this,
                profilePics as ArrayList<String>,
                position,
                view?.findViewById(R.id.mIvRelatedCertificate),
                ResUtils.getStringRes(R.string.img_transition_name))
    }

}