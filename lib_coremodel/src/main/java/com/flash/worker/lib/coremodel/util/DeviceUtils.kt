package com.flash.worker.lib.coremodel.util

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import permission.victor.com.library.PermissionHelper
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import kotlin.experimental.and


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DeviceUtils
 * Author: Victor
 * Date: 2020/11/27 17:28
 * Description: 
 * -----------------------------------------------------------------
 */
object DeviceUtils {
    const val TAG = "DeviceUtils"
    /**
     * 获取设备的唯一标识 物理地址加device id
     *
     * @return
     */
    fun getUDID(context: Context?): String? {
        var udid: String = getDeviceID(context)

        if (TextUtils.isEmpty(udid)) {
            udid = getAndroidID(context)
        }
        if (TextUtils.isEmpty(udid)) {
            udid = getDeviceUUID()
        }
        var udidMd5Str = CryptoUtils.MD5(udid)
        Log.e(TAG,"getUDID()...udidMd5Str = $udidMd5Str")
        return udidMd5Str
    }

    fun getDeviceID (context: Context?): String {
        var deviceId = ""
        try {
            val tm = context?.applicationContext?.getSystemService(
                Context.TELEPHONY_SERVICE) as TelephonyManager

            if (PermissionHelper.isPermissionGranted(context,Manifest.permission.READ_PHONE_STATE)) {
                deviceId = tm.deviceId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e(TAG,"getDeviceID()...deviceId = $deviceId")
        return deviceId
    }

    fun getAndroidID (context: Context?): String {
        var androidId = ""
        try {
            androidId = Settings.Secure.getString(
                context?.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e(TAG,"getAndroidID()...androidId = $androidId")
        return androidId
    }

    fun getDeviceUUID(): String {
        var brand = Build.BRAND

        val dev = Build.ID +
                Build.MANUFACTURER +
                Build.BRAND +
                Build.PRODUCT +
                Build.DEVICE +
                Build.BOARD +
                Build.DISPLAY +
                Build.MODEL +
                Build.FINGERPRINT +
                Build.HOST
        var uuid = UUID(dev.hashCode().toLong(),brand.hashCode().toLong()).toString()
        Log.e(TAG,"getDeviceUUID()...uuid = $uuid")
        return uuid
    }

    fun getMac(): String {
        var macAddr = ""
        try {
            val mac: ByteArray
            val ne = NetworkInterface.getByInetAddress(
                InetAddress.getByName(getLocalIpAddress())
            )
            mac = ne.hardwareAddress
            macAddr = byte2hex(mac)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e(TAG,"getMac()...macAddr = $macAddr")
        return macAddr
    }

    fun getEthernetMac(): String? {
        var reader: BufferedReader? = null
        var ethernetMac = ""
        try {
            reader = BufferedReader(
                FileReader(
                    "sys/class/net/eth0/address"
                )
            )
            ethernetMac = reader.readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Log.e(TAG,"getEthernetMac()...ethernetMac = $ethernetMac")
        return ethernetMac.toUpperCase()
    }

    fun byte2hex(b: ByteArray): String {
        var hs = StringBuffer(b.size)
        var stmp = ""
        val len = b.size
        for (n in 0 until len) {
            stmp = Integer.toHexString((b[n] and 0xFF.toByte()).toInt())
            hs = if (stmp.length == 1) hs.append("0").append(stmp) else {
                hs.append(stmp)
            }
        }
        return hs.toString()
    }


    fun getLocalIpAddress(): String? {
        var sLocalIPAddress = ""
        try {
            val en =
                NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val netInterface =
                    en.nextElement() as NetworkInterface
                val ipaddr =
                    netInterface.inetAddresses
                while (ipaddr.hasMoreElements()) {
                    val inetAddress =
                        ipaddr.nextElement() as InetAddress
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        sLocalIPAddress = inetAddress.getHostAddress().toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        Log.e(TAG,"getLocalIpAddress()...sLocalIPAddress = $sLocalIPAddress")
        return sLocalIPAddress
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    fun getPhoneBrand(): String? {
        return Build.BRAND
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    fun getSysVersion(): String? {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    fun getPhoneModel(): String? {
        return Build.MODEL
    }

    fun getSysLanguage(): String? {
        val locale = Locale.getDefault()
        return locale.language
    }
}