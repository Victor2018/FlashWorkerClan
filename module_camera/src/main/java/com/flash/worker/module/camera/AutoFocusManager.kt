package com.flash.worker.module.camera

import android.annotation.SuppressLint
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import java.util.concurrent.RejectedExecutionException


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AutoFocusManager
 * Author: Victor
 * Date: 2020/12/18 15:33
 * Description: 
 * -----------------------------------------------------------------
 */
class AutoFocusManager(camera: Camera): Camera.AutoFocusCallback {
    val TAG = "AutoFocusManager"
    private var useAutoFocus = false
    private val camera: Camera? = null
    private var stopped = false
    private var focusing = false
    private var outstandingTask: AsyncTask<*, *, *>? = null

    companion object {
        var AUTO_FOCUS_INTERVAL_MS = 2000L
        var FOCUS_MODES_CALLING_AF: MutableList<String>? = ArrayList()

    }

    init {
        FOCUS_MODES_CALLING_AF?.add(Camera.Parameters.FOCUS_MODE_AUTO)
        FOCUS_MODES_CALLING_AF?.add(Camera.Parameters.FOCUS_MODE_MACRO)

        val currentFocusMode = camera.parameters.focusMode
        useAutoFocus = FOCUS_MODES_CALLING_AF!!.contains(currentFocusMode)
        //  Log.i(TAG, "Current focus mode '" + currentFocusMode + "'; use auto focus? " + useAutoFocus);
        start()
    }
    override fun onAutoFocus(success: Boolean, camera: Camera?) {
        focusing = false
        autoFocusAgainLater()
    }

    @SuppressLint("NewApi")
    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            val newTask = AutoFocusTask()
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else {
                    newTask.execute()
                }
                outstandingTask = newTask
            } catch (ree: RejectedExecutionException) {
                Log.w(TAG, "Could not request auto focus", ree)
            }
        }
    }

    @Synchronized
    fun start() {
        if (useAutoFocus) {
            outstandingTask = null
            if (!stopped && !focusing) {
                try {
                    camera?.autoFocus(this)
                    Log.w(TAG, "自动对焦")
                    focusing = true
                } catch (re: RuntimeException) {
                    // Have heard RuntimeException reported in Android 4.0.x+;
                    // continue?
                    Log.w(TAG,"Unexpected exception while focusing", re)
                    // Try again later to keep cycle going
                    autoFocusAgainLater()
                }
            }
        }
    }

    @Synchronized
    private fun cancelOutstandingTask() {
        if (outstandingTask?.getStatus() != AsyncTask.Status.FINISHED) {
            outstandingTask?.cancel(true)
        }
        outstandingTask = null
    }

    @Synchronized
    fun stop() {
        stopped = true
        if (useAutoFocus) {
            cancelOutstandingTask()
            // Doesn't hurt to call this even if not focusing
            try {
                camera?.cancelAutoFocus()
            } catch (re: RuntimeException) {
                // Have heard RuntimeException reported in Android 4.0.x+;
                // continue?
                Log.w(TAG,"Unexpected exception while cancelling focusing", re)
            }
        }
    }

    inner class AutoFocusTask :AsyncTask<Any, Any, Any>() {
        override fun doInBackground(vararg voids: Any): Any? {
            try {
                Thread.sleep(AUTO_FOCUS_INTERVAL_MS)
            } catch (e: InterruptedException) {
            }
            start()
            return null
        }
    }
}