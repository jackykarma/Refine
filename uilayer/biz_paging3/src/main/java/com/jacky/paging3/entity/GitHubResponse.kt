package com.jacky.paging3.entity

import com.google.gson.annotations.SerializedName

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 */
data class GitHubResponse(@SerializedName("items") val items: List<GitHubEntity> = emptyList())