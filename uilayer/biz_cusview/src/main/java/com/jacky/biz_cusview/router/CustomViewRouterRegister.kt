package com.jacky.biz_cusview.router

import android.util.Log
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta
import com.jacky.bizcommon.route.ICustomViewRouterRegister
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.biz_cusview.gui.CustomViewActivity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/8/18, jacky.li, Create file
 * @since 2023/8/18
 * @version v1.0.00
 */
class CustomViewRouterRegister : ICustomViewRouterRegister {

    override fun registerPage() {
        // 动态注册组件路由到路由中心表
        Log.d(TAG, "registerPage: ")
        LogisticsCenter.addRouteGroupDynamic(
            RouterConstant.Page.GROUP_NAME
        ) { atlas ->
            val routeMeta = RouteMeta()
            routeMeta.path = RouterConstant.Page.CUSTOM_VIEW_ACTIVITY
            routeMeta.group = RouterConstant.Page.GROUP_NAME
            routeMeta.type = RouteType.ACTIVITY
            routeMeta.destination = CustomViewActivity::class.java
            atlas?.put(RouterConstant.Page.CUSTOM_VIEW_ACTIVITY, routeMeta)
        }
    }

    companion object {
        private const val TAG = "HomeRouterRegister"
    }
}