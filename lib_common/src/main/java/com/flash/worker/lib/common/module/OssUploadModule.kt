package com.flash.worker.lib.common.module

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import com.alibaba.sdk.android.oss.*
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.OSSRequest
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.flash.worker.lib.common.app.App
import com.flash.worker.lib.common.base.BaseFragment
import com.flash.worker.lib.common.data.UploadData
import com.flash.worker.lib.common.interfaces.OnUploadListener
import com.flash.worker.lib.common.util.CacheCleanUtils
import com.flash.worker.lib.common.util.Loger
import com.flash.worker.lib.common.util.MainHandler
import com.flash.worker.lib.coremodel.http.api.FileApi
import com.flash.worker.lib.coremodel.util.WebConfig
import java.io.File
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: OssUploadModule
 * Author: Victor
 * Date: 2020/12/8 17:18
 * Description: 
 * -----------------------------------------------------------------
 */
class OssUploadModule: OSSCompletedCallback<PutObjectRequest, PutObjectResult>,
    OSSProgressCallback<PutObjectRequest> {
    val TAG = "OssUploadModule"
    var mUploadTask: OSSAsyncTask<*>? = null
    var mUploadData: UploadData? = null
    var mOnUploadListener: OnUploadListener? = null

    var mOSSCredentialProvider: OSSCredentialProvider? = null

    private object Holder {val instance = OssUploadModule()}

    companion object {
        val  instance: OssUploadModule by lazy { Holder.instance }
    }

    //OSS的上传下载
    fun upload (data: UploadData?,listener: OnUploadListener?) {
        mUploadData = data
        mOnUploadListener = listener
        val media = mUploadData?.localMedia

        if (media!!.isCut) {
            media.path = media.cutPath
            media.androidQToPath = media.cutPath
        }
        if (media.isCompressed) {
            media.path = media.compressPath
            media.androidQToPath = media.compressPath
        }

        var path = media.path
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            path = media.androidQToPath
        }

        val ossFileName = mUploadData?.dir + "/" + getUUID() + getFileName(path)

        val file = File(path)
        if (!file.exists()) {
            Loger.e(TAG, "upload()......$path FileNotExist")
            return
        }
        Loger.e(BaseFragment.TAG,"upload-imgSize = " + CacheCleanUtils.getFormatSize(file.length().toDouble()))

        // 构造上传请求。
        val put = PutObjectRequest(mUploadData?.bucketName, ossFileName, path)
        put.crC64 = OSSRequest.CRC64Config.YES
        // 异步上传时可以设置进度回调。
        put.progressCallback = this
        mUploadTask = getOss().asyncPutObject(put,this)
    }

    fun getOSSCredentialProvider (): OSSCredentialProvider? {
        if (mOSSCredentialProvider == null) {
            val url = WebConfig.getRequestUrl(FileApi.GENERATE_CREDENTIALS)
            mOSSCredentialProvider = OSSAuthCredentialsProvider(url)
        }
        return mOSSCredentialProvider
    }

    fun getOss (): OSS {
        val conf = ClientConfiguration()
        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        var oss = OSSClient(App.get(), mUploadData?.ossEndPoint, getOSSCredentialProvider(), conf)
        OSSLog.enableLog()
        return oss
    }

    override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
        Loger.e(TAG, "onSuccess-eTag = " + result?.eTag)
        Loger.e(TAG, "onSuccess-requestId = " + result?.requestId)
        Loger.e(TAG, "onSuccess-objectKey = " + request?.objectKey)
        Loger.e(TAG, "onSuccess-eTag = " + result?.eTag)
        Loger.e(TAG, "onSuccess-serverCallbackReturnBody = " + result?.serverCallbackReturnBody)
        val url = "https://" + mUploadData?.bucketName + "." + mUploadData?.ossEndPoint  + "/" + request?.objectKey
        Loger.e(TAG, "onSuccess-url = " + url)
        OnUpload(100,url,true)
    }

    override fun onFailure(
        request: PutObjectRequest?,
        clientException: ClientException?,
        serviceException: ServiceException?
    ) {
        var info = ""
        // 请求异常
        if (clientException != null) {
            // 本地异常如网络异常等
            clientException.printStackTrace()
            info = clientException.toString()
        }
        if (serviceException != null) {
            // 服务异常
            Loger.e(TAG, "onFailure-errorCode = " + serviceException.errorCode)
            Loger.e(TAG, "onFailure-requestId = " + serviceException.requestId)
            Loger.e(TAG, "onFailure-hostId = " + serviceException.hostId)
            Loger.e(TAG, "onFailure-rawMessage = " + serviceException.rawMessage)
            info = serviceException.toString()
            Loger.e(TAG, "onFailure-info = " + info)
        }

        OnUpload(-1,info,true)
    }

    override fun onProgress(request: PutObjectRequest?, currentSize: Long, totalSize: Long) {
        Loger.e(TAG, "onProgress-currentSize: $currentSize totalSize: $totalSize")
        val progress = (100 * currentSize / totalSize).toInt()
        Loger.e(TAG, "onProgress-上传进度: $progress%")
        OnUpload(progress,null,false)
    }

    fun OnUpload(progress: Int,url: String?,complete: Boolean) {
        MainHandler.get().runMainThread(Runnable {
            mOnUploadListener?.OnUpload(progress,url,complete)
        })
    }

    fun getRealImageUrl (bucketName: String?, objectKey: String?): String {
        var url = ""
        try {
            url = getOss().presignConstrainedObjectURL(bucketName,objectKey, 30 * 60)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return url
    }

    fun getUUID(): String? {
        val uuid = UUID.randomUUID()
        val uniqueId = uuid.toString()
        Loger.d(TAG, "getUUID()......uuid = $uuid")
        return uniqueId
    }

    fun getFileName(path: String): String? {
        if (TextUtils.isEmpty(path)) return ".png"
        try {
            val lastDotIndex = path.lastIndexOf(".")
            return if (lastDotIndex != -1) {
                path.substring(lastDotIndex)
            } else {
                ".png"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ".png"
    }

    fun onDestroy() {
        mUploadTask?.cancel()//取消上传任务。
        mUploadData = null
        mOnUploadListener = null
    }
}