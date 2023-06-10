package com.jacky.paging3

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jacky.paging3.entity.GitHubEntity
import com.jacky.paging3.net.GithubService
import kotlinx.coroutines.flow.Flow

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 */
object DataRepository {

    // 设置每次/每页期望加载的数据量
    private const val PAGE_SIZE = 100

    private val githubService = GithubService.create()

    private val pageConfig = PagingConfig(
        /* 每次分页的时候需要加载的数量；一般是visibleRange屏幕可见数量的数倍。
        一次从SQLite加载超过2MB要尽力避免，因为这会产生额外的成本；
        如果你正在为占据大部分屏幕的非常大的社交媒体风格的卡片加载数据，并且你的数据库不是瓶颈，10-20可能不错。
        如果你在平铺网格中显示数十个项目，这可以更快地在滚动过程中显示项目，可以考虑接近100个。 */
        pageSize = PAGE_SIZE,

        // 告诉PageList距离屏幕底部还有几个item的时候就开始加载下一页。理论上应该是屏幕上可见Item数量的数倍。
        // 如下设置是:当滑动到距离屏幕底部还有50个item的时候就开始加载下一页数据。
        // 如果没有设置，默认就是PAGE_SIZE大小。
        // 如果不想要加载完上下数据立马加载下一页，可以把 initialLoadSize调整为比pageSize稍微大一点即可。
        prefetchDistance = 50,

        // 启用占位图、占位符；当且仅当咱们把这个enablePlaceholders置为true并且咱们告诉了datasource一共有多少条
        // 数据可以加载的时候，那么才会起作用。如果我们告诉这个datasource一共有100条数据，然后初始的时候加载了10条，
        // 那么当咱们滑动到第11条、12条、13条的时候；由于数据还未被加载完成，那么这个PageList会把剩下的这些数据用
        // 占位符来代替。
        enablePlaceholders = true,

        // 第一次加载数据的时候，加载的数量。初始加载量，默认是PAGE_SIZE的3倍
        initialLoadSize = PAGE_SIZE,

        // 列表总共有多少条数据，默认是无限制取到整型的最大值；在国内来说，一般我们是不可能知道这个列表一共有多少条的。
        // 如果你真的知道一共有多少条，那么可以设置maxSize = list.count
        // maxSize = Int.MAX_VALUE,
    )

    /**
     * 除了GitHubEntity可以修改，其他部分都是固定的。
     */
    fun getPagingData(): Flow<PagingData<GitHubEntity>> {
        // 先创建Pager对象，再构建一个Flow对象
        return Pager(
            config = pageConfig,
            pagingSourceFactory = {
                GithubPagingSource(githubService)
            }
        ).flow
    }
}