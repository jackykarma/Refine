package com.jacky.biz_cusview.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jacky.biz_cusview.R

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description 演示Android中坐标系统
 * @author jacky.li
 * 2023/9/1, jacky.li, Create file
 * @since 2023/9/1
 * @version v1.0.00
 */
class CordinateSystemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.layout_cordinate_system, container, false)
        return rootView
    }
}