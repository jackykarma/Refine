package com.jacky.biz_cusview.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jacky.biz_cusview.R

/**
 * 演示2D Canvas绘制的自定义View
 */
class CustomView2DFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.fragment_2d_custom_view, container, false)
        return rootView
    }
}