package com.flash.worker.module.login.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.flash.worker.lib.common.base.ARouterPath
import com.flash.worker.lib.common.provider.LoginService
import com.flash.worker.lib.common.util.NavigationUtils


/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LoginServiceImpl
 * Author: Victor
 * Date: 2020/11/30 10:47
 * Description:  实现了登录服务接口
 * -----------------------------------------------------------------
 */
@Route(path = ARouterPath.loginService)
class LoginServiceImpl: LoginService {
    val TAG = "LoginServiceImpl"

    override fun toLoginView(phone: String, code: Int): String {
        NavigationUtils.goCodeLoginActivity(null,false)
        return phone + "_" + code + "_老了老弟"
    }

    override fun init(context: Context?) {
    }


}