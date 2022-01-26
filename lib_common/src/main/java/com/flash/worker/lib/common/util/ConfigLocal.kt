package com.flash.worker.lib.common.util


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ConfigLocal
 * Author: Victor
 * Date: 2021/8/23 16:02
 * Description: 
 * -----------------------------------------------------------------
 */
object ConfigLocal {
    private const val GUILD_RED_ENVELOPE_GUIDE = "GUILD_RED_ENVELOPE_GUIDE"

    /**
     * 是否显示公会红包引导弹窗
     * 针对用户
     *
     * @return
     */
    fun needShowGuildRedEnvelopeGuide(userId: String?): Boolean {
        return SharedPreferencesUtils.getBoolean( "$GUILD_RED_ENVELOPE_GUIDE:$userId", true)
    }

    fun updateShowGuildRedEnvelopeGuide(userId: String?, enable: Boolean) {
        SharedPreferencesUtils.putBoolean("$GUILD_RED_ENVELOPE_GUIDE:$userId", enable)
    }
}