package com.protone.layout.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.protone.common.entity.BaseResponse
import com.protone.layout.entity.GithubHot
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class PagingDataAPI {

    companion object {
        const val BASE_URL = "https://api.github.com"
    }

    val api by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .callTimeout(3000L, TimeUnit.MILLISECONDS)
                    .connectTimeout(3000L, TimeUnit.MILLISECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java)
    }

    interface API {

        //?sort=stars&q=Android&per_page=5&page=1
        @GET("/search/repositories")
        fun searchRepositories(
            @Query("sort") query: String = "stars",
            @Query("q") q: String = "Android",
            @Query("per_page") perPage: Int,
            @Query("page") page: Int
        ): Call<ResponseBody>

    }

    fun getPagingSource(pageSize: Int) =
        Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { GithubPagingSource(api) }
        ).flow

}