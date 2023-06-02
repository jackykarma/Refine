package com.jacky.appsupport

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.jacky.basebiz.route.IHomeRouterRegister
import com.jacky.basebiz.route.IPaging3RouterRegister
import com.jacky.foundationimpl.arch.PluginRouterRegister

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initARouter()
        registerPluginRouter()
    }

    /**
     * 注册动态feature插件路由
     */
    private fun registerPluginRouter() {
        PluginRouterRegister.register(IHomeRouterRegister::class.java)
        PluginRouterRegister.register(IPaging3RouterRegister::class.java)
    }

    /**
     * 初始化ARouter路由库
     */
    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }
}