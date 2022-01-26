package com.flash.worker.module.message.view.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.flash.worker.lib.common.base.BaseActivity
import com.flash.worker.lib.common.interfaces.OnBitmapLoadListener
import com.flash.worker.lib.common.util.*
import com.flash.worker.lib.common.view.dialog.LoadingDialog
import com.flash.worker.lib.common.view.widget.PullBackLayout
import com.flash.worker.module.message.R
import kotlinx.android.synthetic.main.activity_view_message_image.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ViewMessageImageActivity: BaseActivity(),View.OnClickListener, PullBackLayout.Callback,
    OnBitmapLoadListener {

    companion object {
        fun  intentStart (activity: AppCompatActivity, data: String?) {
            var intent = Intent(activity, ViewMessageImageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            activity.startActivity(intent)
        }

        fun  intentStart (activity: AppCompatActivity,
                          data: String?,
                          sharedElement: View,
                          sharedElementName: String) {
            var intent = Intent(activity, ViewMessageImageActivity::class.java)
            intent.putExtra(Constant.INTENT_DATA_KEY,data)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,sharedElement, sharedElementName)

            ActivityCompat.startActivity(activity, intent,options.toBundle())
        }
    }

    var imageUrl: String? = null
    private var background: ColorDrawable? = null
    var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutResource() = R.layout.activity_view_message_image

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        mLoadingDialog = LoadingDialog(this)
        background = ColorDrawable(Color.BLACK)
        mPblBack.background = background

        mIvImage.setOnClickListener(this)
        mIvDownload.setOnClickListener(this)
        mPblBack.callback = this
    }

    fun initData(intent: Intent?) {
        imageUrl = intent?.getStringExtra(Constant.INTENT_DATA_KEY)
        ImageUtils.instance.loadImage(this,mIvImage,imageUrl)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvImage -> {
                onBackPressed()
            }
            R.id.mIvDownload -> {
                if (TextUtils.isEmpty(imageUrl)) {
                    ToastUtils.show("图片地址错误")
                    return
                }
                if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return
                }
                ImageUtils.instance.getBitmap(this,imageUrl,this)
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

    fun downImage (bitmap: Bitmap?) {
        mLoadingDialog?.show()
        lifecycleScope.launch {
            //lifecycleScope使协程的生命周期随着activity的生命周期变化
            saveImage(bitmap)
            mLoadingDialog?.dismiss()
        }
    }

    suspend fun saveImage (bitmap: Bitmap?) {
        try {
            withContext(Dispatchers.IO) {
                var lastIndex = imageUrl?.lastIndexOf("/") ?: 0
                var length = imageUrl?.length ?: 0
                val fileName = imageUrl?.substring(lastIndex + 1, length) + ".png"
                val dir = FileUtil.getExRootFolder()

                BitmapUtil.saveBitmap(bitmap!!,dir?.absolutePath,fileName!!,true)

                withContext(Dispatchers.Main) {
                    ToastUtils.show("已保存到相册",R.mipmap.ic_save_image_message_success)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.show("保存图片失败-error = ${e.localizedMessage}")
        }

    }

    override fun onPermissionGranted(permissionName: Array<out String>) {
        super.onPermissionGranted(permissionName)
        Loger.e(TAG, "onPermissionGranted-Permission(s) " + Arrays.toString(permissionName) + " Granted")
        if (permissionName.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ImageUtils.instance.getBitmap(this,imageUrl,this)
        }
    }

    override fun onPermissionDeclined(permissionName: Array<out String>) {
        super.onPermissionDeclined(permissionName)
        Loger.e(TAG, "onPermissionDeclined-Permission(s) " + Arrays.toString(permissionName) + " Declined")
        if (permissionName.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ToastUtils.show("保存图片失败，请打开存储权限")
        }
    }

    override fun onPermissionReallyDeclined(permissionName: String) {
        super.onPermissionReallyDeclined(permissionName)
        if (permissionName.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ToastUtils.show("保存图片失败，请打开存储权限")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }

    override fun OnBitmapLoad(bitmap: Bitmap?) {
        downImage(bitmap)
    }
}