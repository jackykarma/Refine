package com.jacky.foundation.arch

import android.util.Log
import java.util.ServiceLoader

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
object PluginRouterRegister : IPluginRouterRegister {

    private const val TAG = "PluginRouterRegister"

    /**
     * 动态注册组件提供的服务接口
     * FIXME：ServiceLoader的方式得到RouterRegister实际实例对象需要经过IO，存在性能风险，尤其是在启动时使用主线程，
     *  会对应用启动性能有影响。
     */
    override fun register(routerClz: Class<out IRouterRegister>) {
        val serviceLoader = ServiceLoader.load(routerClz)
        var iRouter: IRouterRegister? = null
        kotlin.runCatching {
            iRouter = serviceLoader.iterator().next() as IRouterRegister
        }.onFailure {
            Log.e(TAG, "dynamic registerRouter", it)
        }.onSuccess {
            iRouter?.registerPage()
        }
    }
}