package com.jacky.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jacky.paging3.entity.GitHubEntity
import com.jacky.paging3.net.GithubService

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 *
 * PagingSource 分页数据的数据源，用来从网络或者数据库中加载分页数据。
 *
 * PagingSource
 * 1.key:页面数据类型，无特殊要求用Int表示页面数就足够
 * 2.value:表示每一项数据所对应的对象类型，对应GitHubEntity
 */
class GithubPagingSource(private val githubService: GithubService) : PagingSource<Int, GitHubEntity>() {

    override fun getRefreshKey(state: PagingState<Int, GitHubEntity>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubEntity> {
        return try {
            // params.key代表当前的页数, key可能为null，若为null，默认将当前页设置为第一页
            val page = params.key ?: 1
            // params.loadSize 表示每一页包含多少条数据，这个数据的大小在页面配置中设置
            val pageSize = params.loadSize
            // 从网络数据源获取当前页所对应的数据
            val githubResponse = githubService.searchRepos(page, pageSize)
            val items = githubResponse.items
            // 上次加载的页
            val prevKey = if (page > 1) page - 1 else null
            // 若下一页有数据就加载下一页，若没有则不加载
            val nextKey = if (items.isNotEmpty()) page + 1 else null
            LoadResult.Page(items, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}