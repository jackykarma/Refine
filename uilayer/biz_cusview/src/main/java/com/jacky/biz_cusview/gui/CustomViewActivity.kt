package com.jacky.biz_cusview.gui

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.basebiz.route.RouterConstant

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
        val text = TextView(this)
        text.text = "自定义View"
        text.textSize = 18f
        text.setTextColor(Color.parseColor("#FFAABB"))
        text.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setContentView(text)
    }
}