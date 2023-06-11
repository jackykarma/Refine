package com.jacky.biz_task.gui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jacky.biz_task.vm.Page4ViewModel
import com.jacky.biz_taskmanager.databinding.LayoutPage4Binding

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/10, jacky.li, Create file
 * @since 2023/6/10
 * @version v1.0.00
 */
class Page4Fragment : Fragment() {

    private var page4ViewModel: Page4ViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutPage4Binding.inflate(inflater, container, false).root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page4ViewModel = ViewModelProvider(this)[Page4ViewModel::class.java]
    }
}