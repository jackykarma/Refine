package com.jacky.foundationapi.arch

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
interface IPluginRouterRegister {

    fun register(routerClz: Class<out IRouterRegister>)
}