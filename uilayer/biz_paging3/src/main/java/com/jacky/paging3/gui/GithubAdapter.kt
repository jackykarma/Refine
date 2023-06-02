package com.jacky.paging3.gui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jacky.biz_paging3.R
import com.jacky.paging3.entity.GitHubEntity

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/2, jacky.li, Create file
 * @since 2023/6/2
 * @version v1.0.00
 *
 * 数据源是由 Paging 3 在内部自己管理的。同时也不需要重写 getItemCount() 函数了，
 * 原因也是相同的，有多少条数据 Paging 3 自己就能够知道。
 */
class GithubAdapter : PagingDataAdapter<GitHubEntity, GithubAdapter.ViewHolder>(COMPARATOR){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_text)
        val description: TextView = itemView.findViewById(R.id.description_text)
        val starCount: TextView = itemView.findViewById(R.id.star_count_text)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gitHubEntity = getItem(position)
        gitHubEntity?.let {
            holder.name.text = gitHubEntity.name
            holder.description.text = gitHubEntity.description
            holder.starCount.text = gitHubEntity.starCount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.github_item, parent, false)
        return ViewHolder(itemView)
    }

    companion object {
        // Paging 3 在内部会使用 DiffUtil 来管理数据变化，所以这个 COMPARATOR 是必须的。
        private val COMPARATOR = object : DiffUtil.ItemCallback<GitHubEntity>() {
            override fun areItemsTheSame(oldItem: GitHubEntity, newItem: GitHubEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GitHubEntity, newItem: GitHubEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}