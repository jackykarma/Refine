package com.jacky.biz_main.router

import android.util.Log
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta
import com.jacky.bizcommon.route.IHomeRouterRegister
import com.jacky.bizcommon.route.RouterConstant.Page
import com.jacky.biz_main.HomeActivity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
class HomeRouterRegister : IHomeRouterRegister {

    override fun registerPage() {
        // 动态注册组件路由到路由中心表
        Log.d(TAG, "registerPage: ")
        LogisticsCenter.addRouteGroupDynamic(
            Page.GROUP_NAME
        ) { atlas ->
            val routeMeta = RouteMeta()
            routeMeta.path = Page.HOME_ACTIVITY
            routeMeta.group = Page.GROUP_NAME
            routeMeta.type = RouteType.ACTIVITY
            routeMeta.destination = HomeActivity::class.java // 在app模块没有办法获取到HomeActivity::class对象
            atlas?.put(Page.HOME_ACTIVITY, routeMeta)
        }
    }

    companion object {
        private const val TAG = "HomeRouterRegister"
    }
}