package com.flash.worker.lib.common.module

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.flash.worker.lib.common.data.ProgressInfo
import com.flash.worker.lib.common.interfaces.OnDownloadProgressListener
import com.flash.worker.lib.common.util.MainHandler
import com.flash.worker.lib.coremodel.data.bean.LatestVersionData
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownLoadTask
 * Author: Victor
 * Date: 2021/8/31 11:34
 * Description: 
 * -----------------------------------------------------------------
 */
class DownLoadTask(var context: Context?, var listener: OnDownloadProgressListener?): AsyncTask<LatestVersionData, Integer, ProgressInfo>() {
    val TAG = "DownLoadTask"
    var data = ProgressInfo()

    override fun doInBackground(vararg params: LatestVersionData?): ProgressInfo {
        try {
            var updateData = params[0]

            data.downloadUrl = updateData?.appDownloadUrl
            data.savePath = context?.filesDir?.path
            data.filePath = data.savePath + File.separator + data.getUrlFileName() + ".apk"

            var url = URL(data.downloadUrl)

            val con: URLConnection = url.openConnection()
            data.totalSize = con.contentLength.toLong()
            val inputStream = con.getInputStream()

            val file = File(data.filePath)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()

            val fos = FileOutputStream(file)
            val buffer = ByteArray(2048)
            var size: Int = inputStream.read(buffer)
            while (size != -1) {
                data.downloadSize += size
                fos.write(buffer, 0, size)
                size = inputStream.read(buffer)
                //    时刻将当前进度更新给onProgressUpdate方法
                publishProgress(Integer(data?.percent().toInt()))
            }
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            MainHandler.get().runMainThread(Runnable {
                listener?.OnError(e.localizedMessage)
            })
        }

        return data
    }

    override fun onProgressUpdate(vararg values: Integer?) {
        super.onProgressUpdate(*values)
        Log.e(TAG, "download-progress = " + values[0] + "%");
        listener?.OnDownloadProgress(data)
    }

    override fun onPostExecute(result: ProgressInfo) {
        super.onPostExecute(result)
        listener?.OnDownloadCompleted(result)
    }


}