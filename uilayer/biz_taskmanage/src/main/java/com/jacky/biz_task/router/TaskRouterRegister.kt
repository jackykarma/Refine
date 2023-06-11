package com.jacky.biz_task.router

import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta
import com.jacky.basebiz.route.ITaskRouterRegister
import com.jacky.basebiz.route.RouterConstant
import com.jacky.biz_task.gui.TaskManageActivity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 */
class TaskRouterRegister : ITaskRouterRegister {

    override fun registerPage() {
        LogisticsCenter.addRouteGroupDynamic(RouterConstant.Page.GROUP_NAME
        ) { atlas ->
            val routeMeta = RouteMeta()
            routeMeta.type = RouteType.ACTIVITY
            routeMeta.path = RouterConstant.Page.TASK_MANAGE_ACTIVITY
            routeMeta.group = RouterConstant.Page.GROUP_NAME
            routeMeta.destination = TaskManageActivity::class.java
            atlas?.put(RouterConstant.Page.TASK_MANAGE_ACTIVITY, routeMeta)
        }
    }
}