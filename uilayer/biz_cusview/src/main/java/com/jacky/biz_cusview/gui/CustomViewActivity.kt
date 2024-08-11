package com.jacky.biz_cusview.gui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.biz_cusview.fragment.CoordinateSystemFragment
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.bizcommon.ui.VerticalListActivity
import com.jacky.bizcommon.R as BaseR

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/15, jacky.li, Create file
 * @since 2023/6/15
 * @version v1.0.00
 */
@Route(group = RouterConstant.Page.GROUP_NAME, path = RouterConstant.Page.CUSTOM_VIEW_ACTIVITY)
class CustomViewActivity : VerticalListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
    }

    private fun setListener() {
        listAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                showFragment(getListData()[position].second as Fragment)
            }
        }
    }

    override fun getListData(): MutableList<Pair<String, Any>> {
        return mutableListOf(
            Pair("演示Android中坐标系统", CoordinateSystemFragment())
        )
    }

    private fun showFragment(fragment: Fragment) {
        // 插件apk用的fg_container资源是来自biz_common，该组件属于base apk中
        supportFragmentManager.beginTransaction()
            .replace(BaseR.id.fg_container, fragment).addToBackStack(fragment.javaClass.name)
            .commit()
    }
}