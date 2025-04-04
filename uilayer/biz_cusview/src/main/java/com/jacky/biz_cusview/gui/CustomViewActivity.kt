package com.jacky.biz_cusview.gui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.biz_cusview.fragment.CoordinateSystemFragment
import com.jacky.biz_cusview.fragment.CustomView2DFragment
import com.jacky.biz_cusview.fragment.GLSurfaceViewFragment
import com.jacky.biz_cusview.fragment.SurfaceViewFragment
import com.jacky.biz_cusview.fragment.TextureViewFragment
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.bizcommon.ui.VerticalListActivity

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

    private var currentFragment:Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
    }

    private fun setListener() {
        listAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                currentFragment = getListData()[position].second as Fragment
                currentFragment?.let {
                    showFragment(it)
                }
            }
        }
    }

    override fun getListData(): MutableList<Pair<String, Any?>> {
        return mutableListOf(
            Pair("演示Android中坐标系统", CoordinateSystemFragment()),
            Pair("2D Canvas绘制自定义View", CustomView2DFragment()),
            Pair("SurfaceView 2DView演示", SurfaceViewFragment()),
            Pair("TextureView 2DView演示", TextureViewFragment()),
            Pair("GLSurfaceView 3D渲染演示", GLSurfaceViewFragment()),
        )
    }
}