package com.jacky.foundationimpl.arch

import android.util.Log
import com.jacky.foundationapi.arch.IPluginRouterRegister
import com.jacky.foundationapi.arch.IRouterRegister
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