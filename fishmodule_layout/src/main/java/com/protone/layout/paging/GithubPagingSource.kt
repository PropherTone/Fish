package com.protone.layout.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.protone.common.baseType.withDefaultContext
import com.protone.common.utils.json.toEntity
import com.protone.layout.entity.GithubHot
import com.protone.layout.entity.Item
import com.protone.layout.viewModel.GithubViewModel

class GithubPagingSource(private val api: PagingDataAPI.API) : PagingSource<Int, Item>() {

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return runCatching {
            (params.key ?: 1).let { page ->
                val response = withDefaultContext {
                    api.searchRepositories(perPage = params.loadSize, page = page).execute()
                }
                if (response.isSuccessful) {
                    response.body()?.bytes()?.let {
                        val githubHot = String(it).toEntity(GithubHot::class.java)
                        LoadResult.Page(
                            githubHot.items,
                            if (page > 1) page - 1 else null,
                            if (githubHot.items.isNotEmpty()) page + 1 else null
                        )
                    }
                } else {
                    throw NullPointerException()
                }
            }
        }.let {
            it.getOrNull() ?: LoadResult.Error(it.exceptionOrNull() ?: NullPointerException())
        }
    }

}