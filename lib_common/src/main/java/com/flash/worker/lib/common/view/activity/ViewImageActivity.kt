package com.flash.worker.lib.common.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.viewpager.widget.ViewPager
import com.flash.worker.lib.common.R
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.util.Constant
import com.flash.worker.lib.common.view.adapter.ViewImagePagerAdapter
import com.flash.worker.lib.common.view.adapter.ViewPagerAdapter
import com.flash.worker.lib.common.view.fragment.ViewImageFragment
import com.flash.worker.lib.common.view.widget.PullBackLayout
import com.flash.worker.lib.livedatabus.action.JobActions
import com.flash.worker.lib.livedatabus.core.LiveDataBus
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.activity_view_image.*


class ViewImageActivity : BaseActivity(), ViewPager.OnPageChangeListener,View.OnClickListener,
        PullBackLayout.Callback {

    companion object {
        fun  intentStart (activity: AppCompatActivity, datas: ArrayList<String>?, position: Int) {
            var intent = Intent(activity, ViewImageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.POSITION_KEY,position)
            activity.startActivity(intent)
        }

        fun  intentStart (activity: AppCompatActivity,
                          datas: ArrayList<String>?,
                          position: Int,
                          sharedElement: View?,
                          sharedElementName: String) {
            var intent = Intent(activity, ViewImageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,datas)
            intent.putExtra(Constant.POSITION_KEY,position)

            if (sharedElement == null) {
                activity.startActivity(intent)
            } else {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,sharedElement, sharedElementName)
                ActivityCompat.startActivity(activity, intent,options.toBundle())
            }
        }
    }

//    var mViewImagePagerAdapter: ViewImagePagerAdapter? = null
    var mViewPagerAdapter: ViewPagerAdapter? = null
    var imgUrls: ArrayList<String>? = null
    var fragmentList = ArrayList<ViewImageFragment>()
    var pagePosition: Int = 0

    private var background: ColorDrawable? = null

    override fun getLayoutResource() = R.layout.activity_view_image

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        background = ColorDrawable(Color.BLACK)
        mPblBack.background = background

//        mViewImagePagerAdapter = ViewImagePagerAdapter(this)
//        mViewImagePagerAdapter?.mOnClickListener = this
//        mVpImage.adapter = mViewImagePagerAdapter

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mVpImage.adapter = mViewPagerAdapter

        mVpImage.addOnPageChangeListener(this)
        mPblBack.callback = this

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                        // Locate the image view at the primary fragment (the ImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.
                        val view = mViewPagerAdapter?.getItem(mVpImage.currentItem)?.view ?: return

                        // Map the first shared element name to the child ImageView.
                        sharedElements[names[0]] = view.findViewById(R.id.mIvImage)
                    }
                })
    }

    fun initData (intent: Intent?) {
        imgUrls = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as ArrayList<String>?
        pagePosition = intent?.getIntExtra(Constant.POSITION_KEY,0) ?: 0

//        mViewImagePagerAdapter?.datas = imgUrls
//        mViewImagePagerAdapter?.notifyDataSetChanged()

        imgUrls?.forEach {
            fragmentList.add(ViewImageFragment.newInstance(it))
        }
        mViewPagerAdapter?.fragmetList = fragmentList
        mViewPagerAdapter?.notifyDataSetChanged()

        var imgCount = imgUrls?.size ?: 0
        if (imgCount > 0 && pagePosition < imgCount) {
            mVpImage.currentItem = pagePosition
        }

        mTvImageCount.text = String.format("%d/%d",pagePosition + 1,imgUrls?.size)

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        LiveDataBus.send(JobActions.REFRESH_IMAGE_VIEW_POSITION,position)
        mTvImageCount.text = String.format("%d/%d",position + 1,mViewPagerAdapter?.count)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvImage -> {
                onBackPressed()
            }
        }
    }

    override fun onPullStart() {
    }

    override fun onPull(progress: Float) {
        var progress = Math.min(1f, progress * 3f)
        background?.alpha = (0xff * (1f - progress)).toInt()
    }

    override fun onPullCancel() {
    }

    override fun onPullComplete() {
        supportFinishAfterTransition();
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }


}