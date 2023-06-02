package com.jacky.paging3.gui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jacky.paging3.DataRepository
import com.jacky.paging3.entity.GitHubEntity
import kotlinx.coroutines.flow.Flow

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/2, jacky.li, Create file
 * @since 2023/6/2
 * @version v1.0.00
 */
class Paging3ViewModel : ViewModel(){

    fun getPagingData() : Flow<PagingData<GitHubEntity>> {
        // cachedIn是将服务器返回的数据在viewModelScope这个作用域内进行缓存;
        // 假如手机横竖屏发生旋转导致Activity重建，Paging3就可以直接读取缓存中的数据，而不用重新发送网络请求了。
        return DataRepository.getPagingData().cachedIn(viewModelScope)
    }
}