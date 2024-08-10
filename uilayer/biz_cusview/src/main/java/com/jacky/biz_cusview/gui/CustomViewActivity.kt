package com.jacky.biz_cusview.gui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.bizcommon.route.RouterConstant
import com.jacky.biz_cusview.R
import com.jacky.biz_cusview.fragment.CordinateSystemFragment

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/15, jacky.li, Create file
 * @since 2023/6/15
 * @version v1.0.00
 */
@Route(group = RouterConstant.Page.GROUP_NAME, path = RouterConstant.Page.CUSTOM_VIEW_ACTIVITY)
class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_custom_view)
        showFragment(CordinateSystemFragment())
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fg_container, fragment)
            .commitNowAllowingStateLoss()
    }
}