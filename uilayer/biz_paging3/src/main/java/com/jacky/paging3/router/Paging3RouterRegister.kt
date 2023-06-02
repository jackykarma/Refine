package com.jacky.paging3.router

import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta
import com.alibaba.android.arouter.facade.template.IRouteGroup
import com.jacky.basebiz.route.IPaging3RouterRegister
import com.jacky.basebiz.route.RouterConstant
import com.jacky.paging3.gui.Paging3Activity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 */
class Paging3RouterRegister : IPaging3RouterRegister {

    override fun registerPage() {
        LogisticsCenter.addRouteGroupDynamic(RouterConstant.Page.GROUP_NAME
        ) { atlas ->
            val routeMeta = RouteMeta()
            routeMeta.type = RouteType.ACTIVITY
            routeMeta.path = RouterConstant.Page.PAGING3_ACTIVITY
            routeMeta.group = RouterConstant.Page.GROUP_NAME
            routeMeta.destination = Paging3Activity::class.java
            atlas?.put(RouterConstant.Page.PAGING3_ACTIVITY, routeMeta)
        }
    }
}