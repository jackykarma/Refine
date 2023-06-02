package com.jacky.paging3.net

import com.jacky.paging3.entity.GitHubResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Copyright (C)  2022 Jacky夜雨
 * Description
 * @author jacky.li
 * 2023/6/1, jacky.li, Create file
 * @since 2023/6/1
 * @version v1.0.00
 */
interface GithubService {

    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos(@Query("page") page: Int, @Query("per_page") perPage: Int): GitHubResponse

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
        }
    }
}