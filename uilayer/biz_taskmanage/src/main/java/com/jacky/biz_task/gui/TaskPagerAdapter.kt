package com.jacky.biz_task.gui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter


class TaskPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    fm: FragmentManager
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return FG_TITLE.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Page1Fragment()
            1 -> Page2Fragment()
            2 -> Page3Fragment()
            3 -> Page4Fragment()
            else -> Page1Fragment()
        }
    }

    companion object {
        private val FG_TITLE = arrayListOf(
            "Page1", "Page2", "Page3", "Page4"
        )
    }
}