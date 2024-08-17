package com.jacky.biz_main

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.jacky.bizcommon.route.RouterConstant.Page
import com.jacky.bizcommon.ui.VerticalListActivity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
@Route(group = Page.GROUP_NAME, path = Page.HOME_ACTIVITY)
class HomeActivity : VerticalListActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
    }

    private fun setListener() {
        listAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                ARouter.getInstance().build(getListData()[position].second as String).navigation()
            }
        }
    }

    override fun getListData(): MutableList<Pair<String, Any>> {
        return mutableListOf(
            Pair("自定义View业务组件", Page.CUSTOM_VIEW_ACTIVITY),
            Pair("动画业务组件", Page.ANIMATION_ACTIVITY),
            Pair("Paging3演示业务组件", Page.PAGING3_ACTIVITY),
            Pair("Lint静态检查演示业务组件", Page.LINT_ACTIVITY),
            Pair("任务调度与管理业务组件", Page.TASK_MANAGE_ACTIVITY)
        )
    }
}