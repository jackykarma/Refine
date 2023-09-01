package com.jacky.biz_main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.jacky.basebiz.route.RouterConstant.Page
import com.jacky.biz_main.databinding.LayoutHomeBinding

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/5/31, jacky.li, Create file
 * @since 2023/5/31
 * @version v1.0.00
 */
@Route(group = Page.GROUP_NAME, path = Page.HOME_ACTIVITY)
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ARouter.getInstance().build(Page.CUSTOM_VIEW_ACTIVITY).navigation()
    }
}