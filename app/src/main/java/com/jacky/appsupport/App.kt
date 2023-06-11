package com.jacky.appsupport

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jacky.basebiz.route.IHomeRouterRegister
import com.jacky.basebiz.route.IPaging3RouterRegister
import com.jacky.basebiz.route.ITaskRouterRegister
import com.jacky.foundation.arch.PluginRouterRegister
import com.jacky.foundation.log.HiConsolePrinter
import com.jacky.foundation.log.HiFilePrinter
import com.jacky.foundation.log.HiLogConfig
import com.jacky.foundation.log.HiLogManager

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
        initLog()
    }

    /**
     * 注册动态feature插件路由
     */
    private fun registerPluginRouter() {
        PluginRouterRegister.register(IHomeRouterRegister::class.java)
        PluginRouterRegister.register(IPaging3RouterRegister::class.java)
        PluginRouterRegister.register(ITaskRouterRegister::class.java)
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

    /**
     * 日志库初始化
     */
    private fun initLog() {
        HiLogManager.init(object : HiLogConfig() {
            override fun injectJsonParser(): JsonParser {
                return JsonParser { src: Any? ->
                    // 避免出现Task(groupName\u003d\u0027page1\u0027符号
                    GsonBuilder().disableHtmlEscaping().create().toJson(src)
                }
            }

            override fun includeThread(): Boolean {
                return false
            }

            override fun stackTraceDepth(): Int {
                return 0
            }

            override fun getGlobalTag(): String {
                return "Refine"
            }
        }, HiConsolePrinter(), HiFilePrinter.getInstance(cacheDir.absolutePath, 0))
    }
}