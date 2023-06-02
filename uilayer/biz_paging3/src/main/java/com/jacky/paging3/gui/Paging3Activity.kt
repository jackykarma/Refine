package com.jacky.paging3.gui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.jacky.basebiz.route.RouterConstant
import com.jacky.biz_paging3.R
import kotlinx.coroutines.launch

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 *
 * Paging 3 将一些琐碎的细节进行了隐藏，比如你不需要监听列表的滑动事件，也不需要知道知道何时应该加载下一页的数据，
 * 这些都被 Paging 3 封装掉了。我们只需要按照 Paging 3 搭建好的框架去编写逻辑实现，告诉 Paging 3 如何去加载数据，
 * 其他的事情 Paging 3 都会帮我们自动完成。
 *
 * 根据 Paging 3 的设计，其实我们理论上是不应该在底部看到加载状态的。因为 Paging 3 会在列表还远没有滑动到底部的时候
 * 就提前加载更多的数据（这是默认属性，可配置），从而产生一种好像永远滑不到头的感觉。
 *
 * 然而凡事总有意外，比如说当前的网速不太好，虽然 Paging 3 会提前加载下一页的数据，但是当滑动到列表底部的时候，
 * 服务器响应的数据可能还没有返回，这个时候就应该在底部显示一个正在加载的状态。另外，如果网络条件非常糟糕，还可能会出现
 * 加载失败的情况，此时应该在列表底部显示一个重试按钮。
 */
@Route(group = RouterConstant.Page.GROUP_NAME, path = RouterConstant.Page.PAGING3_ACTIVITY)
class Paging3Activity : AppCompatActivity() {

    private lateinit var listView: RecyclerView

    private val viewModel by lazy { ViewModelProvider(this)[Paging3ViewModel::class.java] }

    private val githubAdapter = GithubAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_paging3)
        listView = findViewById(R.id.list_view)
        listView.adapter = githubAdapter.withLoadStateFooter(FooterAdapter {
            // PagingDataAdapter本身提供了重试方法
            githubAdapter.retry()
        })
        listView.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launch {
            // 获取页面数据并给到adapter适配器;
            // collect() 函数是一个挂起函数，只有在协程作用域中才能调用它
            viewModel.getPagingData().collect {
                // 触发Paging3分页功能的核心函数
                githubAdapter.submitData(it)
            }
        }

        githubAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    Log.d(TAG, "loadPageData not loading")
                }

                is LoadState.Loading -> {
                    Log.d(TAG, "loadPageData loading")
                }

                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    Log.e(TAG, "loadPageData error: ${state.error.message}")
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val TAG = "Paging3Activity"
    }
}