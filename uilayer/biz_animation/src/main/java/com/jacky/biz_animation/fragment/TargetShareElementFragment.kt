package com.jacky.biz_animation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jacky.biz_animation.R
import com.jacky.bizcommon.ui.BaseFragment

class TargetShareElementFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_target_share_element, container, false)
        initViews(rootView)
        return rootView
    }

    override fun initViews(view: View?) {
    }
}