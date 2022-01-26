package com.flash.worker.module.camera

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.*


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: SensorControler
 * Author: Victor
 * Date: 2020/12/18 15:23
 * Description: 加速度控制器，用来控制对焦
 * -----------------------------------------------------------------
 */
class SensorControler(var context: Context): SensorEventListener {
    val TAG = "SensorControler"
    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var mX = 0
    private  var mY:Int = 0
    private  var mZ:Int = 0
    private var lastStaticStamp: Long = 0
    var mCalendar: Calendar? = null
    val DELEY_DURATION = 500
    private var foucsing = 1 //1 表示没有被锁定 0表示被锁定

    var isFocusing = false
    var canFocusIn = false //内部是否能够对焦控制机制

    var canFocus = false

    val STATUS_NONE = 0
    val STATUS_STATIC = 1
    val STATUS_MOVE = 2
    private var STATUE = STATUS_NONE

    var mCameraFocusListener: CameraFocusListener? = null

    init {
        mSensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // TYPE_GRAVITY
    }

    companion object {
        private var mInstance: SensorControler? = null

        fun getInstance(context: Context?): SensorControler? {
            if (mInstance == null) {
                synchronized(SensorControler.javaClass) {
                    if (mInstance == null) {
                        mInstance =
                            SensorControler(
                                context!!
                            )
                    }
                }
            }
            return mInstance
        }
    }

    fun onStart() {
        restParams()
        canFocus = true
        mSensorManager!!.registerListener(
            this, mSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun onStop() {
        mCameraFocusListener = null
        mSensorManager?.unregisterListener(this, mSensor)
        canFocus = false
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == null) {
            return
        }

        if (isFocusing) {
            restParams()
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toInt()
            val y = event.values[1].toInt()
            val z = event.values[2].toInt()
            mCalendar = Calendar.getInstance()
            val stamp = mCalendar?.getTimeInMillis() // 1393844912
            val second = mCalendar?.get(Calendar.SECOND) // 53
            if (STATUE != STATUS_NONE) {
                val px = Math.abs(mX - x)
                val py = Math.abs(mY - y)
                val pz = Math.abs(mZ - z)
                //                Log.d(TAG, "pX:" + px + "  pY:" + py + "  pZ:" + pz + "    stamp:"
                //                        + stamp + "  second:" + second);
                val value =
                    Math.sqrt(px * px + py * py + (pz * pz).toDouble())
                if (value > 1.4) {
                    //                    textviewF.setText("检测手机在移动..");
                    //                    Log.i(TAG,"mobile moving");
                    STATUE = STATUS_MOVE
                } else {
                    //                    textviewF.setText("检测手机静止..");
                    //                    Log.i(TAG,"mobile static");
                    //上一次状态是move，记录静态时间点
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp!!
                        canFocusIn = true
                    }
                    if (canFocusIn) {
                        if (stamp!! - lastStaticStamp > DELEY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if (!isFocusing) {
                                canFocusIn = false
                                //                                onCameraFocus();
                                if (mCameraFocusListener != null) {
                                    mCameraFocusListener?.onFocus()
                                }
                                //                                Log.i(TAG,"mobile focusing");
                            }
                        }
                    }
                    STATUE = STATUS_STATIC
                }
            } else {
                lastStaticStamp = stamp!!
                STATUE = STATUS_STATIC
            }
            mX = x
            mY = y
            mZ = z
        }
    }

    /**
     * 重置参数
     */
    private fun restParams() {
        STATUE = STATUS_NONE
        canFocusIn = false
        mX = 0
        mY = 0
        mZ = 0
    }

    /**
     * 对焦是否被锁定
     *
     * @return
     */
    fun isFocusLocked(): Boolean {
        return if (canFocus) {
            foucsing <= 0
        } else false
    }

    /**
     * 锁定对焦
     */
    fun lockFocus() {
        isFocusing = true
        foucsing--
        Log.i(TAG, "lockFocus")
    }

    /**
     * 解锁对焦
     */
    fun unlockFocus() {
        isFocusing = false
        foucsing++
        Log.i(TAG, "unlockFocus")
    }

    fun restFoucs() {
        foucsing = 1
    }


    interface CameraFocusListener {
        fun onFocus()
    }

}