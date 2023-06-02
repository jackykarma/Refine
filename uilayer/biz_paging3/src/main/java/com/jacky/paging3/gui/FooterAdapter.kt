package com.jacky.paging3.gui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jacky.biz_paging3.R

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/3, jacky.li, Create file
 * @since 2023/6/3
 * @version v1.0.00
 *
 * 使用 Kotlin 的高阶函数来给重试按钮注册点击事;替代Java中设置监听器回调的做法。
 */
class FooterAdapter(val retry: () -> Unit) : LoadStateAdapter<FooterAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.progressBar.isVisible = loadState is LoadState.Loading
        holder.retryButton.isVisible = loadState is LoadState.Error
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.footer_view, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.retryButton.setOnClickListener {
            retry()
        }
        return viewHolder
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 如果是正在加载中那么就显示加载进度条
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        // 如果是加载失败那么就显示重试按钮
        val retryButton: Button = itemView.findViewById(R.id.retry_button)
    }
}