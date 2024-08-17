package com.jacky.biz_animation.router

import android.util.Log
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta
import com.jacky.biz_animation.gui.AnimationActivity
import com.jacky.bizcommon.route.IAnimationRouterRegister
import com.jacky.bizcommon.route.RouterConstant

class AnimationRouterRegister : IAnimationRouterRegister {

    override fun registerPage() {
        // 动态注册组件路由到路由中心表
        Log.d(TAG, "registerPage: ")
        LogisticsCenter.addRouteGroupDynamic(
            RouterConstant.Page.GROUP_NAME
        ) { atlas ->
            val routeMeta = RouteMeta()
            routeMeta.path = RouterConstant.Page.ANIMATION_ACTIVITY
            routeMeta.group = RouterConstant.Page.GROUP_NAME
            routeMeta.type = RouteType.ACTIVITY
            routeMeta.destination = AnimationActivity::class.java
            atlas?.put(RouterConstant.Page.ANIMATION_ACTIVITY, routeMeta)
        }
    }

    companion object {
        private const val TAG = "AnimationRouterRegister"
    }
}